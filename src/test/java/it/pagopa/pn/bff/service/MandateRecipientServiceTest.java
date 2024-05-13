package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto;
import it.pagopa.pn.bff.mappers.mandate.MandateCountMapper;
import it.pagopa.pn.bff.mocks.MandateMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.mandate.PnMandateClientRecipientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
}