package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.mappers.virtualkeys.ResponseNewVirtualKeysMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.VirtualKeysMapper;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.pnclient.apikeys.PnVirtualKeysManagerClientPGImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VirtualKeysServiceTest {
    private static VirtualKeysService virtualKeysService;
    private static PnVirtualKeysManagerClientPGImpl pnVirtualKeysManagerClientPG;
    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();


    @BeforeAll
    public static void setup(){
        pnVirtualKeysManagerClientPG = mock(PnVirtualKeysManagerClientPGImpl.class);
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());

        virtualKeysService = new VirtualKeysService(pnVirtualKeysManagerClientPG,pnExternalRegistriesClient,pnBffExceptionUtility);
    }


    @Test
    void getVirtualKeys() {
        when(pnVirtualKeysManagerClientPG.getVirtualKeys(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.just(virtualKeysMock.getVirtualKeysMock()));


        Mono<BffVirtualKeysResponse> result = virtualKeysService.getVirtualKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        );

        StepVerifier.create(result)
                .expectNext(VirtualKeysMapper.modelMapper.mapVirtualKeysResponse(virtualKeysMock.getVirtualKeysMock()))
                .verifyComplete();
    }

    @Test
    void getVirtualKeysError() {
        when(pnVirtualKeysManagerClientPG.getVirtualKeys(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffVirtualKeysResponse> result = virtualKeysService.getVirtualKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }


    @Test
    void deleteVirtualKey() {
        when(pnVirtualKeysManagerClientPG.deleteVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        Mono<Void> result = virtualKeysService.deleteVirtualKey(
                UserMock.PN_UID,
               CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext()
                .verifyComplete();
    }

    @Test
    void deleteVirtualKeyError() {
        when(pnVirtualKeysManagerClientPG.deleteVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<Void> result = virtualKeysService.deleteVirtualKey(
                UserMock.PN_UID,
        CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404).verify();
    }

    @Test
    void newVirtualKey() {
        BffNewVirtualKeyRequest bffNewVirtualKeyRequest = new BffNewVirtualKeyRequest();
        bffNewVirtualKeyRequest.setName("mock-api-key-name");

        when(pnVirtualKeysManagerClientPG.newVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestNewVirtualKey.class),
                Mockito.anyList()
        )).thenReturn(Mono.just(virtualKeysMock.getResponseNewVirtualKeyMock()));

        Mono<BffNewVirtualKeyResponse> result = virtualKeysService.newVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                Mono.just(bffNewVirtualKeyRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(ResponseNewVirtualKeysMapper.modelMapper.mapResponseNewVirtualKey(virtualKeysMock.getResponseNewVirtualKeyMock()))
                .verifyComplete();
    }

    @Test
    void newVirtualKeysError() {
        BffNewVirtualKeyRequest bffNewVirtualKeyRequest = new BffNewVirtualKeyRequest();
        bffNewVirtualKeyRequest.setName("mock-virtual-key-name");

        when(pnVirtualKeysManagerClientPG.newVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestNewVirtualKey.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNewVirtualKeyResponse> result = virtualKeysService.newVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                Mono.just(bffNewVirtualKeyRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }


    @Test
    void changeStatusVirtualKey() {
        BffVirtualKeyStatusRequest bffVirtualKeyStatusRequest = new BffVirtualKeyStatusRequest();
        bffVirtualKeyStatusRequest.setStatus(BffVirtualKeyStatusRequest.StatusEnum.ROTATE);

        when(pnVirtualKeysManagerClientPG.changeStatusVirtualKey(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        Mono<Void> result = virtualKeysService.changeStatusVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "API_KEY_ID",
                Mono.just(bffVirtualKeyStatusRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext()
                .verifyComplete();
    }

    @Test
    void changeStatusVirtualKeyError() {
        BffRequestApiKeyStatus bffRequestApiKeyStatus = new BffRequestApiKeyStatus();
        bffRequestApiKeyStatus.setStatus(BffRequestApiKeyStatus.StatusEnum.BLOCK);

        BffVirtualKeyStatusRequest bffVirtualKeyStatusRequest = new BffVirtualKeyStatusRequest();
        bffVirtualKeyStatusRequest.setStatus(BffVirtualKeyStatusRequest.StatusEnum.ROTATE);

        when(pnVirtualKeysManagerClientPG.changeStatusVirtualKey(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        Mono<Void> result = virtualKeysService.changeStatusVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "API_KEY_ID",
                Mono.just(bffVirtualKeyStatusRequest),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}
