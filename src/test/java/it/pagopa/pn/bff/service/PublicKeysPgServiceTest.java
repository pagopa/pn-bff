package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysCheckIssuerResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysResponse;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeyResponseMapper;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeysCheckIssuerStatusMapper;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeysIssuerStatusMapper;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeysResponseMapper;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.apikeys.PnPublicKeyManagerClientPGImpl;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PublicKeysPgServiceTest {
    private static PublicKeysPgService publicKeysPgService;
    private static PnPublicKeyManagerClientPGImpl pnPublicKeyManagerClientPG;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private static PnUserAttributesClientImpl pnUserAttributesClient;
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();

    @BeforeAll
    public static void setup() {
        pnPublicKeyManagerClientPG = mock(PnPublicKeyManagerClientPGImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        pnUserAttributesClient = mock(PnUserAttributesClientImpl.class);

        publicKeysPgService = new PublicKeysPgService(pnPublicKeyManagerClientPG, pnBffExceptionUtility, pnUserAttributesClient);
    }

    @Test
    void getPublicKeys() {
        when(pnPublicKeyManagerClientPG.getPublicKeys(
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

        Mono<BffPublicKeysResponse> result = publicKeysPgService.getPublicKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "CREATED_AT",
                true
        );

        StepVerifier.create(result)
                .expectNext(PublicKeysResponseMapper.modelMapper.mapPublicKeysResponse(publicKeysMock.getPublicKeysMock()))
                .verifyComplete();
    }

    @Test
    void getPublicKeysError() {
        when(pnPublicKeyManagerClientPG.getPublicKeys(
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

        Mono<BffPublicKeysResponse> result = publicKeysPgService.getPublicKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "CREATED_AT",
                true
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void newPublicKey() {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();
        bffPublicKeyRequest.setName("mock-public-key-name");
        bffPublicKeyRequest.setPublicKey("PUBLIC_KEY");
        bffPublicKeyRequest.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RS256);
        bffPublicKeyRequest.setExponent("EXPONENT");

        when(pnPublicKeyManagerClientPG.newPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.just(publicKeysMock.gePublicKeyResponseMock()));

        Mono<BffPublicKeyResponse> result = publicKeysPgService.newPublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                Mono.just(bffPublicKeyRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(PublicKeyResponseMapper.modelMapper.mapPublicKeyResponse(publicKeysMock.gePublicKeyResponseMock()))
                .verifyComplete();
    }

    @Test
    void newPublicKeyError() {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();
        bffPublicKeyRequest.setName("mock-public-key-name");
        bffPublicKeyRequest.setPublicKey("PUBLIC_KEY");
        bffPublicKeyRequest.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RS256);
        bffPublicKeyRequest.setExponent("EXPONENT");

        when(pnPublicKeyManagerClientPG.newPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffPublicKeyResponse> result = publicKeysPgService.newPublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                Mono.just(bffPublicKeyRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void deletePublicKey() {
        when(pnPublicKeyManagerClientPG.deletePublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        Mono<Void> result = publicKeysPgService.deletePublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext()
                .verifyComplete();
    }

    @Test
    void deletePublicKeyError() {
        when(pnPublicKeyManagerClientPG.deletePublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<Void> result = publicKeysPgService.deletePublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void changeStatusPublicKey() {
        when(pnPublicKeyManagerClientPG.changeStatusPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        Mono<Void> result = publicKeysPgService.changeStatusPublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                "BLOCK",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext()
                .verifyComplete();
    }

    @Test
    void changeStatusPublicKeyError() {
        when(pnPublicKeyManagerClientPG.changeStatusPublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<Void> result = publicKeysPgService.changeStatusPublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                "BLOCK",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void rotatePublicKey() {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();
        bffPublicKeyRequest.setName("mock-public-key-name");
        bffPublicKeyRequest.setPublicKey("PUBLIC_KEY");
        bffPublicKeyRequest.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RS256);
        bffPublicKeyRequest.setExponent("EXPONENT");

        when(pnPublicKeyManagerClientPG.rotatePublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.just(publicKeysMock.gePublicKeyResponseMock()));

        Mono<BffPublicKeyResponse> result = publicKeysPgService.rotatePublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                Mono.just(bffPublicKeyRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(PublicKeyResponseMapper.modelMapper.mapPublicKeyResponse(publicKeysMock.gePublicKeyResponseMock()))
                .verifyComplete();
    }

    @Test
    void rotatePublicKeyError() {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();
        bffPublicKeyRequest.setName("mock-public-key-name");
        bffPublicKeyRequest.setPublicKey("PUBLIC_KEY");
        bffPublicKeyRequest.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RS256);
        bffPublicKeyRequest.setExponent("EXPONENT");

        when(pnPublicKeyManagerClientPG.rotatePublicKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PublicKeyRequest.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffPublicKeyResponse> result = publicKeysPgService.rotatePublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                Mono.just(bffPublicKeyRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void checkIssuerPublicKeys() {
        ConsentsMock consentsMock = new ConsentsMock();
        when(pnPublicKeyManagerClientPG.getIssuerStatus(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString()
        )).thenReturn(Mono.just(publicKeysMock.getIssuerStatusPublicKeysResponseMock()));

        when(pnUserAttributesClient.getConsentByType(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class)
        )).thenReturn(Mono.just(consentsMock.getB2BConsentResponseMock()));

        Mono<BffPublicKeysCheckIssuerResponse> result = publicKeysPgService.checkIssuerPublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID
        );

        StepVerifier.create(result)
                .expectNext(PublicKeysCheckIssuerStatusMapper.modelMapper.mapPublicKeysIssuerStatus(
                        PublicKeysIssuerStatusMapper.modelMapper.mapPublicKeysIssuerStatus(publicKeysMock.getIssuerStatusPublicKeysResponseMock()),
                        consentsMock.getB2BConsentResponseMock()
                ))
                .verifyComplete();
    }

    @Test
    void checkIssuerPublicKeysError() {
        when(pnPublicKeyManagerClientPG.getIssuerStatus(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString()
        )).thenReturn(Mono.just(publicKeysMock.getIssuerStatusPublicKeysResponseMock()));

        when(pnUserAttributesClient.getConsentByType(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));;

        Mono<BffPublicKeysCheckIssuerResponse> result = publicKeysPgService.checkIssuerPublicKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}