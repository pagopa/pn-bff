package it.pagopa.pn.bff.service.senderdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.config.JacksonConfig;
import it.pagopa.pn.bff.config.PnBffConfigs;
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
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PnBffConfigs.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(JacksonConfig.class)
public class PnS3IndexResourceTest {

    @Autowired
    private PnBffConfigs pnBffConfigs;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private PnS3IndexResource pnS3IndexResource;

    @BeforeAll
    public void setup() {
        pnS3IndexResource = new PnS3IndexResource(pnBffConfigs, objectMapper);
        pnS3IndexResource.init();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOverviewObject() {
        // Arrange
        mockS3GetObject("./senderdashboard/senderDashboardIndex_test.json");

        // Act
        IndexObject actualIndexObject = pnS3IndexResource.getIndexObject();

        // Assert
        assertEquals(actualIndexObject.getBucketName(), "testBucket");
        assertEquals(actualIndexObject.getBucketRegion(), "eu-south-1");
        assertEquals(actualIndexObject.getOverviewObjectKey(), "testKeyOverview");
        assertEquals(actualIndexObject.getFocusObjectKey(), "testKeyFocus");
        assertNull(actualIndexObject.getOverviewObjectVersionId());
        assertNull(actualIndexObject.getFocusObjectVersionId());
        assertEquals(actualIndexObject.getGenTimestamp(), OffsetDateTime.parse("2024-05-22T10:31:00.611Z"));
        assertEquals(actualIndexObject.getLastDate(), LocalDate.parse("2024-05-08"));
        assertEquals(actualIndexObject.getStartDate(), LocalDate.parse("2023-07-17"));
        assertEquals(actualIndexObject.getOverviewObjectSizeByte(), 1618696);
        assertEquals(actualIndexObject.getFocusObjectSizeByte(), 98669);
        assertEquals(actualIndexObject.getOverviewSendersId().size(), 12);
        assertEquals(actualIndexObject.getFocusSendersId().size(), 12);
    }

    @Test
    public void testGetOverviewObjectImmutability() {
        // Arrange
        mockS3GetObject("./senderdashboard/senderDashboardIndex_test.json");

        // Act
        IndexObject fisrtIndexObject = pnS3IndexResource.getIndexObject();
        mockS3GetObject("./senderdashboard/empty.json");
        IndexObject secondIndexObject = pnS3IndexResource.getIndexObject();

        // Assert
        assertEquals(fisrtIndexObject.getBucketName(), "testBucket");
        assertNull(secondIndexObject.getBucketName());
    }

    private void mockS3GetObject(String src) {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenAnswer(invocation -> {
            byte[] data = readResourceAsBytes(src);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            return new ResponseInputStream<>(
                    GetObjectResponse.builder().build(), inputStream
            );
        });
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