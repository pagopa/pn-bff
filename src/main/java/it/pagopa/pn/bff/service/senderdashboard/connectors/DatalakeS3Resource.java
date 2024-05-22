package it.pagopa.pn.bff.service.senderdashboard.connectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDigitalNotificationFocus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardNotificationOverview;
import it.pagopa.pn.bff.service.senderdashboard.model.IndexObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is responsible for managing the S3 client and retrieving objects
 * from the data-lake S3 bucket for the sender dashboard.
 */
@Slf4j
@Lazy
@Component
@RequiredArgsConstructor
public class DatalakeS3Resource {
    private static final long CREDENTIAL_EXPIRATION_MARGIN_S = 600; // 10 minutes
    private static final String DEFAULT_STS_REGION = "eu-south-1";
    private static final String ROLE_SESSION_NAME = "BffSenderDashboardAssumeRole";

    private final PnBffConfigs pnBffConfigs;
    private final ObjectMapper objectMapper;
    private final PnS3IndexResource pnS3IndexResource;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private S3Client s3Client;
    private StsClient stsClient;
    private Instant credentialsExpiration;

    /**
     * Initializes the STS client and updates the AWS credentials creating
     * the S3 client.
     *
     * @throws IOException if there is an error initializing the client
     */
    @PostConstruct
    public void init() throws IOException {
        stsClient = StsClient.builder()
                .region(Region.of(DEFAULT_STS_REGION))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        updateCredentials();
    }

    /**
     * Releases resources by closing the S3 and STS clients.
     */
    @PreDestroy
    public void destroy() {
        lock.writeLock().lock();
        try {
            if (s3Client != null) {
                s3Client.close();
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (stsClient != null) {
            stsClient.close();
        }
    }

    /**
     * Retrieves sender dashboard data for the specified sender ID and date range.
     *
     * @param senderId the sender ID
     * @param startDate the start date of the range
     * @param endDate the end date of the range (included)
     * @return the sender dashboard data response
     */
    public BffSenderDashboardDataResponse getDataResponse(String senderId, LocalDate startDate, LocalDate endDate) {
        updateCredentialsIfNecessary();
        IndexObject indexObject = pnS3IndexResource.getIndexObject();
        String overviewObjectKey = pnBffConfigs.getDlOverviewObjectKey();
        String focusObjectKey = pnBffConfigs.getDlFocusObjectKey();

        // No senderId found
        IndexObject.SenderInfo focusIndex = indexObject.getFocusSendersId().get(senderId);
        IndexObject.SenderInfo overviewIndex = indexObject.getOverviewSendersId().get(senderId);
        if(overviewIndex == null){
            return null;
        }

        List<BffSenderDashboardNotificationOverview> overviewList;
        List<BffSenderDashboardDigitalNotificationFocus> focusList;

        lock.readLock().lock();
        try {
            // Assume that overviewListJson and focusListJson are ordered by notification_send_date
            // in descending order [ "2024-05-08", "2024-05-07", "2024-03-02", ...]
            overviewList = getObjectsAsStream(
                    overviewIndex.getStart(),
                    overviewIndex.getEnd(),
                    overviewObjectKey,
                    indexObject.getOverviewObjectVersionId(),
                    BffSenderDashboardNotificationOverview.class)
                    .takeWhile(o -> startDate == null || !o.getNotificationSendDate().isBefore(startDate))
                    .filter(o -> endDate == null || !o.getNotificationSendDate().isAfter(endDate))
                    .collect(Collectors.toList());


            if(focusIndex != null) {
                // Found senderId in focus
                focusList = getObjectsAsStream(
                        focusIndex.getStart(),
                        focusIndex.getEnd(),
                        focusObjectKey,
                        indexObject.getFocusObjectVersionId(),
                        BffSenderDashboardDigitalNotificationFocus.class)
                        .takeWhile(o -> startDate == null || !o.getNotificationSendDate().isBefore(startDate))
                        .filter(o -> endDate == null || !o.getNotificationSendDate().isAfter(endDate))
                        .collect(Collectors.toList());
            } else {
                focusList = new ArrayList<>();
            }

            LocalDate resStartDate = startDate == null ? indexObject.getStartDate() : startDate;
            LocalDate resEndDate = endDate == null ? indexObject.getLastDate() : endDate;

            // Default dates are taken from overview
            return BffSenderDashboardDataResponse.builder()
                    .senderId(senderId)
                    .genTimestamp(indexObject.getGenTimestamp())
                    .lastDate(indexObject.getLastDate())
                    .startDate(resStartDate)
                    .endDate(resEndDate)
                    .notificationsOverview(overviewList)
                    .digitalNotificationFocus(focusList)
                    .build();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Updates the AWS credentials by assuming the specified role.
     */
    public void updateCredentials() {
        log.info("Updating AWS credentials...");
        if(!pnBffConfigs.getDlAssumeRoleArn().equals("test")) {
            String assumeRoleArn = pnBffConfigs.getDlAssumeRoleArn();

            // Obtain credentials for the IAM role
            AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                    .roleArn(assumeRoleArn)
                    .roleSessionName(ROLE_SESSION_NAME)
                    .build();

            AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
            Credentials sessionCredentials = roleResponse.credentials();

            // Create AwsSessionCredentials object with the retrieved credentials
            AwsSessionCredentials awsCredentials = AwsSessionCredentials.create(
                    sessionCredentials.accessKeyId(),
                    sessionCredentials.secretAccessKey(),
                    sessionCredentials.sessionToken());

            // Provide temporary security credentials for the S3 clients
            AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);

            lock.writeLock().lock();
            try {
                // Close S3 client for a new instance
                try {
                    if (s3Client != null) {
                        s3Client.close();
                    }
                } catch (Exception e) {
                    log.error("Error closing S3 client:", e);
                }
                // Update expiration
                credentialsExpiration = sessionCredentials.expiration();
                // Create the updated S3 client
                s3Client = S3Client.builder()
                        .region(Region.of(pnBffConfigs.getDlBucketRegion()))
                        .credentialsProvider(credentialsProvider)
                        .build();
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            lock.writeLock().lock();
            try {
                credentialsExpiration = Instant.now().plusSeconds(60 * 60 * 2);
                s3Client = S3Client.builder()
                        .region(Region.of(pnBffConfigs.getPnBucketRegion()))
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();
            } finally {
                lock.writeLock().unlock();
            }
        }
        log.info("S3 client updated with new credentials. Credentials will expire at {}", credentialsExpiration);
    }

    /**
     * Updates the AWS credentials if they are expired or about to expire.
     */
    private void updateCredentialsIfNecessary() {
        lock.readLock().lock();
        try {
            if (credentialsExpiration != null &&
                    !credentialsExpiration.isBefore(Instant.now().plusSeconds(CREDENTIAL_EXPIRATION_MARGIN_S))) {
                log.debug("AWS credentials are still valid.");
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        updateCredentials();
    }

    /**
     * Retrieves objects from an S3 bucket as a stream.
     *
     * @param startByte the starting byte range
     * @param endByte the ending byte range
     * @param objectKey the key of the S3 object
     * @param versionId the version ID of the S3 object
     * @param valueType the class type of the objects to be deserialized
     * @param <E> the type of the objects to be deserialized
     * @return a stream of objects
     */
    private <E> Stream<E> getObjectsAsStream(long startByte, long endByte, String objectKey, String versionId, Class<E> valueType) {
        String bucketName = pnBffConfigs.getDlAssumeRoleArn().equals("test") ? pnBffConfigs.getPnBucketName() : pnBffConfigs.getDlBucketName();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .versionId(versionId)
                .range("bytes=" + startByte + "-" + endByte)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object));

        return reader.lines()
                .map(line -> {
                    if (line.endsWith(",")) {
                        line = removeLastCharacter(line);
                    }
                    try {
                        return objectMapper.readValue(line, valueType);
                    } catch (IOException e) {
                        log.info("Not able to parse line, skip: {}", line);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .onClose(() -> {
                    try {
                        reader.close();
                        s3Object.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Error closing resources", e);
                    }
                });
    }

    /**
     * Removes the last character from a string.
     *
     * @param str the original string
     * @return the string with the last character removed
     */
    private static String removeLastCharacter(String str) {
        if (str == null || str.isEmpty()) {
            return str; // Return the original string if it's null or empty
        }
        return str.substring(0, str.length() - 1);
    }
}