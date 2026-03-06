package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.model.NotificationCostRecipientResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationCostDetails;
import it.pagopa.pn.bff.mocks.NotificationCostDetailsMock;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class NotificationCostDetailsMapperTest {

    private final NotificationCostDetailsMapper mapper =
            Mappers.getMapper(NotificationCostDetailsMapper.class);

    private final NotificationCostRecipientResponse source =
            new NotificationCostDetailsMock().getNotificationCostRecipientResponseMock();

    @Test
    void shouldMapCostDetails() {
        BffNotificationCostDetails result = mapper.mapCostDetails(source);

        assertNotNull(result);

        assertEquals(source.getTotalCost().getCostWithVat(), result.getTotalCost());
        assertEquals(source.getTotalCost().getDetails().getBaseCostDetail().getCost(), result.getBaseCost());
        assertEquals(source.getTotalCost().getDetails().getAnalogCostDetail().getCostWithVat(), result.getAnalogCost());
        assertEquals(
                source.getTotalCost().getDetails()
                        .getAnalogCostDetail()
                        .getAnalogCostComponents()
                        .size(),
                result.getNumberOfAnalogCost()
        );
    }

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        assertNull(mapper.mapCostDetails(null));
    }

    @Test
    void shouldReturnZeroWhenAnalogDetailIsNull() {
        source.getTotalCost()
                .getDetails()
                .setAnalogCostDetail(null);

        BffNotificationCostDetails result = mapper.mapCostDetails(source);

        assertNotNull(result);
        assertEquals(0, result.getNumberOfAnalogCost());
    }
}