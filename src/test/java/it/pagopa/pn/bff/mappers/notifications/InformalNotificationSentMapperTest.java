package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationV1;
import it.pagopa.pn.bff.mocks.InformalSentNotificationDetailMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformalNotificationSentMapperTest {

    private final InformalSentNotificationDetailMock mock = new InformalSentNotificationDetailMock();

    @Test
    void testMapNotificationNull() {
        BffFullSentInformalNotificationV1 result =
                InformalNotificationSentMapper.modelMapper.mapSentInformalNotificationDetail(null);
        assertNull(result);
    }

    @Test
    void testMapSentInformalNotificationDetail() {
        FullSentInformalNotificationV1 notification = mock.getFullSentInformalNotificationMock();

        BffFullSentInformalNotificationV1 result =
                InformalNotificationSentMapper.modelMapper.mapSentInformalNotificationDetail(notification);

        assertNotNull(result);
        assertEquals(notification.getIun(), result.getIun());
        assertEquals(notification.getSenderDenomination(), result.getSenderDenomination());
        assertEquals(notification.getSubject(), result.getSubject());
        assertEquals(notification.getDocumentsAvailable(), result.getDocumentsAvailable());
        assertEquals(notification.getNotificationStatus().getValue(), result.getNotificationStatus().getValue());

        assertNotNull(result.getRecipients());
        assertEquals(notification.getRecipients().size(), result.getRecipients().size());
        assertEquals(notification.getRecipients().get(0).getTaxId(), result.getRecipients().get(0).getTaxId());
        assertEquals(notification.getRecipients().get(0).getDenomination(), result.getRecipients().get(0).getDenomination());
        assertEquals(notification.getRecipients().get(0).getRecipientType().getValue(), result.getRecipients().get(0).getRecipientType().getValue());

        assertNotNull(result.getDocuments());
        assertEquals(notification.getDocuments().size(), result.getDocuments().size());
        assertEquals(notification.getDocuments().get(0).getTitle(), result.getDocuments().get(0).getTitle());
        assertEquals(notification.getDocuments().get(0).getDocIdx(), result.getDocuments().get(0).getDocIdx());
    }
}
