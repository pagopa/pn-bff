package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationDigitalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationPhysicalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationRecipientV23;

import java.util.ArrayList;

public class RecipientsMock {

    public static ArrayList<NotificationRecipientV23> getRecipientsMock() {
        ArrayList<NotificationRecipientV23> recipients = new ArrayList<>();

        recipients.add(
                new NotificationRecipientV23()
                        .recipientType(NotificationRecipientV23.RecipientTypeEnum.PF)
                        .taxId("LVLDAA85T50G702B")
                        .denomination("Mario Cucumber")
                        .digitalDomicile(
                                new NotificationDigitalAddress()
                                        .type(NotificationDigitalAddress.TypeEnum.PEC)
                                        .address("notifichedigitali-uat@pec.pagopa.it")
                        )
                        .physicalAddress(
                                new NotificationPhysicalAddress()
                                        .at("Presso")
                                        .address("VIA SENZA NOME")
                                        .addressDetails("SCALA B")
                                        .zip("87100")
                                        .municipality("MILANO")
                                        .municipalityDetails("MILANO")
                                        .province("MI")
                                        .foreignState("ITALIA")
                        )
                        .payments(PaymentsMock.getPaymentsMock())
        );

        recipients.add(
                new NotificationRecipientV23()
                        .recipientType(NotificationRecipientV23.RecipientTypeEnum.PF)
                        .taxId("FRMTTR76M06B715E")
                        .denomination("Mario Gherkin")
                        .digitalDomicile(
                                new NotificationDigitalAddress()
                                        .type(NotificationDigitalAddress.TypeEnum.PEC)
                                        .address("testpagopa3@pec.pagopa.it")
                        )
                        .physicalAddress(
                                new NotificationPhysicalAddress()
                                        .at("Presso")
                                        .address("VIA SENZA NOME")
                                        .addressDetails("SCALA A")
                                        .zip("87100")
                                        .municipality("MILANO")
                                        .municipalityDetails("MILANO")
                                        .province("MI")
                                        .foreignState("ITALIA")
                        )
        );

        recipients.add(
                new NotificationRecipientV23()
                        .recipientType(NotificationRecipientV23.RecipientTypeEnum.PG)
                        .taxId("12345678910")
                        .denomination("Ufficio Tal dei Tali")
                        .physicalAddress(
                                new NotificationPhysicalAddress()
                                        .at("Presso")
                                        .address("VIA SENZA NOME")
                                        .addressDetails("SCALA C")
                                        .zip("87100")
                                        .municipality("MILANO")
                                        .municipalityDetails("MILANO")
                                        .province("MI")
                                        .foreignState("ITALIA")
                        )
        );


        return recipients;
    }
}
