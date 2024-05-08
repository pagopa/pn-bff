package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaymentRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PaymentsMock {

    private final Random random = new Random();

    private PaymentInfoRequest getPaymentInfoRequestMock(int index) {
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest();
        paymentInfoRequest.setCreditorTaxId("77777777777");
        paymentInfoRequest.setNoticeCode("33333333333333333" + index);
        return paymentInfoRequest;
    }

    private it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaymentInfoRequest getBffPaymentInfoRequestMock(int index) {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaymentInfoRequest paymentInfoRequest = new it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaymentInfoRequest();
        paymentInfoRequest.setCreditorTaxId("77777777777");
        paymentInfoRequest.setNoticeCode("33333333333333333" + index);
        return paymentInfoRequest;
    }

    private PaymentInfoV21 getPaymentInfoResponseMock(int index, PaymentStatus status, Detail detail, String errorCode) {
        PaymentInfoV21 paymentInfoResponse = new PaymentInfoV21();
        paymentInfoResponse.setCreditorTaxId("77777777777");
        paymentInfoResponse.setNoticeCode("33333333333333333" + index);
        paymentInfoResponse.setAmount(random.nextInt(1000));
        paymentInfoResponse.setDetail(detail);
        paymentInfoResponse.setDetailV2(errorCode);
        paymentInfoResponse.setCausaleVersamento("TARI rata " + index);
        paymentInfoResponse.setStatus(status);
        paymentInfoResponse.setUrl("https://api.uat.platform.pagopa.it/checkout/auth/payments/v2");
        paymentInfoResponse.setDueDate(LocalDate.ofEpochDay(ThreadLocalRandom
                .current().nextInt(-365, 365)).toString()
        );
        paymentInfoResponse.setErrorCode(errorCode);
        return paymentInfoResponse;
    }

    public List<PaymentInfoRequest> getPaymentsInfoRequestMock() {
        List<PaymentInfoRequest> paymentsInfoRequest = new ArrayList<>();
        paymentsInfoRequest.add(getPaymentInfoRequestMock(0));
        paymentsInfoRequest.add(getPaymentInfoRequestMock(1));
        paymentsInfoRequest.add(getPaymentInfoRequestMock(2));
        paymentsInfoRequest.add(getPaymentInfoRequestMock(3));
        paymentsInfoRequest.add(getPaymentInfoRequestMock(4));
        paymentsInfoRequest.add(getPaymentInfoRequestMock(5));
        return paymentsInfoRequest;
    }

    public List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaymentInfoRequest> getBffPaymentsInfoRequestMock() {
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaymentInfoRequest> paymentsInfoRequest = new ArrayList<>();
        paymentsInfoRequest.add(getBffPaymentInfoRequestMock(0));
        paymentsInfoRequest.add(getBffPaymentInfoRequestMock(1));
        paymentsInfoRequest.add(getBffPaymentInfoRequestMock(2));
        paymentsInfoRequest.add(getBffPaymentInfoRequestMock(3));
        paymentsInfoRequest.add(getBffPaymentInfoRequestMock(4));
        paymentsInfoRequest.add(getBffPaymentInfoRequestMock(5));
        return paymentsInfoRequest;
    }

    public List<PaymentInfoV21> getPaymentsInfoResponseMock() {
        List<PaymentInfoV21> paymentsInfoResponse = new ArrayList<>();
        paymentsInfoResponse.add(getPaymentInfoResponseMock(0, PaymentStatus.SUCCEEDED, null, null));
        paymentsInfoResponse.add(getPaymentInfoResponseMock(1, PaymentStatus.REQUIRED, null, null));
        paymentsInfoResponse.add(getPaymentInfoResponseMock(2, PaymentStatus.FAILURE, Detail.PAYMENT_CANCELED, "PAYMENT_CANCELED"));
        paymentsInfoResponse.add(getPaymentInfoResponseMock(3, PaymentStatus.FAILURE, Detail.GENERIC_ERROR, "GENERIC_ERROR"));
        paymentsInfoResponse.add(getPaymentInfoResponseMock(4, PaymentStatus.FAILURE, Detail.PAYMENT_EXPIRED, "PAYMENT_EXPIRED"));
        paymentsInfoResponse.add(getPaymentInfoResponseMock(5, PaymentStatus.REQUIRED, null, null));
        return paymentsInfoResponse;
    }

    public PaymentRequest getPaymentRequestMock() {
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentNotice paymentNotice = new PaymentNotice();
        paymentNotice.setAmount(999);
        paymentNotice.setNoticeNumber("333333333333333333");
        paymentNotice.setFiscalCode("77777777777");
        paymentNotice.setDescription("Description of the payment");
        paymentNotice.setCompanyName("The name of the company");
        paymentRequest.setPaymentNotice(paymentNotice);
        paymentRequest.setReturnUrl("https://return-url.com");
        return paymentRequest;
    }

    public BffPaymentRequest getBffPaymentRequestMock() {
        BffPaymentRequest paymentRequest = new BffPaymentRequest();
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaymentNotice paymentNotice = new it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaymentNotice();
        paymentNotice.setAmount(999);
        paymentNotice.setNoticeNumber("333333333333333333");
        paymentNotice.setFiscalCode("77777777777");
        paymentNotice.setDescription("Description of the payment");
        paymentNotice.setCompanyName("The name of the company");
        paymentRequest.setPaymentNotice(paymentNotice);
        paymentRequest.setReturnUrl("https://return-url.com");
        return paymentRequest;
    }

    public PaymentResponse getPaymentResponseMock() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setCheckoutUrl("https://checkout-url.com");
        return paymentResponse;
    }
}