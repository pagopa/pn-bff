package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup;
import it.pagopa.pn.bff.mappers.groups.GroupsMapper;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.GroupsService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@WebFluxTest(GroupsController.class)
public class GroupsControllerTest {
    private final UserMock userMock = new UserMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private GroupsService groupsService;
    @SpyBean
    private GroupsController groupsController;

    @Test
    void getGroupsV1() {
        List<PaGroup> paGroups = userMock.getPaGroupsMock()
                .stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();
        Mockito
                .when(groupsService.getGroups(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(BffPaGroupStatus.class)))
                .thenReturn(Flux.fromIterable(paGroups));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.GROUPS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PaGroup.class)
                .isEqualTo(paGroups);
    }

    @Test
    void getGroupsV1Error(){
        Mockito
                .when(groupsService.getGroups(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(BffPaGroupStatus.class)
                ))
                .thenReturn(Flux.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.GROUPS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus().isNotFound();
    }

}
