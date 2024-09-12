package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.VirtualKeysService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Slf4j
@WebFluxTest(VirtualKeysController.class)
public class VirtualKeysControllerTest {
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private VirtualKeysService virtualKeysService;

    @Test
    void deleteVirtualKey() {
        Mockito.when(virtualKeysService.deleteVirtualKey(
                        Mockito.anyString(),
                        Mockito.any(it.pagopa.pn.bff.generated.openapi.virtualkey.apikey_pg.model.CxTypeAuthFleet.class),
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
                it.pagopa.pn.bff.generated.openapi.virtualkey.apikey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS
        );
    }
}
