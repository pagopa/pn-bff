package it.pagopa.pn.bff.pnclient.deliverypush;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.DocumentsWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
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

@ContextConfiguration(classes = {PnDocumentsWebClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnDocumentsWebClientImplTest {
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    @Autowired
    private PnDocumentsWebClientImpl pnDocumentsWebClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.DocumentsWebApi")
    private DocumentsWebApi documentsWebApi;

    @Test
    void getDocumentsWeb() throws RestClientException {
        when(documentsWebApi.getDocumentsWeb(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(DocumentCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getDocumentMock()));

        StepVerifier.create(pnDocumentsWebClient.getDocumentsWeb(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                DocumentCategory.AAR,
                "DOCUMENT_ID",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectNext(notificationDownloadDocumentMock.getDocumentMock()).verifyComplete();
    }

    @Test
    void getDocumentsWebError() {
        when(documentsWebApi.getDocumentsWeb(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(DocumentCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDocumentsWebClient.getDocumentsWeb(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                DocumentCategory.AAR,
                "DOCUMENT_ID",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectError(PnBffException.class).verify();
    }
}