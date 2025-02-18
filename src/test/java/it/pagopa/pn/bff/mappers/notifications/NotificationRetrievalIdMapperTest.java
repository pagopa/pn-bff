package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.RetrievalPayload;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCheckTPPResponse;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationRetrievalIdMapperTest {
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();

    @Test
    void testToBffCheckTPPResponse() {
        RetrievalPayload retrievalPayload = notificationsReceivedMock.getRetrievalPayloadMock();

        BffCheckTPPResponse response = NotificationRetrievalIdMapper.modelMapper.toBffCheckTPPResponse(retrievalPayload);
        assertNotNull(response);

        assertEquals(response.getRetrievalId(), retrievalPayload.getRetrievalId());
        assertEquals(response.getTppId(), retrievalPayload.getTppId());
        assertEquals(response.getDeeplink(), retrievalPayload.getDeeplink());
        assertEquals(response.getOriginId(), retrievalPayload.getOriginId());
        assertEquals(response.getPaymentButton(), retrievalPayload.getPaymentButton());

        BffCheckTPPResponse responseNull = NotificationRetrievalIdMapper.modelMapper.toBffCheckTPPResponse(null);
        assertNull(responseNull);
    }
}