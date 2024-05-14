package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffMandate;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateResponse;
import it.pagopa.pn.bff.mappers.mandate.MandateCountMapper;
import it.pagopa.pn.bff.mappers.mandate.MandatesByDelegateMapper;
import it.pagopa.pn.bff.mappers.mandate.SearchMandateByDelegateMapper;
import it.pagopa.pn.bff.mocks.MandateMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.mandate.PnMandateClientRecipientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MandateRecipientServiceTest {
    private static MandateRecipientService mandateRecipientService;
    private static PnMandateClientRecipientImpl pnMandateClientRecipient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    MandateMock mandateMock = new MandateMock();

    @BeforeAll
    public static void setup() {
        pnMandateClientRecipient = mock(PnMandateClientRecipientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        mandateRecipientService = new MandateRecipientService(pnMandateClientRecipient, pnBffExceptionUtility);
    }

    @Test
    void countMandatesByDelegate() {
        when(pnMandateClientRecipient.countMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.just(mandateMock.getCountMock()));

        StepVerifier.create(mandateRecipientService.countMandatesByDelegate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        "STATUS"
                ))
                .expectNext(MandateCountMapper.modelMapper.mapCount(mandateMock.getCountMock()))
                .verifyComplete();
    }

    @Test
    void countMandatesByDelegateError() {
        when(pnMandateClientRecipient.countMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.countMandatesByDelegate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        "STATUS"
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void createMandate() {
        when(pnMandateClientRecipient.createMandate(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(MandateDto.class)
        )).thenReturn(Mono.just(mandateMock.getNewMandateResponseMock()));

        StepVerifier.create(mandateRecipientService.createMandate(
                        UserMock.PN_UID,
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        Mono.just(mandateMock.getBffNewMandateRequestMock())
                ))
                .expectNext()
                .verifyComplete();
    }

    @Test
    void createMandateError() {
        when(pnMandateClientRecipient.createMandate(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(MandateDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.createMandate(
                        UserMock.PN_UID,
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        Mono.just(mandateMock.getBffNewMandateRequestMock())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void acceptMandate() {
        when(pnMandateClientRecipient.acceptMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(AcceptRequestDto.class)
        )).thenReturn(Mono.empty());

        StepVerifier.create(mandateRecipientService.acceptMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        Mono.just(mandateMock.getBffAcceptRequestMock())
                ))
                .expectNext()
                .verifyComplete();
    }

    @Test
    void acceptMandateError() {
        when(pnMandateClientRecipient.acceptMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(AcceptRequestDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.acceptMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        Mono.just(mandateMock.getBffAcceptRequestMock())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void updateMandate() {
        when(pnMandateClientRecipient.updateMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(UpdateRequestDto.class)
        )).thenReturn(Mono.empty());

        StepVerifier.create(mandateRecipientService.updateMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        Mono.just(mandateMock.getBffUpdateRequestMock())
                ))
                .expectNext()
                .verifyComplete();
    }

    @Test
    void updateMandateError() {
        when(pnMandateClientRecipient.updateMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(UpdateRequestDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.updateMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        Mono.just(mandateMock.getBffUpdateRequestMock())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void rejectMandate() {
        when(pnMandateClientRecipient.rejectMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(mandateRecipientService.rejectMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE
                ))
                .expectNext()
                .verifyComplete();
    }

    @Test
    void rejectMandateError() {
        when(pnMandateClientRecipient.rejectMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.rejectMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void revokeMandate() {
        when(pnMandateClientRecipient.revokeMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(mandateRecipientService.revokeMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE
                ))
                .expectNext()
                .verifyComplete();
    }

    @Test
    void revokeMandateError() {
        when(pnMandateClientRecipient.revokeMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.revokeMandate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "MANDATE_ID",
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getMandatesByDelegate() {
        List<BffMandate> response = mandateMock.getMandatesByDelegateMock()
                .stream()
                .map(MandatesByDelegateMapper.modelMapper::mapMandate)
                .toList();
        when(pnMandateClientRecipient.getMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Flux.fromIterable(mandateMock.getMandatesByDelegateMock()));

        StepVerifier.create(mandateRecipientService.getMandatesByDelegate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        "STATUS"
                ))
                .expectNextSequence(response)
                .verifyComplete();
    }

    @Test
    void getMandatesByDelegateError() {
        when(pnMandateClientRecipient.getMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.getMandatesByDelegate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        "STATUS"
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void searchMandatesByDelegate() {
        BffSearchMandateResponse response = SearchMandateByDelegateMapper.modelMapper.mapResponse(mandateMock.getSearchMandatesByDelegateResponseMock());
        when(pnMandateClientRecipient.searchMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(SearchMandateRequestDto.class)
        )).thenReturn(Mono.just(mandateMock.getSearchMandatesByDelegateResponseMock()));

        StepVerifier.create(mandateRecipientService.searchMandatesByDelegate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        10,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        "NEXT_PAGE",
                        Mono.just(mandateMock.getBffSearchMandatesByDelegateRequestMock())
                ))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void searchMandatesByDelegateError() {
        when(pnMandateClientRecipient.searchMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(SearchMandateRequestDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.searchMandatesByDelegate(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        10,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE,
                        "NEXT_PAGE",
                        Mono.just(mandateMock.getBffSearchMandatesByDelegateRequestMock())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getMandatesByDelegator() {
        List<BffMandate> response = mandateMock.getMandatesByDelegatorMock()
                .stream()
                .map(MandatesByDelegateMapper.modelMapper::mapMandate)
                .toList();
        when(pnMandateClientRecipient.getMandatesByDelegator(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.fromIterable(mandateMock.getMandatesByDelegatorMock()));

        StepVerifier.create(mandateRecipientService.getMandatesByDelegator(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE
                ))
                .expectNextSequence(response)
                .verifyComplete();
    }

    @Test
    void getMandatesByDelegatorError() {
        when(pnMandateClientRecipient.getMandatesByDelegator(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(mandateRecipientService.getMandatesByDelegator(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_GROUPS,
                        UserMock.PN_CX_ROLE
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}