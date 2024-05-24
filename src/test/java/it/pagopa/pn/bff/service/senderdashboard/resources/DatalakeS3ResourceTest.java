package it.pagopa.pn.bff.service.senderdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.config.JacksonConfig;
import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.service.senderdashboard.exceptions.SenderNotFoundException;
import it.pagopa.pn.bff.service.senderdashboard.model.IndexObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PnBffConfigs.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(JacksonConfig.class)
public class DatalakeS3ResourceTest {
    @Autowired
    private PnBffConfigs pnBffConfigs;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private PnS3IndexResource pnS3IndexResource;
    @Mock
    private S3Client s3Client;
    @Mock
    private StsClient stsClient;

    @InjectMocks
    private DatalakeS3Resource datalakeS3Resource;

    @BeforeAll
    public void setup() throws IOException {
        InputStream indexResource = getResource("./senderdashboard/senderDashboardIndex_test.json");
        IndexObject indexObject = objectMapper.readValue(indexResource, IndexObject.class);
        when(pnS3IndexResource.getIndexObject()).thenReturn(indexObject);
        datalakeS3Resource = new DatalakeS3Resource(pnBffConfigs, objectMapper, pnS3IndexResource);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateCredentials() {
        // Arrange
        Instant expiration = Instant.now();
        mockStsClient(expiration);

        // Act
        datalakeS3Resource.updateCredentials();

        // Assert
        Instant credentialsExpiration =
                (Instant) ReflectionTestUtils.getField(datalakeS3Resource, "credentialsExpiration");
        assertEquals(credentialsExpiration, expiration);
    }

    @Test
    public void testGetDataResponse() throws IOException, SenderNotFoundException {
        // Arrange
        Instant expiration = Instant.now().plusSeconds(3 * 60 * 60);
        ReflectionTestUtils.setField(datalakeS3Resource, "credentialsExpiration", expiration);

        String senderId = "acb31680-55e6-4a3d-9355-b7531f55d11a";

        byte[] overviewBytes = readResourceAsBytes("./senderdashboard/export-notifications_overview_test.json");
        byte[] focusBytes = readResourceAsBytes("./senderdashboard/export-digital_notifications_focus_test.json");

        mockS3(overviewBytes, focusBytes);


        // Act
        var response = datalakeS3Resource.getDataResponse(senderId, null, null);

        // Assert
        assertEquals(response.getSenderId(), senderId);
        assertEquals(response.getEndDate(), response.getLastDate());
        assertEquals(response.getLastDate(), LocalDate.parse("2024-05-08"));
        assertEquals(response.getNotificationsOverview().size(), 658);
        assertEquals(response.getDigitalNotificationFocus().size(), 115);
    }

    @Test
    public void testGetDataResponseOnDates() throws IOException, SenderNotFoundException {
        // Arrange
        Instant expiration = Instant.now().plusSeconds(3 * 60 * 60);
        ReflectionTestUtils.setField(datalakeS3Resource, "credentialsExpiration", expiration);

        String senderId = "e955a1a1-86f0-4d7d-9c2a-f90783f6067c";

        byte[] overviewBytes = readResourceAsBytes("./senderdashboard/export-notifications_overview_test.json");
        byte[] focusBytes = readResourceAsBytes("./senderdashboard/export-digital_notifications_focus_test.json");

        mockS3(overviewBytes, focusBytes);

        LocalDate startDate = LocalDate.parse("2023-10-06");
        LocalDate endDate = LocalDate.parse("2024-04-24");

        // Act
        var response = datalakeS3Resource.getDataResponse(senderId, startDate, endDate);

        // Assert
        assertEquals(response.getSenderId(), senderId);
        assertEquals(response.getStartDate(), startDate);
        assertEquals(response.getEndDate(), endDate);
        assertEquals(response.getNotificationsOverview().size(), 1703);
        assertEquals(response.getDigitalNotificationFocus().size(), 277);
    }

    @Test
    public void testGetDataResponseSenderNotFound() {
        // Arrange
        Instant expiration = Instant.now().plusSeconds(3 * 60 * 60);
        ReflectionTestUtils.setField(datalakeS3Resource, "credentialsExpiration", expiration);

        String senderId = "sender_not_in_index";

        // Act & Assert
        assertThrows(SenderNotFoundException.class, () -> {
            datalakeS3Resource.getDataResponse(senderId, null, null);
        });
    }

    private void mockS3(byte[] overviewBytes, byte[] focusBytes) {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenAnswer(invocation -> {
            GetObjectRequest request = invocation.getArgument(0);
            String requestedKey = request.key();
            String range = request.range();
            byte[] data = null;

            if (requestedKey.equals("overviewTestKey")) {
                data = overviewBytes;
            } else if (requestedKey.equals("focusTestKey")) {
                data = focusBytes;
            }

            if (data != null && range != null) {
                // Parse the range header, e.g., "bytes=0-499"
                String[] parts = range.replace("bytes=", "").split("-");
                int start = Integer.parseInt(parts[0]);
                int end = parts.length > 1 ? Integer.parseInt(parts[1]) : data.length - 1;
                end = Math.min(end, data.length - 1);

                // Extract the requested byte range
                byte[] rangeData = Arrays.copyOfRange(data, start, end + 1);

                // Convert the byte array to an InputStream
                ByteArrayInputStream rangeInputStream = new ByteArrayInputStream(rangeData);
                return new ResponseInputStream<>(
                        GetObjectResponse.builder().build(), rangeInputStream
                );
            }

            if (data != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                return new ResponseInputStream<>(
                        GetObjectResponse.builder().build(), inputStream
                );
            }

            return null;
        });
    }

    private void mockStsClient(Instant expiration) {
        Credentials awsCredentials = Credentials.builder()
                .accessKeyId("test")
                .secretAccessKey("test")
                .sessionToken("test")
                .expiration(expiration)
                .build();
        AssumeRoleResponse roleResponse = AssumeRoleResponse.builder()
                .credentials(awsCredentials)
                .build();
        when(stsClient.assumeRole(any(AssumeRoleRequest.class))).thenReturn(roleResponse);
    }

    private static byte[] readResourceAsBytes(String resourcePath) throws IOException {
        try (InputStream inputStream = getResource(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return inputStream.readAllBytes();
        }
    }

    private static InputStream getResource(String src) {
        return Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(src);
    }

}
