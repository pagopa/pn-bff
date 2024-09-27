package it.pagopa.pn.bff.mappers.payments;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentInfoItem;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentsInfoMapperTest {
    private final PaymentsMock paymentsMock = new PaymentsMock();

    @Test
    void testPaymentInfoRequestMapper() {
        List<PaymentInfoRequest> paymentInfoRequest = PaymentsInfoMapper.modelMapper.mapPaymentInfoRequest(paymentsMock.getBffPaymentsInfoRequestMock());
        assertNotNull(paymentInfoRequest);
        assertEquals(paymentInfoRequest, paymentsMock.getPaymentsInfoRequestMock());

        List<PaymentInfoRequest> paymentInfoRequestNull = PaymentsInfoMapper.modelMapper.mapPaymentInfoRequest(null);
        assertNull(paymentInfoRequestNull);
    }

    @Test
    void testPaymentInfoResponseMapper() {
        for (PaymentInfoV21 paymentInfoItem : paymentsMock.getPaymentsInfoResponseMock()) {
            BffPaymentInfoItem bffPaymentInfoItem = PaymentsInfoMapper.modelMapper.mapPaymentInfoResponse(paymentInfoItem);
            assertNotNull(bffPaymentInfoItem);
            assertEquals(bffPaymentInfoItem.getAmount(), paymentInfoItem.getAmount());
            if (paymentInfoItem.getDetail() != null) {
                assertEquals(bffPaymentInfoItem.getDetail().getValue(), paymentInfoItem.getDetail().getValue());
            } else {
                assertNull(bffPaymentInfoItem.getDetail());
            }
            assertEquals(bffPaymentInfoItem.getStatus().getValue(), paymentInfoItem.getStatus().getValue());
            assertEquals(bffPaymentInfoItem.getDetailV2(), paymentInfoItem.getDetailV2());
            assertEquals(bffPaymentInfoItem.getDueDate(), paymentInfoItem.getDueDate());
            assertEquals(bffPaymentInfoItem.getCausaleVersamento(), paymentInfoItem.getCausaleVersamento());
            assertEquals(bffPaymentInfoItem.getErrorCode(), paymentInfoItem.getErrorCode());
            assertEquals(bffPaymentInfoItem.getCreditorTaxId(), paymentInfoItem.getCreditorTaxId());
            assertEquals(bffPaymentInfoItem.getNoticeCode(), paymentInfoItem.getNoticeCode());
        }

        BffPaymentInfoItem bffPaymentInfoItemNull = PaymentsInfoMapper.modelMapper.mapPaymentInfoResponse(null);
        assertNull(bffPaymentInfoItemNull);
    }
}