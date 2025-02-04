package it.pagopa.pn.bff.pnclient.emd;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.DocumentsWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.api.CheckTppApi;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.RetrievalPayload;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnEmdClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnEmdClientImplTest {
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();

    @Autowired
    private PnEmdClientImpl pnEmdClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.emd.api.CheckTppApi")
    private CheckTppApi checkTppApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckTpp() {

        when(checkTppApi.emdCheckTPP(anyString())).thenReturn(Mono.just(notificationsReceivedMock.getRetrievalPayloadMock()));

        Mono<RetrievalPayload> result = pnEmdClient.checkTpp("retrievalId");

        StepVerifier.create(result)
                .expectNext(notificationsReceivedMock.getRetrievalPayloadMock())
                .verifyComplete();
    }

    @Test
    void testCheckTppError() {
        when(checkTppApi.emdCheckTPP(anyString())).thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<RetrievalPayload> result = pnEmdClient.checkTpp("test-retrieval-id");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}