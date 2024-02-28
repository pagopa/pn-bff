package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDigests;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationDocument;

import java.util.ArrayList;

public class DocumentsMock {

    public static ArrayList<NotificationDocument> getDocumentsMock() {
        ArrayList<NotificationDocument> notificationDocuments = new ArrayList<>();

        notificationDocuments.add(
                new NotificationDocument()
                        .docIdx("0")
                        .title("Document 0")
                        .contentType("application/pdf")
                        .digests(new NotificationAttachmentDigests()
                                .sha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlE=")
                        )
                        .ref(new NotificationAttachmentBodyRef()
                                .key("PN_NOTIFICATION_ATTACHMENTS-abb7804b6e442c8b2223648af970cd1.pdf")
                                .versionToken("v1")
                        )
        );

        return notificationDocuments;
    }

}
