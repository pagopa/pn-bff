package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PreLoadRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PreLoadResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPreLoadRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPreLoadResponse;
import it.pagopa.pn.bff.mocks.NewSentNotificationMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationSentPreloadDocumentsMapperTest {
    private final NewSentNotificationMock newSentNotificationMock = new NewSentNotificationMock();

    @Test
    void testPreLoadRequestMapper() {
        List<PreLoadRequest> request = newSentNotificationMock.getPreloadRequestMock();
        List<BffPreLoadRequest> bffRequest = newSentNotificationMock.getBffPreloadRequestMock();
        List<PreLoadRequest> mappedRequest = NotificationSentPreloadDocumentsMapper.modelMapper.mapRequest(bffRequest);
        assertNotNull(mappedRequest);
        for (int i = 0; i < mappedRequest.size(); i++) {
            assertEquals(mappedRequest.get(i).getPreloadIdx(), request.get(i).getPreloadIdx());
            assertEquals(mappedRequest.get(i).getContentType(), request.get(i).getContentType());
            assertEquals(mappedRequest.get(i).getSha256(), request.get(i).getSha256());
        }

        List<PreLoadRequest> mappedRequestNull = NotificationSentPreloadDocumentsMapper.modelMapper.mapRequest(null);
        assertNull(mappedRequestNull);
    }

    @Test
    void testPreLoadResponseMapper() {
        List<PreLoadResponse> response = newSentNotificationMock.getPreloadResponseMock();

        List<BffPreLoadResponse> bffResponse = response
                .stream()
                .map(NotificationSentPreloadDocumentsMapper.modelMapper::mapResponse)
                .toList();
        assertNotNull(bffResponse);
        assertEquals(bffResponse.size(), response.size());
        for (int i = 0; i < bffResponse.size(); i++) {
            assertEquals(bffResponse.get(i).getHttpMethod().getValue(), response.get(i).getHttpMethod().getValue());
            assertEquals(bffResponse.get(i).getKey(), response.get(i).getKey());
            assertEquals(bffResponse.get(i).getPreloadIdx(), response.get(i).getPreloadIdx());
            assertEquals(bffResponse.get(i).getSecret(), response.get(i).getSecret());
            assertEquals(bffResponse.get(i).getUrl(), response.get(i).getUrl());
        }

        BffPreLoadResponse bffResponseNull = NotificationSentPreloadDocumentsMapper.modelMapper.mapResponse(null);
        assertNull(bffResponseNull);
    }
}