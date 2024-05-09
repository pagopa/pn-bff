package it.pagopa.pn.bff.mappers.payments;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaymentResponse;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentsCartMapperTest {

    private final PaymentsMock paymentsMock = new PaymentsMock();

    @Test
    void testPaymentRequestMapper() {
        PaymentRequest paymentRequest = PaymentsCartMapper.modelMapper.mapPaymentRequest(paymentsMock.getBffPaymentRequestMock());
        assertNotNull(paymentRequest);
        assertEquals(paymentRequest, paymentsMock.getPaymentRequestMock());

        PaymentRequest paymentRequestNull = PaymentsCartMapper.modelMapper.mapPaymentRequest(null);
        assertNull(paymentRequestNull);
    }

    @Test
    void testPaymentResponseMapper() {
        BffPaymentResponse paymentResponse = PaymentsCartMapper.modelMapper.mapPaymentResponse(paymentsMock.getPaymentResponseMock());
        assertNotNull(paymentResponse);
        assertEquals(paymentResponse.getCheckoutUrl(), paymentsMock.getPaymentResponseMock().getCheckoutUrl());

        BffPaymentResponse paymentResponseNull = PaymentsCartMapper.modelMapper.mapPaymentResponse(null);
        assertNull(paymentResponseNull);
    }
}