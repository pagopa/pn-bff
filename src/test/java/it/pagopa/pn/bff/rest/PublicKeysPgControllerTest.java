package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeyResponseMapper;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeysCheckIssuerStatusMapper;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeysResponseMapper;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.PublicKeysPgService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.MonoMatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(PublicKeysPgController.class)
class PublicKeysPgControllerTest {
    private static final Integer LIMIT = 10;
    private static final String LAST_KEY = "LAST_KEY";
    private static final String CREATED_AT = "CREATED_AT";
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private PublicKeysPgService publicKeysPgService;

    @Test
    void getPublicKeys() {
        BffPublicKeysResponse response = PublicKeysResponseMapper.modelMapper.mapPublicKeysResponse(publicKeysMock.getPublicKeysMock());

        Mockito.when(publicKeysPgService.getPublicKeys(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyBoolean()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_PATH)
                                .queryParam("limit", LIMIT)
                                .queryParam("lastKey", LAST_KEY)
                                .queryParam("createdAt", CREATED_AT)
                                .queryParam("showPublicKey", true)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffPublicKeysResponse.class)
                .isEqualTo(response);

        Mockito.verify(publicKeysPgService).getPublicKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                LIMIT,
                LAST_KEY,
                CREATED_AT,
                true
        );
    }

    @Test
    void getPublicKeysError() {
        Mockito.when(publicKeysPgService.getPublicKeys(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyBoolean()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_PATH)
                                .queryParam("limit", LIMIT)
                                .queryParam("lastKey", LAST_KEY)
                                .queryParam("createdAt", CREATED_AT)
                                .queryParam("showPublicKey", true)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(publicKeysPgService).getPublicKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                LIMIT,
                LAST_KEY,
                CREATED_AT,
                true
        );
    }

    @Test
    void newPublicKey() {
        BffPublicKeyRequest request = new BffPublicKeyRequest();
        request.setName("mock-public-key-name");
        request.setPublicKey("mock-public-key-value");
        request.setExponent("mock-public-key-exponent");
        request.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RSA);
        BffPublicKeyResponse response = PublicKeyResponseMapper.modelMapper.mapPublicKeyResponse(publicKeysMock.gePublicKeyResponseMock());

        Mockito.when(publicKeysPgService.newPublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(BffPublicKeyResponse.class)
                .isEqualTo(response);

        Mockito.verify(publicKeysPgService).newPublicKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PG),
                eq(UserMock.PN_CX_ID),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void newPublicKeyError() {
        BffPublicKeyRequest request = new BffPublicKeyRequest();
        request.setName("mock-public-key-name");
        request.setPublicKey("mock-public-key-value");
        request.setExponent("mock-public-key-exponent");
        request.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RSA);

        Mockito.when(publicKeysPgService.newPublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(publicKeysPgService).newPublicKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PG),
                eq(UserMock.PN_CX_ID),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void deletePublicKey() {
        Mockito.when(publicKeysPgService.deletePublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_PATH + "/PUBLIC_KEY_ID")
                                .build())
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);

        Mockito.verify(publicKeysPgService).deletePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void deletePublicKeyError() {
        Mockito.when(publicKeysPgService.deletePublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.delete()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_PATH + "/PUBLIC_KEY_ID")
                                .build())
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(publicKeysPgService).deletePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void changePublicKeyStatus() {
        Mockito.when(publicKeysPgService.changeStatusPublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.empty());

        webTestClient.put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_STATUS_PATH)
                                .queryParam("status", "BLOCK")
                                .build("PUBLIC_KEY_ID"))
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);

        Mockito.verify(publicKeysPgService).changeStatusPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                "BLOCK",
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void changePublicKeyStatusError() {
        Mockito.when(publicKeysPgService.changeStatusPublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_STATUS_PATH)
                                .queryParam("status", "BLOCK")
                                .build("PUBLIC_KEY_ID"))
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(publicKeysPgService).changeStatusPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                "BLOCK",
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void rotatePublicKey() {
        BffPublicKeyRequest request = new BffPublicKeyRequest();
        request.setName("mock-public-key-name");
        request.setPublicKey("mock-public-key-value");
        request.setExponent("mock-public-key-exponent");
        request.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RSA);
        BffPublicKeyResponse response = PublicKeyResponseMapper.modelMapper.mapPublicKeyResponse(publicKeysMock.gePublicKeyResponseMock());

        Mockito.when(publicKeysPgService.rotatePublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_ROTATE_PATH)
                                .build("PUBLIC_KEY_ID"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffPublicKeyResponse.class)
                .isEqualTo(response);

        Mockito.verify(publicKeysPgService).rotatePublicKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PG),
                eq(UserMock.PN_CX_ID),
                eq(UserMock.PN_CX_ROLE),
                eq("PUBLIC_KEY_ID"),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void rotatePublicKeyError() {
        BffPublicKeyRequest request = new BffPublicKeyRequest();
        request.setName("mock-public-key-name");
        request.setPublicKey("mock-public-key-value");
        request.setExponent("mock-public-key-exponent");
        request.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RSA);

        Mockito.when(publicKeysPgService.rotatePublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_ROTATE_PATH)
                                .build("PUBLIC_KEY_ID"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(publicKeysPgService).rotatePublicKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PG),
                eq(UserMock.PN_CX_ID),
                eq(UserMock.PN_CX_ROLE),
                eq("PUBLIC_KEY_ID"),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void checkIssuerStatusPublicKeys() {
        Consent consent = new Consent();
        consent.setConsentType(ConsentType.TOS_DEST_B2B);
        consent.setAccepted(true);

        BffPublicKeysCheckIssuerResponse response = PublicKeysCheckIssuerStatusMapper.modelMapper.mapPublicKeysCheckIssuerStatus(
                publicKeysMock.getIssuerStatusPublicKeysResponseMock(),
                consent
        );

        Mockito.when(publicKeysPgService.checkIssuerPublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_CHECK_ISSUER_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffPublicKeysCheckIssuerResponse.class)
                .isEqualTo(response);

        Mockito.verify(publicKeysPgService).checkIssuerPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID
        );
    }

    @Test
    void checkIssuerStatusPublicKeysError() {
        Mockito.when(publicKeysPgService.checkIssuerPublicKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PUBLIC_KEYS_CHECK_ISSUER_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(publicKeysPgService).checkIssuerPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID
        );
    }
}