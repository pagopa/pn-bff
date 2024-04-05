package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.apikeys.PnApikeyManagerClientPAImpl;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiKeysPaServiceTest {
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
}