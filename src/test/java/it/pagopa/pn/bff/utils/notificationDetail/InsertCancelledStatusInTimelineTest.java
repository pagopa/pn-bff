package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementCategoryV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatusHistory;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

public class InsertCancelledStatusInTimelineTest {

    @Test
    void insertCancelledStatusInTimeline() {
        FullReceivedNotificationV23 cancellationInProgressNotification = new FullReceivedNotificationV23();

        BeanUtils.copyProperties(NotificationDetailMock.getOneRecipientNotification(), cancellationInProgressNotification);

        cancellationInProgressNotification.getTimeline().add(
                NotificationDetailMock.getTimelineElem(
                        TimelineElementCategoryV23.NOTIFICATION_CANCELLATION_REQUEST,
                        null
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper
                .mapNotificationDetail(cancellationInProgressNotification);

        NotificationStatusHistory cancellationInProgressStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(NotificationStatus.CANCELLATION_IN_PROGRESS))
                        .findFirst()
                        .orElse(null);

        assert cancellationInProgressStatusHistory != null;

        assert calculatedParsedNotification.getNotificationStatus().equals(NotificationStatus.CANCELLATION_IN_PROGRESS);
    }
}
