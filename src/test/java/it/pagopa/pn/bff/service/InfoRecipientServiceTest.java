package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroup;
import it.pagopa.pn.bff.mappers.inforecipient.GroupsMapper;
import it.pagopa.pn.bff.mocks.RecipientInfoMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InfoRecipientServiceTest {
    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final RecipientInfoMock recipientInfoMock = new RecipientInfoMock();
    private final UserMock userMock = new UserMock();
    private InfoRecipientService infoRecipientService;

    @BeforeAll
    public void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        infoRecipientService = new InfoRecipientService(pnExternalRegistriesClient, pnBffExceptionUtility);
    }

    @Test
    void getGroups(){
        List<BffPgGroup> bffPgGroups = recipientInfoMock.getPgGroupsMock()
                .stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();

        when(pnExternalRegistriesClient.getPgGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(PgGroupStatus.class)
        )).thenReturn(Flux.fromIterable(recipientInfoMock.getPgGroupsMock()));

        Flux<BffPgGroup> result = infoRecipientService.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                null
        );

        StepVerifier.create(result.collectList())
                .expectNext(bffPgGroups)
                .verifyComplete();
    }

    @Test
    void getGroupsError(){
        when(pnExternalRegistriesClient.getPgGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(PgGroupStatus.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Flux<BffPgGroup> result = infoRecipientService.getGroups(
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
