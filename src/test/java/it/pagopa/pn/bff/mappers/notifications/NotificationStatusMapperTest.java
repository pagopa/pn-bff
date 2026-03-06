package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.NotificationStatusV26;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NotificationStatusMapperTest {

    @Test
    void testConvertDeliveryWebPANotificationStatus() {
        NotificationStatusV26 notificationStatus = NotificationStatusV26.ACCEPTED;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatusV26 actualConvertDeliveryWebPANotificationStatusResult = NotificationStatusMapper.notificationStatusMapper.convertDeliveryWebPANotificationStatus(notificationStatus);
        assertNotNull(actualConvertDeliveryWebPANotificationStatusResult);
        assertEquals(actualConvertDeliveryWebPANotificationStatusResult.getValue(), notificationStatus.getValue());
    }

    @Test
    void testConvertDeliveryRecipientNotificationStatus() {
        NotificationStatusV26 notificationStatus = NotificationStatusV26.ACCEPTED;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26 actualConvertDeliveryRecipientNotificationStatusResult = NotificationStatusMapper.notificationStatusMapper.convertDeliveryRecipientNotificationStatus(notificationStatus);
        assertNotNull(actualConvertDeliveryRecipientNotificationStatusResult);
        assertEquals(actualConvertDeliveryRecipientNotificationStatusResult.getValue(), notificationStatus.getValue());
    }
}