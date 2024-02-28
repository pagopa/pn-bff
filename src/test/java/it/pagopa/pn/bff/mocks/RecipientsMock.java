package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationDigitalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationPhysicalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationRecipientV23;

import java.util.ArrayList;
import java.util.List;

public class RecipientsMock {

    public static ArrayList<NotificationRecipientV23> getRecipientsMock() {
        NotificationDigitalAddress digitalAddress = new NotificationDigitalAddress();
        digitalAddress.setType(NotificationDigitalAddress.TypeEnum.PEC);
        digitalAddress.setAddress("notifichedigitali-uat@pec.pagopa.it");

        NotificationPhysicalAddress physicalAddress = new NotificationPhysicalAddress();
        physicalAddress.setAt("Presso");
        physicalAddress.setAddress("VIA SENZA NOME");
        physicalAddress.setAddressDetails("SCALA B");
        physicalAddress.setZip("87100");
        physicalAddress.setMunicipality("MILANO");
        physicalAddress.setProvince("MI");
        physicalAddress.setForeignState("ITALIA");


        NotificationRecipientV23 recipient1 = new NotificationRecipientV23();
        recipient1.setRecipientType(NotificationRecipientV23.RecipientTypeEnum.PF);
        recipient1.setTaxId("LVLDAA85T50G702B");
        recipient1.setDenomination("Mario Cucumber");
        recipient1.setDigitalDomicile(digitalAddress);
        recipient1.setPayments(PaymentsMock.getPaymentsMock());

        NotificationRecipientV23 recipient2 = new NotificationRecipientV23();
        recipient2.setRecipientType(NotificationRecipientV23.RecipientTypeEnum.PF);
        recipient2.setTaxId("FRMTTR76M06B715E");
        recipient2.setDenomination("Mario Gherkin");
        recipient1.setPhysicalAddress(physicalAddress);


        return new ArrayList<>(List.of(recipient1, recipient2));
    }
}
