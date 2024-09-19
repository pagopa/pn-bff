package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeysResponse;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeyResponseMapper;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeysResponseMapper;
import it.pagopa.pn.bff.mocks.PgInfoMock;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.pnclient.publickeys.PnPublicKeyManagerClientPGImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PublicKeysPgServiceTest {
    private static PublicKeysPgService publicKeysPgService;
    private static PnPublicKeyManagerClientPGImpl pnPublicKeyManagerClientPG;
    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();
    private final PgInfoMock pgInfoMock = new PgInfoMock();

    @BeforeAll
    public static void setup() {
        pnPublicKeyManagerClientPG = mock(PnPublicKeyManagerClientPGImpl.class);
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());

        publicKeysPgService = new PublicKeysPgService(pnPublicKeyManagerClientPG, pnExternalRegistriesClient, pnBffExceptionUtility);
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

        when(pnExternalRegistriesClient.getPgGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any()
        )).thenReturn(Flux.fromIterable(pgInfoMock.getPgGroupsMock()));

        Mono<BffPublicKeysResponse> result = publicKeysPgService.getPublicKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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

        when(pnExternalRegistriesClient.getPgGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any()
        )).thenReturn(Flux.fromIterable(pgInfoMock.getPgGroupsMock()));

        Mono<BffPublicKeysResponse> result = publicKeysPgService.getPublicKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
}
