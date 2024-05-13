package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupsServiceTest {
    private static GroupsService groupsService;
    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;

    private final UserMock userMock = new UserMock();


    @BeforeAll
    public static void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        groupsService = new GroupsService(pnExternalRegistriesClient, pnBffExceptionUtility);
    }

    @Test
    void getGroups(){
        List<PaGroup> groups = userMock.getPaGroupsMock()
                .stream()
                .map(it.pagopa.pn.bff.mappers.groups.GroupsMapper.modelMapper::mapGroups)
                .toList();

        when(pnExternalRegistriesClient.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(PaGroupStatus.class)
        )).thenReturn(Flux.fromIterable(userMock.getPaGroupsMock()));

        Flux<PaGroup> result = groupsService.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                null
        );

        StepVerifier.create(result.collectList())
                .expectNext(groups)
                .verifyComplete();
    }

    @Test
    void getGroupsError(){
        when(pnExternalRegistriesClient.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(PaGroupStatus.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Flux<PaGroup> result = groupsService.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                null
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

}
