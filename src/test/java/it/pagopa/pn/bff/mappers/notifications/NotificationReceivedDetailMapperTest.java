package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV26;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationReceivedDetailMapperTest {
    @Test
    void testReceivedNotificationDetailMapper() {
        FullReceivedNotificationV26 notification = new FullReceivedNotificationV26();
        BffFullNotificationV1 actualMapNotificationDetailResult = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification);
        assertNotNull(actualMapNotificationDetailResult);

        BffFullNotificationV1 mapNotificationNull = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(null);
        assertNull(mapNotificationNull);
    }
}