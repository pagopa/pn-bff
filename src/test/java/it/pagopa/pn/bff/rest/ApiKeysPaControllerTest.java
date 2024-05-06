package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.mappers.apikeys.ResponseNewApiKeyMapper;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.ApiKeysPaService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.MonoComparator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

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
                                .path(PnBffRestConstants.APIKEYS_PATH)
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
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.APIKEYS_PATH)
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

    @Test
    void newApiKey() {
        BffRequestNewApiKey request = new BffRequestNewApiKey();
        request.setName("mock-api-key-name");
        List<String> groups = new ArrayList<>();
        groups.add("mock-id-1");
        groups.add("mock-id-3");
        request.setGroups(groups);
        BffResponseNewApiKey response = ResponseNewApiKeyMapper.modelMapper.mapResponseNewApiKey(apiKeysMock.geResponseNewApiKeyMock());

        Mockito.when(apiKeysPaService.newApiKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.APIKEYS_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffResponseNewApiKey.class)
                .isEqualTo(response);

        Mockito.verify(apiKeysPaService).newApiKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PA),
                eq(UserMock.PN_CX_ID),
                argThat((argumentToCompare -> MonoComparator.compare(argumentToCompare, Mono.just(request)))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void newApiKeyError() {
        BffRequestNewApiKey request = new BffRequestNewApiKey();
        request.setName("mock-api-key-name");
        List<String> groups = new ArrayList<>();
        groups.add("mock-id-1");
        groups.add("mock-id-3");
        request.setGroups(groups);

        Mockito.when(apiKeysPaService.newApiKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.APIKEYS_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(apiKeysPaService).newApiKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PA),
                eq(UserMock.PN_CX_ID),
                argThat((argumentToCompare -> MonoComparator.compare(argumentToCompare, Mono.just(request)))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void deleteApiKey() {
        Mockito.when(apiKeysPaService.deleteApiKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.empty());


        webTestClient.delete()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.APIKEYS_PATH + "/API_KEY_ID")
                                .build())
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);

        Mockito.verify(apiKeysPaService).deleteApiKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void deleteApiKeyError() {
        Mockito.when(apiKeysPaService.deleteApiKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.delete()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.APIKEYS_PATH + "/API_KEY_ID")
                                .build())
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(apiKeysPaService).deleteApiKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void changeStatusApiKey() {
        BffRequestApiKeyStatus request = new BffRequestApiKeyStatus();
        request.setStatus(BffRequestApiKeyStatus.StatusEnum.BLOCK);

        Mockito.when(apiKeysPaService.changeStatusApiKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.empty());


        webTestClient.put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.APIKEYS_PATH + "/API_KEY_ID/status")
                                .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);

        Mockito.verify(apiKeysPaService).changeStatusApiKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PA),
                eq(UserMock.PN_CX_ID),
                eq("API_KEY_ID"),
                argThat((argumentToCompare -> MonoComparator.compare(argumentToCompare, Mono.just(request)))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void changeStatusApiKeyError() {
        BffRequestApiKeyStatus request = new BffRequestApiKeyStatus();
        request.setStatus(BffRequestApiKeyStatus.StatusEnum.BLOCK);

        Mockito.when(apiKeysPaService.changeStatusApiKey(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.APIKEYS_PATH + "/API_KEY_ID/status")
                                .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(apiKeysPaService).changeStatusApiKey(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PA),
                eq(UserMock.PN_CX_ID),
                eq("API_KEY_ID"),
                argThat((argumentToCompare -> MonoComparator.compare(argumentToCompare, Mono.just(request)))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }
}