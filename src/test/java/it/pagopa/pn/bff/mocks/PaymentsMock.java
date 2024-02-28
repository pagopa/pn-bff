package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.F24Payment;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationPaymentItem;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.PagoPaPayment;

import java.util.ArrayList;
import java.util.List;

public class PaymentsMock {

    public static ArrayList<NotificationPaymentItem> getPaymentsMock() {
        NotificationPaymentItem notificationPaymentItem = new NotificationPaymentItem();
        PagoPaPayment pagoPaPayment = new PagoPaPayment().noticeCode("302011686772695132").creditorTaxId("77777777777").applyCost(true);
        F24Payment f24Payment = new F24Payment().title("F24-001").applyCost(false);

        notificationPaymentItem.setPagoPa(pagoPaPayment);
        notificationPaymentItem.setF24(f24Payment);

        return new ArrayList<>(List.of(notificationPaymentItem));
    }
}
