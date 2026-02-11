package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationSentDetailMapperTest {

    @Test
    void testMapNotificationNull() {
        FullSentNotificationV28 notification = new FullSentNotificationV28();
        BffFullNotificationV1 actualMapSentNotificationDetailResult = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);
        assertNotNull(actualMapSentNotificationDetailResult);

        BffFullNotificationV1 mapSentNotificationNull = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(null);
        assertNull(mapSentNotificationNull);
    }

    @Test
    void testSentNotificationDetailMapper() {
        FullSentNotificationV28 notification = new FullSentNotificationV28();
        notification.setSenderPaId("sent-id-123");

        BffFullNotificationV1 result = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);

        assertNotNull(result);
        assertNotNull(result.getSenderPaId()); // <--- Aggiungiamo questo per coerenza con l'altro test
    }
}