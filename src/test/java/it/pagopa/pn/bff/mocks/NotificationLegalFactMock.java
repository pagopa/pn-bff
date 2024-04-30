package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse;

import java.math.BigDecimal;

public class NotificationLegalFactMock {
    public LegalFactDownloadMetadataResponse getLegalFactMock() {
        LegalFactDownloadMetadataResponse legalFact = new LegalFactDownloadMetadataResponse();
        legalFact.setContentLength(BigDecimal.valueOf(999));
        legalFact.setFilename("Legal fact");
        legalFact.setUrl("https://legal-fact-fake-url.com");
        legalFact.setRetryAfter(BigDecimal.valueOf(10));
        return legalFact;
    }
}