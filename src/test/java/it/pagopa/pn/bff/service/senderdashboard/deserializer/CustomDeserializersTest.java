package it.pagopa.pn.bff.service.senderdashboard.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.config.JacksonConfig;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDigitalNotificationFocus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardNotificationOverview;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@Import(JacksonConfig.class)
public class CustomDeserializersTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testOverviewDeserialization() throws IOException {
        // Arrange
        InputStream indexResource = getResource("./senderdashboard/overviewTest.json");

        // Act
        var values = getLinesAsObjects(indexResource, BffSenderDashboardNotificationOverview.class);

        // Assert
        assertEquals(values.size(), 3354);

        indexResource.close();
    }

    @Test
    public void testFocusDeserialization() throws IOException {
        // Arrange
        InputStream indexResource = getResource("./senderdashboard/focusTest.json");

        // Act
        var values = getLinesAsObjects(indexResource, BffSenderDashboardDigitalNotificationFocus.class);

        // Assert
        assertEquals(values.size(), 577);

        indexResource.close();
    }

    private <E> List<E> getLinesAsObjects(InputStream in, Class<E> valueType) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<E> values = new LinkedList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith(",")) {
                line = removeLastCharacter(line);
            }
            try {
                var object = objectMapper.readValue(line, valueType);
                values.add(object);
            } catch (Exception e) {
                log.info("Not able to parse line, skip: {}", line);
            }
        }
        return values;
    }

    private static InputStream getResource(String src) {
        return Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(src);
    }

    private static String removeLastCharacter(String str) {
        if (str == null || str.isEmpty()) {
            return str; // Return the original string if it's null or empty
        }
        return str.substring(0, str.length() - 1);
    }
}
