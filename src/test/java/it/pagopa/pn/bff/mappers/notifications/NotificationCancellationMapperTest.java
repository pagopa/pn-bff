package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.RequestStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffRequestStatus;
import it.pagopa.pn.bff.mocks.NotificationsSentMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationCancellationMapperTest {
    private final NotificationsSentMock notificationsSentMock = new NotificationsSentMock();

    @Test
    void testNotificationCancellationMapper() {
        RequestStatus notificationMock = notificationsSentMock.notificationCancellationPNMock();

        BffRequestStatus bffRequestStatus = NotificationCancellationMapper.modelMapper.mapNotificationCancellation(notificationMock);
        assertNotNull(bffRequestStatus);
        assertEquals(bffRequestStatus.getStatus(), notificationMock.getStatus());

        for (int i = 0; i < notificationMock.getDetails().size(); i++) {
            assertEquals(bffRequestStatus.getDetails().get(i).getCode(), notificationMock.getDetails().get(i).getCode());
            assertEquals(bffRequestStatus.getDetails().get(i).getLevel(), notificationMock.getDetails().get(i).getLevel());
            assertEquals(bffRequestStatus.getDetails().get(i).getDetail(), notificationMock.getDetails().get(i).getDetail());
        }

        BffRequestStatus bffRequestStatusNull = NotificationCancellationMapper.modelMapper.mapNotificationCancellation(null);
        assertNull(bffRequestStatusNull);
    }

}