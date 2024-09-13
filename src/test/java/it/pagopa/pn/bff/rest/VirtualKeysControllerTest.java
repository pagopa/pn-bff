package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffResponseNewApiKey;
import it.pagopa.pn.bff.mappers.virtualkeys.ResponseNewVirtualKeysMapper;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
import it.pagopa.pn.bff.service.VirtualKeysService;
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
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(VirtualKeysController.class)
public class VirtualKeysControllerTest {
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private VirtualKeysService virtualKeysService;
    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();


    @Test
    void deleteVirtualKey() {
        Mockito.when(virtualKeysService.deleteVirtualKey(
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
                                .path(PnBffRestConstants.VIRTUALKEYS_PATH + "/VIRTUAL_KEY_ID")
                                .build())
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .header("id", "VIRTUALKEY_ID")
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);

        Mockito.verify(virtualKeysService).deleteVirtualKey(
                UserMock.PN_UID,
               CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void newVirtualKey() {
        BffNewVirtualKeyRequest request = new BffNewVirtualKeyRequest();
        request.setName("mock-virtual-key-name");
        BffNewVirtualKeyResponse response = ResponseNewVirtualKeysMapper.modelMapper.mapResponseNewVirtualKey(virtualKeysMock.getResponseNewVirtualKeyMock());

        Mockito.when(virtualKeysService.newVirtualKey(
                        Mockito.anyString(),
                        Mockito.any(it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.VIRTUALKEYS_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_ROLE_HEADER,UserMock.PN_CX_ROLE)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNewVirtualKeyResponse.class)
                .isEqualTo(response);

        Mockito.verify(virtualKeysService).newVirtualKey(
                eq(UserMock.PN_UID),
                eq(it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG),
                eq(UserMock.PN_CX_ID),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void newVirtualKeyError() {
        BffNewVirtualKeyRequest request = new BffNewVirtualKeyRequest();
        request.setName("mock-virtual-key-name");
        BffNewVirtualKeyResponse response = ResponseNewVirtualKeysMapper.modelMapper.mapResponseNewVirtualKey(virtualKeysMock.getResponseNewVirtualKeyMock());

        Mockito.when(virtualKeysService.newVirtualKey(
                        Mockito.anyString(),
                        Mockito.any(it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.VIRTUALKEYS_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_ROLE_HEADER,UserMock.PN_CX_ROLE)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();


        Mockito.verify(virtualKeysService).newVirtualKey(
                eq(UserMock.PN_UID),
                eq(it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG),
                eq(UserMock.PN_CX_ID),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
                );
    }
}
