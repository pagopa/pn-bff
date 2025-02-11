package it.pagopa.pn.bff.pnclient.emd;

import it.pagopa.pn.bff.generated.openapi.msclient.emd.api.CheckTppApi;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.api.PaymentApi;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.PaymentUrlResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.RetrievalPayload;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private final PaymentsMock paymentsMock = new PaymentsMock();

    @Autowired
    private PnEmdClientImpl pnEmdClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.emd.api.CheckTppApi")
    private CheckTppApi checkTppApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.emd.api.PaymentApi")
    private PaymentApi paymentApi;

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

    @Test
    void testPayment() {
        when(paymentApi.getPaymentUrl(anyString(), anyString(), anyString())).thenReturn(Mono.just(paymentsMock.getPaymentUrlResponse()));

        Mono<PaymentUrlResponse> result = pnEmdClient.getPaymentUrl("retrievalId", "notificationId", "paTaxId");

        StepVerifier.create(result)
                .expectNext(paymentsMock.getPaymentUrlResponse())
                .verifyComplete();
    }

    @Test
    void testPaymentError() {
        when(paymentApi.getPaymentUrl(anyString(), anyString(), anyString())).thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<PaymentUrlResponse> result = pnEmdClient.getPaymentUrl("retrievalId", "notificationId", "paTaxId");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}