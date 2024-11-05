package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NewNotificationRequestV24;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NewNotificationResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNewNotificationRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNewNotificationResponse;
import it.pagopa.pn.bff.mocks.NewSentNotificationMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NewSentNotificationMapperTest {
    private final NewSentNotificationMock newSentNotificationMock = new NewSentNotificationMock();

    @Test
    void testNewNotificationRequestMapper() {
        NewNotificationRequestV24 request = newSentNotificationMock.getNewSentNotificationRequest();
        BffNewNotificationRequest bffRequest = newSentNotificationMock.getBffNewSentNotificationRequest();
        NewNotificationRequestV24 mappedRequest = NewSentNotificationMapper.modelMapper.mapRequest(bffRequest);
        assertNotNull(mappedRequest);
        assertEquals(mappedRequest.getCancelledIun(), request.getCancelledIun());
        assertEquals(mappedRequest.getPaProtocolNumber(), request.getPaProtocolNumber());
        assertEquals(mappedRequest.getSubject(), request.getSubject());
        assertEquals(mappedRequest.getAbstract(), request.getAbstract());
        assertThat(mappedRequest.getRecipients()).usingRecursiveComparison().isEqualTo(request.getRecipients());
        assertThat(mappedRequest.getDocuments()).usingRecursiveComparison().isEqualTo(request.getDocuments());
        assertEquals(mappedRequest.getNotificationFeePolicy().getValue(), request.getNotificationFeePolicy().getValue());
        assertEquals(mappedRequest.getPhysicalCommunicationType().getValue(), request.getPhysicalCommunicationType().getValue());
        assertEquals(mappedRequest.getSenderDenomination(), request.getSenderDenomination());
        assertEquals(mappedRequest.getSenderTaxId(), request.getSenderTaxId());
        assertEquals(mappedRequest.getGroup(), request.getGroup());
        assertEquals(mappedRequest.getTaxonomyCode(), request.getTaxonomyCode());
        assertNull(mappedRequest.getAmount());
        assertNull(mappedRequest.getIdempotenceToken());
        assertNull(mappedRequest.getPaFee());
        assertNull(mappedRequest.getPagoPaIntMode());
        assertNull(mappedRequest.getPaymentExpirationDate());
        assertNull(mappedRequest.getVat());

        NewNotificationRequestV24 mappedRequestNull = NewSentNotificationMapper.modelMapper.mapRequest(null);
        assertNull(mappedRequestNull);
    }

    @Test
    void testNewNotificationResponseMapper() {
        NewNotificationResponse response = newSentNotificationMock.getNewSentNotificationResponse();

        BffNewNotificationResponse bffResponse = NewSentNotificationMapper.modelMapper.mapResponse(response);
        assertNotNull(bffResponse);
        assertEquals(bffResponse.getNotificationRequestId(), response.getNotificationRequestId());
        assertEquals(bffResponse.getIdempotenceToken(), response.getIdempotenceToken());
        assertEquals(bffResponse.getPaProtocolNumber(), response.getPaProtocolNumber());


        BffNewNotificationResponse bffResponseNull = NewSentNotificationMapper.modelMapper.mapResponse(null);
        assertNull(bffResponseNull);
    }
}