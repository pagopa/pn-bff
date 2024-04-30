package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;

import java.math.BigDecimal;

public class NotificationDocumentMock {
    public DocumentDownloadMetadataResponse getDocumentMock() {
        DocumentDownloadMetadataResponse document = new DocumentDownloadMetadataResponse();
        document.setContentLength(BigDecimal.valueOf(999));
        document.setFilename("Document");
        document.setUrl("https://document-fake-url.com");
        document.setRetryAfter(BigDecimal.valueOf(10));
        return document;
    }
}