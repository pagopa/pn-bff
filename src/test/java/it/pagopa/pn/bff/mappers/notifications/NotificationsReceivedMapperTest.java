package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationsReceivedMapperTest {
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();

    @Test
    void testNotificationReceivedMapper() {
        NotificationSearchResponse notificationSearchResponse = notificationsReceivedMock.getNotificationReceivedPNMock();

        BffNotificationsResponse bffNotificationsResponse = NotificationsReceivedMapper.modelMapper.toBffNotificationsResponse(notificationSearchResponse);
        assertNotNull(bffNotificationsResponse);

        for (int i = 0; i < bffNotificationsResponse.getResultsPage().size(); i++) {
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getIun(), notificationSearchResponse.getResultsPage().get(i).getIun());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getPaProtocolNumber(), notificationSearchResponse.getResultsPage().get(i).getPaProtocolNumber());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getSender(), notificationSearchResponse.getResultsPage().get(i).getSender());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getSentAt(), notificationSearchResponse.getResultsPage().get(i).getSentAt());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getSubject(), notificationSearchResponse.getResultsPage().get(i).getSubject());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getNotificationStatus().getValue(), notificationSearchResponse.getResultsPage().get(i).getNotificationStatus().getValue());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getRecipients(), notificationSearchResponse.getResultsPage().get(i).getRecipients());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getRequestAcceptedAt(), notificationSearchResponse.getResultsPage().get(i).getRequestAcceptedAt());
            assertEquals(bffNotificationsResponse.getResultsPage().get(i).getGroup(), notificationSearchResponse.getResultsPage().get(i).getGroup());
        }

        assertEquals(bffNotificationsResponse.getMoreResult(), notificationSearchResponse.getMoreResult());
        assertEquals(bffNotificationsResponse.getNextPagesKey(), notificationSearchResponse.getNextPagesKey());

        BffNotificationsResponse bffNotificationsResponseV1Null = NotificationsReceivedMapper.modelMapper.toBffNotificationsResponse(null);
        assertNull(bffNotificationsResponseV1Null);
    }
}