package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullInformalNotificationRecipientV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalNotificationStatusV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.NotificationAttachmentDigests;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.NotificationDocument;

import java.time.OffsetDateTime;
import java.util.List;

public class InformalSentNotificationDetailMock {

    public static final String IUN = "AAAA-BBBB-CCCC-000000-A-B";
    public static final String SENDER_DENOMINATION = "Comune di Test";
    public static final String CAMPAIGN_ID = "campaign-1";
    public static final String SUBJECT = "Oggetto notifica bonaria";
    public static final String RECIPIENT_TAX_ID = "RCPTTX00A00A000A";
    public static final String RECIPIENT_DENOMINATION = "TSTUTN00A07A001G";

    public FullSentInformalNotificationV1 getFullSentInformalNotificationMock() {
        return new FullSentInformalNotificationV1()
                .iun(IUN)
                .senderDenomination(SENDER_DENOMINATION)
                .campaignId(CAMPAIGN_ID)
                .subject(SUBJECT)
                .sentAt(OffsetDateTime.parse("2026-08-29T13:54:42.563421537Z"))
                .documentsAvailable(true)
                .notificationStatus(InformalNotificationStatusV1.ACCEPTED)
                .recipients(List.of(getRecipientMock()))
                .documents(List.of(getDocumentMock()));
    }

    private FullInformalNotificationRecipientV1 getRecipientMock() {
        return new FullInformalNotificationRecipientV1()
                .recipientType(FullInformalNotificationRecipientV1.RecipientTypeEnum.PF)
                .taxId(RECIPIENT_TAX_ID)
                .denomination(RECIPIENT_DENOMINATION)
                .email("mario.rossi@example.com");
    }

    private NotificationDocument getDocumentMock() {
        return new NotificationDocument()
                .title("Document_0")
                .docIdx("0")
                .contentType("application/pdf")
                .digests(new NotificationAttachmentDigests().sha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlE="))
                .ref(new NotificationAttachmentBodyRef()
                        .key("PN_NOTIFICATION_ATTACHMENTS-abb7804b6e442c8b2223648af970cd1-0.pdf")
                        .versionToken("v1"));
    }
}
