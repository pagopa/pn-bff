package it.pagopa.pn.bff.service.senderdashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DatalakeNotificationOverview {

    @JsonProperty("sender_id")
    public String senderId;

    @JsonProperty("notification_send_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDate notificationSendDate;

    @JsonProperty("notification_request_status")
    public String notificationRequestStatus;

    @JsonProperty("notification_status")
    public String notificationStatus;

    @JsonProperty("notification_type")
    public String notificationType;

    @JsonProperty("status_digital_delivery")
    public String statusDigitalDelivery;

    @JsonProperty("notification_delivered")
    public String notificationDelivered;

    @JsonProperty("notification_viewed")
    public String notificationViewed;

    @JsonProperty("notification_refined")
    public String notificationRefined;

    @JsonProperty("attempt_count_per_digital_notification")
    public String attemptCountPerDigitalNotification;

    @JsonProperty("notifications_count")
    public String notificationsCount;

    @JsonProperty("delivery_time")
    public String deliveryTime;

    @JsonProperty("view_time")
    public String viewTime;

    @JsonProperty("refinement_time")
    public String refinementTime;

    @JsonProperty("validation_time")
    public String validationTime;
}