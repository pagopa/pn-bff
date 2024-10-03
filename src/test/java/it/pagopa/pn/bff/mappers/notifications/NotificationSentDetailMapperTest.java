package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV24;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationSentDetailMapperTest {

    @Test
    void testSentNotificationDetailMapper() {
        FullSentNotificationV24 notification = new FullSentNotificationV24();
        BffFullNotificationV1 actualMapSentNotificationDetailResult = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);
        assertNotNull(actualMapSentNotificationDetailResult);

        BffFullNotificationV1 mapSentNotificationNull = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(null);
        assertNull(mapSentNotificationNull);
    }
}