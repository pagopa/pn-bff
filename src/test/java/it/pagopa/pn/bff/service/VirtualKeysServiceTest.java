package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffResponseNewApiKey;
import it.pagopa.pn.bff.mappers.apikeys.ResponseNewApiKeyMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.RequestNewVirtualKeysMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.ResponseNewVirtualKeysMapper;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.pnclient.virtualkeys.PnVirtualKeysManagerClientPGImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

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
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
        bffNewVirtualKeyRequest.setName("mock-api-key-name");

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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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
}
