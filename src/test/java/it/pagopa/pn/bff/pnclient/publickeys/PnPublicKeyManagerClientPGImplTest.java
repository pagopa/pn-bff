package it.pagopa.pn.bff.pnclient.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.api.PublicKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyStatus;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
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

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnPublicKeyManagerClientPGImpl.class})
@ExtendWith(SpringExtension.class)
class PnPublicKeyManagerClientPGImplTest {
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();
    @Autowired
    private PnPublicKeyManagerClientPGImpl pnPublicKeyManagerClientPG;
    @MockBean()
    private PublicKeysApi publicKeysApi;

    @Test
    void getPublicKeys() throws RestClientException {
        when(publicKeysApi.getPublicKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.just(publicKeysMock.getPublicKeysMock()));

        StepVerifier.create(pnPublicKeyManagerClientPG.getPublicKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "CREATED_AT",
                true
        )).expectNext(publicKeysMock.getPublicKeysMock()).verifyComplete();
    }

    @Test
    void getPublicKeysError() {
        when(publicKeysApi.getPublicKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnPublicKeyManagerClientPG.getPublicKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "CREATED_AT",
                true
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void newPublicKey() throws RestClientException {
        when(publicKeysApi.newPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.just(publicKeysMock.gePublicKeyResponseMock()));

        StepVerifier.create(pnPublicKeyManagerClientPG.newPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectNext(publicKeysMock.gePublicKeyResponseMock()).verifyComplete();
    }

    @Test
    void newPublicKeyError() {
        when(publicKeysApi.newPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnPublicKeyManagerClientPG.newPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void deletePublicKey() throws RestClientException {
        when(publicKeysApi.deletePublicKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnPublicKeyManagerClientPG.deletePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void deletePublicKeyError() {
        when(publicKeysApi.deletePublicKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnPublicKeyManagerClientPG.deletePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void changeStatusPublicKey() throws RestClientException {
        when(publicKeysApi.changeStatusPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnPublicKeyManagerClientPG.changeStatusPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                PublicKeyStatus.BLOCKED.getValue(),
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void changeStatusPublicKeyError() {
        when(publicKeysApi.changeStatusPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnPublicKeyManagerClientPG.changeStatusPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                PublicKeyStatus.BLOCKED.getValue(),
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void rotatePublicKey() throws RestClientException {
        when(publicKeysApi.rotatePublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.just(publicKeysMock.gePublicKeyResponseMock()));

        StepVerifier.create(pnPublicKeyManagerClientPG.rotatePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectNext(publicKeysMock.gePublicKeyResponseMock()).verifyComplete();
    }

    @Test
    void rotatePublicKeyError() {
        when(publicKeysApi.rotatePublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnPublicKeyManagerClientPG.rotatePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }
}