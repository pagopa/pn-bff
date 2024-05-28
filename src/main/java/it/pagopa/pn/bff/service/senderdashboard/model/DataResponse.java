package it.pagopa.pn.bff.service.senderdashboard.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class DataResponse {
    public String senderId;
    public OffsetDateTime genTimestamp;
    public LocalDate lastDate;
    public LocalDate startDate;
    public LocalDate endDate;
    public List<DatalakeNotificationOverview> notificationsOverview = null;
    public List<DatalakeDigitalNotificationFocus> digitalNotificationFocus = null;
}
