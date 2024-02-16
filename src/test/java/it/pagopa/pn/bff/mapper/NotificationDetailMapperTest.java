package it.pagopa.pn.bff.mapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullSentNotificationV1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationDetailMapperTest {
    @Test
    void testMapNotificationDetail() {
        FullReceivedNotificationV23 notification = new FullReceivedNotificationV23();
        BffFullReceivedNotificationV1 actualMapNotificationDetailResult = NotificationDetailMapper.modelMapper.mapNotificationDetail(notification);
        assertNotNull(actualMapNotificationDetailResult);

        BffFullReceivedNotificationV1 mapNotificationNull = NotificationDetailMapper.modelMapper.mapNotificationDetail(null);
        assertNull(mapNotificationNull);
    }

    @Test
    void testMapSentNotificationDetail() {
        FullSentNotificationV23 notification = new FullSentNotificationV23();
        BffFullSentNotificationV1 actualMapSentNotificationDetailResult = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notification);
        assertNotNull(actualMapSentNotificationDetailResult);

        BffFullSentNotificationV1 mapSentNotificationNull = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(null);
        assertNull(mapSentNotificationNull);
    }
}