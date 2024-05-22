package it.pagopa.pn.bff.service.senderdashboard.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDigitalNotificationFocus;

import java.io.IOException;
import java.time.LocalDate;

public class CustomFocusDeserializer extends JsonDeserializer<BffSenderDashboardDigitalNotificationFocus> {

    @Override
    public BffSenderDashboardDigitalNotificationFocus deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Normalize fields
        String senderId = node.get("sender_id").asText();
        LocalDate notificationSendDate = LocalDate.parse(node.get("notification_send_date").asText());
        String errorType = node.get("error_type").asText();
        int failedAttemptsCount = normalizeInt(node.get("failed_attempts_count").textValue());
        int notificationsCount = normalizeInt(node.get("notifications_count").textValue());

        // Create the object and set fields
        BffSenderDashboardDigitalNotificationFocus record = BffSenderDashboardDigitalNotificationFocus.builder()
                .senderId(senderId)
                .notificationSendDate(notificationSendDate)
                .errorType(errorType)
                .failedAttemptsCount(failedAttemptsCount)
                .notificationsCount(notificationsCount)
                .build();

        return record;
    }

    private static int normalizeInt(String value) {
        return Integer.parseInt(value.replace(".", ""));
    }

}