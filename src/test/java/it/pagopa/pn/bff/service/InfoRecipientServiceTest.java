package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaSummary;
import it.pagopa.pn.bff.mappers.inforecipient.GroupsMapper;
import it.pagopa.pn.bff.mappers.inforecipient.PaListMapper;
import it.pagopa.pn.bff.mocks.RecipientInfoMock;
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

class InfoRecipientServiceTest {
    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final RecipientInfoMock recipientInfoMock = new RecipientInfoMock();
    private static InfoRecipientService infoRecipientService;

    @BeforeAll
    public static void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        infoRecipientService = new InfoRecipientService(pnExternalRegistriesClient, pnBffExceptionUtility);
    }

    @Test
    void getGroups() {
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
    void getGroupsError() {
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

    @Test
    void getPaList() {
        List<PaSummary> bffPaList = recipientInfoMock.getPaSummaryList()
                .stream()
                .map(PaListMapper.modelMapper::mapPaList)
                .toList();

        when(pnExternalRegistriesClient.getPaList(Mockito.nullable(String.class)))
                .thenReturn(Flux.fromIterable(recipientInfoMock.getPaSummaryList()));

        Flux<PaSummary> result = infoRecipientService.getPaList(UserMock.PN_CX_ID, CxTypeAuthFleet.PF, null);

        StepVerifier.create(result.collectList())
                .expectNext(bffPaList)
                .verifyComplete();
    }

    @Test
    void getPaListError() {
        when(pnExternalRegistriesClient.getPaList(Mockito.nullable(String.class)))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Flux<PaSummary> result = infoRecipientService.getPaList(UserMock.PN_CX_ID, CxTypeAuthFleet.PF, null);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void paListInvalidBodyError() {
        Flux<PaSummary> result = infoRecipientService.getPaList(UserMock.PN_CX_ID, CxTypeAuthFleet.PA, null);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && ((PnBffException) throwable).getProblem().getDetail().equals("Invalid request body")
                )
                .verify();
    }
}
