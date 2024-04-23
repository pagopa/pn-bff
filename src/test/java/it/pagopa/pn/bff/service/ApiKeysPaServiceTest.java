package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestApiKeyStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestApiKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffResponseNewApiKey;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.mappers.apikeys.ResponseNewApiKeyMapper;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.apikeys.PnApikeyManagerClientPAImpl;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiKeysPaServiceTest {
    @Autowired
    private static ApiKeysPaService apiKeysPaService;
    private static PnApikeyManagerClientPAImpl pnApikeyManagerClientPA;
    private static PnInfoPaClientImpl pnInfoPaClient;
    private final ApiKeysMock apiKeysMock = new ApiKeysMock();
    private final UserMock userMock = new UserMock();

    @BeforeAll
    public static void setup() {
        pnApikeyManagerClientPA = mock(PnApikeyManagerClientPAImpl.class);
        pnInfoPaClient = mock(PnInfoPaClientImpl.class);

        apiKeysPaService = new ApiKeysPaService(pnApikeyManagerClientPA, pnInfoPaClient);
    }

    @Test
    void testGetApiKeys() {
        when(pnApikeyManagerClientPA.getApiKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.just(apiKeysMock.getApiKeysMock()));

        when(pnInfoPaClient.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any()
        )).thenReturn(Flux.fromIterable(userMock.getPaGroupsMock()));

        Mono<BffApiKeysResponse> result = apiKeysPaService.getApiKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        );

        StepVerifier.create(result)
                .expectNext(ApiKeysMapper.modelMapper.mapApiKeysResponse(apiKeysMock.getApiKeysMock(), userMock.getPaGroupsMock()))
                .verifyComplete();
    }

    @Test
    void testGetApiKeysError() {
        when(pnApikeyManagerClientPA.getApiKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        when(pnInfoPaClient.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any()
        )).thenReturn(Flux.fromIterable(userMock.getPaGroupsMock()));

        Mono<BffApiKeysResponse> result = apiKeysPaService.getApiKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
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
    void testNewApiKey() {
        BffRequestNewApiKey bffRequestNewApiKey = new BffRequestNewApiKey();
        bffRequestNewApiKey.setName("mock-api-key-name");
        List<String> groups = new ArrayList<>();
        groups.add("mock-id-1");
        groups.add("mock-id-3");
        bffRequestNewApiKey.setGroups(groups);

        when(pnApikeyManagerClientPA.newApiKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(RequestNewApiKey.class),
                Mockito.anyList()
        )).thenReturn(Mono.just(apiKeysMock.geResponseNewApiKeyMock()));

        Mono<BffResponseNewApiKey> result = apiKeysPaService.newApiKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                Mono.just(bffRequestNewApiKey),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(ResponseNewApiKeyMapper.modelMapper.mapResponseNewApiKey(apiKeysMock.geResponseNewApiKeyMock()))
                .verifyComplete();
    }

    @Test
    void testNewApiKeysError() {
        BffRequestNewApiKey bffRequestNewApiKey = new BffRequestNewApiKey();
        bffRequestNewApiKey.setName("mock-api-key-name");
        List<String> groups = new ArrayList<>();
        groups.add("mock-id-1");
        groups.add("mock-id-3");
        bffRequestNewApiKey.setGroups(groups);

        when(pnApikeyManagerClientPA.newApiKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(RequestNewApiKey.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffResponseNewApiKey> result = apiKeysPaService.newApiKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                Mono.just(bffRequestNewApiKey),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void testDeleteApiKey() {
        when(pnApikeyManagerClientPA.deleteApiKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        Mono<Void> result = apiKeysPaService.deleteApiKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext()
                .verifyComplete();
    }

    @Test
    void testDeleteApiKeyError() {
        when(pnApikeyManagerClientPA.deleteApiKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<Void> result = apiKeysPaService.deleteApiKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void testChangeStatusApiKey() {
        BffRequestApiKeyStatus bffRequestApiKeyStatus = new BffRequestApiKeyStatus();
        bffRequestApiKeyStatus.setStatus(BffRequestApiKeyStatus.StatusEnum.BLOCK);

        when(pnApikeyManagerClientPA.changeStatusApiKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestApiKeyStatus.class),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        Mono<Void> result = apiKeysPaService.changeStatusApiKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                Mono.just(bffRequestApiKeyStatus),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext()
                .verifyComplete();
    }

    @Test
    void testChangeStatusApiKeyError() {
        BffRequestApiKeyStatus bffRequestApiKeyStatus = new BffRequestApiKeyStatus();
        bffRequestApiKeyStatus.setStatus(BffRequestApiKeyStatus.StatusEnum.BLOCK);

        when(pnApikeyManagerClientPA.changeStatusApiKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestApiKeyStatus.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<Void> result = apiKeysPaService.changeStatusApiKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                Mono.just(bffRequestApiKeyStatus),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}