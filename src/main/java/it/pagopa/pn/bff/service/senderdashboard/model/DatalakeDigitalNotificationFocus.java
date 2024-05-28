package it.pagopa.pn.bff.service.senderdashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DatalakeDigitalNotificationFocus {
    @JsonProperty("sender_id")
    public String senderId;

    @JsonProperty("notification_send_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDate notificationSendDate;

    @JsonProperty("error_type")
    public String errorType;

    @JsonProperty("failed_attempts_count")
    public String failedAttemptsCount;

    @JsonProperty("notifications_count")
    public String notificationsCount;
}
