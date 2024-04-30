package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NotificationStatusMapperTest {

    @Test
    void testConvertDeliveryWebPANotificationStatus() {
        NotificationStatus notificationStatus = NotificationStatus.ACCEPTED;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus actualConvertDeliveryWebPANotificationStatusResult = NotificationStatusMapper.notificationStatusMapper.convertDeliveryWebPANotificationStatus(notificationStatus);
        assertNotNull(actualConvertDeliveryWebPANotificationStatusResult);
        assertEquals(actualConvertDeliveryWebPANotificationStatusResult.getValue(), notificationStatus.getValue());
    }
}
