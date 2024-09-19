package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffPgGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.PaSummary;
import it.pagopa.pn.bff.mappers.inforecipient.GroupsMapper;
import it.pagopa.pn.bff.mappers.inforecipient.PaListMapper;
import it.pagopa.pn.bff.mocks.RecipientInfoMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.InfoRecipientService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@WebFluxTest(InfoRecipientController.class)
class InfoRecipientControllerTest {
    private final RecipientInfoMock recipientInfoMock = new RecipientInfoMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private InfoRecipientService infoRecipientService;

    @Test
    void getGroupsV1() {
        List<BffPgGroup> bffPgGroups = recipientInfoMock.getPgGroupsMock()
                .stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();
        Mockito
                .when(infoRecipientService.getGroups(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(BffPgGroupStatus.class)))
                .thenReturn(Flux.fromIterable(bffPgGroups));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.GROUPS_PG_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffPgGroup.class)
                .isEqualTo(bffPgGroups);
    }

    @Test
    void getGroupsV1Error() {
        Mockito
                .when(infoRecipientService.getGroups(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(BffPgGroupStatus.class)))
                .thenReturn(Flux.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.GROUPS_PG_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getPaListV1() {
        List<PaSummary> bffPaList = recipientInfoMock.getPaSummaryList()
                .stream()
                .map(PaListMapper.modelMapper::mapPaList)
                .toList();

        Mockito
                .when(infoRecipientService.getPaList(Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.nullable(String.class)))
                .thenReturn(Flux.fromIterable(bffPaList));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.PA_LIST).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PaSummary.class)
                .isEqualTo(bffPaList);
    }

    @Test
    void getPaListV1Error() {
        Mockito
                .when(infoRecipientService.getPaList(Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.nullable(String.class)))
                .thenReturn(Flux.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.PA_LIST).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void wrongCxTypePaListError() {
        Mockito.when(infoRecipientService.getPaList(Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.nullable(String.class)))
                .thenThrow(new PnBffException("Forbidden", "Forbidden", 403, "FORBIDDEN"));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.PA_LIST).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .exchange()
                .expectStatus().isForbidden();
    }
}