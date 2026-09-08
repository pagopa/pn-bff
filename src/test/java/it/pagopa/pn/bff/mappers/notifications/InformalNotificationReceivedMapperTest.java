package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalTimelineElementV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.SenderContactInfo;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import it.pagopa.pn.bff.mocks.InformalNotificationDetailMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InformalNotificationReceivedMapperTest {

    private final InformalNotificationDetailMock mock = new InformalNotificationDetailMock();

    @Test
    void testMapNotificationNull() {
        BffFullInformalNotificationV1 result =
                InformalNotificationReceivedMapper.modelMapper.mapReceivedInformalNotificationDetail(null);
        assertNull(result);
    }

    @Test
    void testMapReceivedInformalNotificationDetail() {
        FullReceivedInformalNotificationV1 notification = mock.getInformalNotificationMock();

        BffFullInformalNotificationV1 result =
                InformalNotificationReceivedMapper.modelMapper.mapReceivedInformalNotificationDetail(notification);

        assertNotNull(result);
        assertEquals(notification.getIun(), result.getIun());
        assertEquals(notification.getSenderDenomination(), result.getSenderDenomination());
        assertEquals(notification.getCampaignId(), result.getCampaignId());
        assertEquals(notification.getSubject(), result.getSubject());
        assertEquals(notification.getGroup(), result.getGroup());
        assertEquals(notification.getSentAt(), result.getSentAt());
        // fields added on the BFF schema
        assertEquals(InformalNotificationDetailMock.SENDER_PA_ID, result.getSenderPaId());
        assertEquals(Boolean.TRUE, result.getDocumentsAvailable());

        // timeline
        List<InformalTimelineElementV1> sourceTimeline = notification.getTimeline();
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalTimelineElementV1> mappedTimeline =
                result.getTimeline();

        assertNotNull(mappedTimeline);
        assertEquals(sourceTimeline.size(), mappedTimeline.size());

        InformalTimelineElementV1 sourceRequestAccepted = sourceTimeline.get(1);
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalTimelineElementV1 mappedRequestAccepted =
                mappedTimeline.get(1);

        assertEquals(sourceRequestAccepted.getElementId(), mappedRequestAccepted.getElementId());
        assertNotNull(sourceRequestAccepted.getCategory());
        assertEquals(sourceRequestAccepted.getCategory().getValue(), mappedRequestAccepted.getCategory().getValue());
        assertEquals(sourceRequestAccepted.getEventTimestamp(), mappedRequestAccepted.getEventTimestamp());
        assertNotNull(mappedRequestAccepted.getDetails());
        assertEquals(InformalNotificationDetailMock.NOTIFICATION_REQUEST_ID,
                mappedRequestAccepted.getDetails().getNotificationRequestId());
        assertEquals(InformalNotificationDetailMock.PA_PROTOCOL_NUMBER,
                mappedRequestAccepted.getDetails().getPaProtocolNumber());
        assertEquals(InformalNotificationDetailMock.IDEMPOTENCE_TOKEN,
                mappedRequestAccepted.getDetails().getIdempotenceToken());
    }

    @Test
    void testFiledAtFromRequestAcceptedTimelineElement() {
        FullReceivedInformalNotificationV1 notification = mock.getInformalNotificationMock();

        BffFullInformalNotificationV1 result =
                InformalNotificationReceivedMapper.modelMapper.mapReceivedInformalNotificationDetail(notification);

        assertNotNull(result);
        assertEquals(InformalNotificationDetailMock.FILED_AT, result.getFiledAt());
    }

    @Test
    void testFiledAtNullWhenNoRequestAcceptedElement() {
        FullReceivedInformalNotificationV1 notification = mock.getInformalNotificationWithoutRequestAcceptedMock();

        BffFullInformalNotificationV1 result =
                InformalNotificationReceivedMapper.modelMapper.mapReceivedInformalNotificationDetail(notification);

        assertNotNull(result);
        assertNull(result.getFiledAt());
    }

    @Test
    void testMapSenderContacts() {
        SenderContactInfo senderContacts = new SenderContactInfo()
                .senderId(InformalNotificationDetailMock.SENDER_PA_ID)
                .email("sender@example.com")
                .pec("sender@pec.example.com")
                .phone("+390212345678")
                .site("https://example.com");

        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.SenderContactInfo result =
                InformalNotificationReceivedMapper.modelMapper.mapSenderContacts(senderContacts);

        assertNotNull(result);
        assertEquals(senderContacts.getSenderId(), result.getSenderId());
        assertEquals(senderContacts.getEmail(), result.getEmail());
        assertEquals(senderContacts.getPec(), result.getPec());
        assertEquals(senderContacts.getPhone(), result.getPhone());
        assertEquals(senderContacts.getSite(), result.getSite());
    }
}
