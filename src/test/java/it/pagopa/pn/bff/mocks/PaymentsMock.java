package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.F24Payment;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationPaymentItem;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.PagoPaPayment;

import java.util.ArrayList;

public class PaymentsMock {

    public ArrayList<NotificationPaymentItem> getPaymentsMock() {
        ArrayList<NotificationPaymentItem> notificationPaymentItems = new ArrayList<>();

        notificationPaymentItems.add(
                new NotificationPaymentItem()
                        .pagoPa(new PagoPaPayment()
                                .noticeCode("302011686772695132")
                                .creditorTaxId("77777777777")
                                .applyCost(true)
                        )
                        .f24(new F24Payment()
                                .title("F24-001")
                                .applyCost(false)
                        )
        );

        return notificationPaymentItems;
    }
}
