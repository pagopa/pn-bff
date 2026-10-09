package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalNotificationStatusV1;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InformalNotificationStatusMapperTest {

    @ParameterizedTest
    @EnumSource(value = InformalNotificationStatusV1.class)
    void testConvertDeliveryInformalPAWebNotificationStatus(InformalNotificationStatusV1 notificationStatus) {
        List<it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationStatusV1> result =
                InformalNotificationStatusMapper.informalNotificationStatusMapper
                        .convertDeliveryInformalPAWebNotificationStatus(List.of(notificationStatus));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(notificationStatus.getValue(), result.get(0).getValue());
    }
}