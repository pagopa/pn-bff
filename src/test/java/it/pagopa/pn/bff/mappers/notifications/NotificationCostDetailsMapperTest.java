package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.model.NotificationCostRecipientResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationCostDetails;
import it.pagopa.pn.bff.mocks.NotificationCostDetailsMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationCostDetailsMapperTest {
    private final NotificationCostDetailsMock notificationCostDetailsMock = new NotificationCostDetailsMock();

    @Test
    void testNotificationCostDetailsMapper() {
        NotificationCostRecipientResponse notificationCostRecipientMock = notificationCostDetailsMock.getNotificationCostRecipientResponseMock();

        BffNotificationCostDetails bffNotificationCostDetails =
                NotificationCostDetailsMapper.modelMapper.mapCostDetails(notificationCostRecipientMock);

        assertNotNull(bffNotificationCostDetails);

        assertEquals(bffNotificationCostDetails.getStatus().getValue(), BffNotificationCostDetails.StatusEnum.UNAVAILABLE.getValue());
        assertEquals(bffNotificationCostDetails.getTotalCost(), notificationCostRecipientMock.getTotalCost().getCostWithVat());
        assertEquals(bffNotificationCostDetails.getBaseCost(), notificationCostRecipientMock.getTotalCost().getDetails().getBaseCostDetail().getCost());
        assertEquals(bffNotificationCostDetails.getAnalogCost(), notificationCostRecipientMock.getTotalCost().getDetails().getAnalogCostDetail().getCostWithVat());
        assertEquals(
                notificationCostRecipientMock.getTotalCost().getDetails().getAnalogCostDetail().getAnalogCostComponents().size(),
                bffNotificationCostDetails.getNumberOfAnalogCost()
        );

        BffNotificationCostDetails bffNotificationCostDetailsNull =
                NotificationCostDetailsMapper.modelMapper.mapCostDetails(null);
        assertNull(bffNotificationCostDetailsNull);
    }
}