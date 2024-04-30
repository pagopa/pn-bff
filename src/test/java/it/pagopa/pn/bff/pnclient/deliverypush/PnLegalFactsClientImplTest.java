package it.pagopa.pn.bff.pnclient.deliverypush;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.LegalFactsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnLegalFactsClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnLegalFactsClientImplTest {
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    @Autowired
    private PnLegalFactsClientImpl pnLegalFactsClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.LegalFactsApi")
    private LegalFactsApi legalFactsApi;

    @Test
    void getLegalFactWeb() throws RestClientException {
        when(legalFactsApi.getLegalFact(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LegalFactCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getLegalFactMock()));

        StepVerifier.create(pnLegalFactsClient.getLegalFact(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                LegalFactCategory.DIGITAL_DELIVERY,
                "LEGAL_FACT_ID",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectNext(notificationDownloadDocumentMock.getLegalFactMock()).verifyComplete();
    }

    @Test
    void getLegalFactError() {
        when(legalFactsApi.getLegalFact(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LegalFactCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnLegalFactsClient.getLegalFact(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                LegalFactCategory.DIGITAL_DELIVERY,
                "LEGAL_FACT_ID",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectError(PnBffException.class).verify();
    }
}