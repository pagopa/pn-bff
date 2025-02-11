package it.pagopa.pn.bff.mappers.payments;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentTppResponse;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentsTppMapperTest {

    private final PaymentsMock paymentsMock = new PaymentsMock();

    @Test
    void testPaymentTppResponseMapper() {
        BffPaymentTppResponse bffPaymentTppResponse = PaymentsTppMapper.modelMapper.mapPaymentTppResponse(paymentsMock.getPaymentUrlResponse());
        assertNotNull(bffPaymentTppResponse);
        assertEquals(bffPaymentTppResponse.getPaymentUrl(), paymentsMock.getPaymentUrlResponse().getPaymentUrl());

        BffPaymentTppResponse bffPaymentTppResponseNull = PaymentsTppMapper.modelMapper.mapPaymentTppResponse(null);
        assertNull(bffPaymentTppResponseNull);
    }
}