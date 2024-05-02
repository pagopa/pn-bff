package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse;

import java.math.BigDecimal;

public class NotificationDownloadDocumentMock {

    public DocumentDownloadMetadataResponse getDocumentMock() {
        DocumentDownloadMetadataResponse document = new DocumentDownloadMetadataResponse();
        document.setContentLength(BigDecimal.valueOf(999));
        document.setFilename("Document");
        document.setUrl("https://document-fake-url.com");
        document.setRetryAfter(BigDecimal.valueOf(10));
        return document;
    }

    public LegalFactDownloadMetadataResponse getLegalFactMock() {
        LegalFactDownloadMetadataResponse legalFact = new LegalFactDownloadMetadataResponse();
        legalFact.setContentLength(BigDecimal.valueOf(999));
        legalFact.setFilename("Legal fact");
        legalFact.setUrl("https://legal-fact-fake-url.com");
        legalFact.setRetryAfter(BigDecimal.valueOf(10));
        return legalFact;
    }

    public it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationAttachmentDownloadMetadataResponse getPaAttachmentMock() {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationAttachmentDownloadMetadataResponse attachment = new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationAttachmentDownloadMetadataResponse();
        attachment.setContentLength(999);
        attachment.setFilename("Attachment");
        attachment.setUrl("https://attachment-fake-url.com");
        attachment.setContentType("application/json");
        attachment.setRetryAfter(10);
        attachment.setSha256("random-sha256");
        return attachment;
    }

    public it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse getRecipientAttachmentMock() {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse attachment = new it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse();
        attachment.setContentLength(999);
        attachment.setFilename("Attachment");
        attachment.setUrl("https://attachment-fake-url.com");
        attachment.setContentType("application/json");
        attachment.setRetryAfter(10);
        attachment.setSha256("random-sha256");
        return attachment;
    }
}