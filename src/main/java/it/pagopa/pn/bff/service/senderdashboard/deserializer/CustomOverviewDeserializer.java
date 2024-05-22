package it.pagopa.pn.bff.service.senderdashboard.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardNotificationOverview;

import java.io.IOException;
import java.time.LocalDate;

public class CustomOverviewDeserializer extends JsonDeserializer<BffSenderDashboardNotificationOverview> {

    @Override
    public BffSenderDashboardNotificationOverview deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Normalize fields
        String senderId = node.get("sender_id").asText();
        LocalDate notificationSendDate = LocalDate.parse(node.get("notification_send_date").asText());
        String notificationRequestStatus = node.get("notification_request_status").asText();
        String notificationStatus = node.get("notification_status").asText();
        String notificationType = node.get("notification_type").asText();
        String statusDigitalDelivery = node.get("status_digital_delivery").asText();
        String notificationDelivered = node.get("notification_delivered").asText();
        String notificationViewed = node.get("notification_viewed").asText();
        String notificationRefined = node.get("notification_refined").asText();
        int attemptCountPerDigitalNotification = normalizeInt(
                node.get("attempt_count_per_digital_notification").textValue());
        int notificationsCount = normalizeInt(node.get("notifications_count").textValue());
        double deliveryTime = normalizeDouble(node.get("delivery_time").textValue());
        double viewTime = normalizeDouble(node.get("view_time").textValue());
        double refinementTime = normalizeDouble(node.get("refinement_time").textValue());
        double validationTime = normalizeDouble(node.get("validation_time").textValue());

        // Create the object and set fields
        BffSenderDashboardNotificationOverview record = BffSenderDashboardNotificationOverview.builder()
                .senderId(senderId)
                .notificationSendDate(notificationSendDate)
                .notificationRequestStatus(notificationRequestStatus)
                .notificationStatus(notificationStatus)
                .notificationType(notificationType)
                .statusDigitalDelivery(statusDigitalDelivery)
                .notificationDelivered(notificationDelivered)
                .notificationViewed(notificationViewed)
                .notificationRefined(notificationRefined)
                .attemptCountPerDigitalNotification(attemptCountPerDigitalNotification)
                .notificationsCount(notificationsCount)
                .deliveryTime(deliveryTime)
                .viewTime(viewTime)
                .refinementTime(refinementTime)
                .validationTime(validationTime)
                .build();

        return record;
    }

    private static int normalizeInt(String value) {
        return Integer.parseInt(value.replace(".", ""));
    }

    private static double normalizeDouble(String value) {
        return Double.parseDouble(value.replace(".", "").replace(",", "."));
    }
}