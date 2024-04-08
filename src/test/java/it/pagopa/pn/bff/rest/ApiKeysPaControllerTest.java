package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.ApiKeysPaService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@WebFluxTest(ApiKeysPaController.class)
class ApiKeysPaControllerTest {
    private static final Integer LIMIT = 10;
    private static final String LAST_KEY = "LAST_KEY";
    private static final String LAST_UPDATE = "LAST_UPDATE";
    private final ApiKeysMock apiKeysMock = new ApiKeysMock();
    private final UserMock userMock = new UserMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private ApiKeysPaService apiKeysPaService;
    @SpyBean
    private ApiKeysPaController apiKeysPaController;

    @Test
    void getApiKeys() {
        BffApiKeysResponse response = ApiKeysMapper.modelMapper.mapApiKeysResponse(apiKeysMock.getApiKeysMock(), userMock.getPaGroupsMock());
        Mockito.when(apiKeysPaService.getApiKeys(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
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
                                .path(PnBffRestConstants.GET_APIKEYS_PATH)
                                .queryParam("limit", LIMIT)
                                .queryParam("lastKey", LAST_KEY)
                                .queryParam("lastUpdate", LAST_UPDATE)
                                .queryParam("showVirtualKey", true)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffApiKeysResponse.class)
                .isEqualTo(response);

        Mockito.verify(apiKeysPaService).getApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                LIMIT,
                LAST_KEY,
                LAST_UPDATE,
                true
        );
    }

    @Test
    void getApiKeysError() {
        Mockito.when(apiKeysPaService.getApiKeys(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyBoolean()
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.GET_APIKEYS_PATH)
                                .queryParam("limit", LIMIT)
                                .queryParam("lastKey", LAST_KEY)
                                .queryParam("lastUpdate", LAST_UPDATE)
                                .queryParam("showVirtualKey", true)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(apiKeysPaService).getApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                LIMIT,
                LAST_KEY,
                LAST_UPDATE,
                true
        );
    }
}