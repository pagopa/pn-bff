package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDigests;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationDocument;

import java.util.ArrayList;
import java.util.List;

public class DocumentsMock {

    public static ArrayList<NotificationDocument> getDocumentsMock() {
        NotificationDocument notificationDocument = new NotificationDocument();
        NotificationAttachmentDigests notificationAttachmentDigests = new NotificationAttachmentDigests();
        notificationAttachmentDigests.setSha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlE=");

        NotificationAttachmentBodyRef notificationAttachmentBodyRef = new NotificationAttachmentBodyRef();
        notificationAttachmentBodyRef.setKey("PN_NOTIFICATION_ATTACHMENTS-abb7804b6e442c8b2223648af970cd1.pdf");
        notificationAttachmentBodyRef.setVersionToken("v1");

        notificationDocument.setDocIdx("0");
        notificationDocument.setTitle("Document 0");
        notificationDocument.setContentType("application/pdf");
        notificationDocument.setDigests(notificationAttachmentDigests);
        notificationDocument.setRef(notificationAttachmentBodyRef);


        return new ArrayList<>(List.of(notificationDocument));
    }

}
