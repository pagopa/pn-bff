package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.LegalNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffLegalNotificationsResponse;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationsReceivedMapperTest {
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();

    @Test
    void testNotificationReceivedMapper() {
        FullNotificationSearchResponse notificationSearchResponse = notificationsReceivedMock.getNotificationReceivedPNMock();

        BffFullNotificationsResponse bffNotificationsResponse = NotificationsReceivedMapper.modelMapper.toBffFullNotificationsResponse(notificationSearchResponse);
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

        // isNewNotification: row one has communicationOutcomes.viewed = true -> not new
        assertFalse(bffNotificationsResponse.getResultsPage().get(0).getIsNewNotification());
        // isNewNotification: row two has no communicationOutcomes and status ACCEPTED -> new
        assertTrue(bffNotificationsResponse.getResultsPage().get(1).getIsNewNotification());

        assertEquals(bffNotificationsResponse.getMoreResult(), notificationSearchResponse.getMoreResult());
        assertEquals(bffNotificationsResponse.getNextPagesKey(), notificationSearchResponse.getNextPagesKey());

        BffFullNotificationsResponse bffNotificationsResponseV1Null = NotificationsReceivedMapper.modelMapper.toBffFullNotificationsResponse(null);
        assertNull(bffNotificationsResponseV1Null);
    }

    @Test
    void testLegalNotificationReceivedMapper() {
        LegalNotificationSearchResponse legalNotificationSearchResponse = notificationsReceivedMock.getLegalNotificationsReceivedMock();

        BffLegalNotificationsResponse bffLegalNotificationsResponse = NotificationsReceivedMapper.modelMapper.toBffLegalNotificationsResponse(legalNotificationSearchResponse);
        assertNotNull(bffLegalNotificationsResponse);

        for (int i = 0; i < bffLegalNotificationsResponse.getResultsPage().size(); i++) {
            assertEquals(bffLegalNotificationsResponse.getResultsPage().get(i).getIun(), legalNotificationSearchResponse.getResultsPage().get(i).getIun());
            assertEquals(bffLegalNotificationsResponse.getResultsPage().get(i).getNotificationStatus().getValue(), legalNotificationSearchResponse.getResultsPage().get(i).getNotificationStatus().getValue());
        }

        // isNewNotification: row one has status VIEWED -> not new
        assertFalse(bffLegalNotificationsResponse.getResultsPage().get(0).getIsNewNotification());
        // isNewNotification: row two has status ACCEPTED -> new
        assertTrue(bffLegalNotificationsResponse.getResultsPage().get(1).getIsNewNotification());

        assertEquals(bffLegalNotificationsResponse.getMoreResult(), legalNotificationSearchResponse.getMoreResult());
        assertEquals(bffLegalNotificationsResponse.getNextPagesKey(), legalNotificationSearchResponse.getNextPagesKey());

        BffLegalNotificationsResponse bffLegalNotificationsResponseNull = NotificationsReceivedMapper.modelMapper.toBffLegalNotificationsResponse(null);
        assertNull(bffLegalNotificationsResponseNull);
    }
}
