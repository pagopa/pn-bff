package it.pagopa.pn.bff.service.senderdashboard.connectors;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.service.senderdashboard.model.IndexObject;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Instant;

/**
 * This class is responsible for managing the S3 client and retrieving index objects
 * from the S3 bucket for the sender dashboard.
 */
@Slf4j
@Lazy
@Component
@RequiredArgsConstructor
public class PnS3IndexResource {
    private static final long CACHE_EXPIRATION_MINUTES = 30;

    private final PnBffConfigs pnBffConfigs;
    private final ObjectMapper objectMapper;

    private S3Client s3Client;
    private String bucketName;
    private String indexKey;

    private volatile IndexObject cachedIndexObject;
    private volatile Instant lastUpdated;

    /**
     * Initializes the S3 client and retrieves configuration values from PnBffConfigs.
     */
    @PostConstruct
    public void init() {
        bucketName = pnBffConfigs.getPnBucketName();
        indexKey = pnBffConfigs.getPnIndexObjectKey();
        String bucketRegion = pnBffConfigs.getPnBucketRegion();

        // Create the S3 client
        s3Client = S3Client.builder()
                .region(Region.of(bucketRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Release resources.
     */
    @PreDestroy
    public void destroy() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    /**
     * Retrieves the index object from the S3 bucket, updating the cache if necessary.
     *
     * @return the IndexObject
     */
    public IndexObject getIndexObject() {
        if (isCacheExpired()) {
            refreshCache();
        }
        return cachedIndexObject;
    }

    /**
     * Checks if the cache is expired.
     *
     * @return true if the cache is expired, false otherwise
     */
    private boolean isCacheExpired() {
        return lastUpdated == null || Instant.now()
                .minusSeconds(CACHE_EXPIRATION_MINUTES * 60)
                .isAfter(lastUpdated);
    }

    /**
     * Refreshes the cache by retrieving the latest index object from S3.
     */
    private void refreshCache() {
        try {
            log.info("Refreshing cache by retrieving object from S3: s3://{}/{}", bucketName, indexKey);
            cachedIndexObject = fetchIndexObjectFromS3();
            lastUpdated = Instant.now();
        } catch (IOException e) {
            log.error("Error refreshing cache from S3:", e);
        }
    }

    /**
     * Fetch the index object from the S3 bucket.
     *
     * @return the IndexObject
     */
    private IndexObject fetchIndexObjectFromS3() throws IOException {
        log.info("Fetching object from S3: s3://{}/{}", bucketName, indexKey);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(indexKey)
                .build();
        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
            return objectMapper.readValue(s3Object, IndexObject.class);
        }
    }
}