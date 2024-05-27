package it.pagopa.pn.bff.pnclient.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.api.MandateServiceApi;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.*;
import it.pagopa.pn.bff.mocks.MandateMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnMandateClientRecipientImpl.class})
@ExtendWith(SpringExtension.class)
class PnMandateClientRecipientImplTest {
    private final MandateMock mandateMock = new MandateMock();
    @Autowired
    private PnMandateClientRecipientImpl pnMandateClient;
    @MockBean
    private MandateServiceApi mandateApi;

    @Test
    void countMandatesByDelegate() {
        when(mandateApi.countMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.just(mandateMock.getCountMock()));

        StepVerifier.create(pnMandateClient.countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectNext(mandateMock.getCountMock()).verifyComplete();
    }

    @Test
    void countMandatesByDelegateError() {
        when(mandateApi.countMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void createMandate() {
        when(mandateApi.createMandate(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(MandateDto.class)
        )).thenReturn(Mono.just(mandateMock.getNewMandateResponseMock()));

        StepVerifier.create(pnMandateClient.createMandate(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getNewMandateRequestMock()
        )).expectNext(mandateMock.getNewMandateResponseMock()).verifyComplete();
    }

    @Test
    void createMandateError() {
        when(mandateApi.createMandate(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(MandateDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.createMandate(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getNewMandateRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void acceptMandate() {
        when(mandateApi.acceptMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(AcceptRequestDto.class)
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnMandateClient.acceptMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getAcceptRequestMock()
        )).expectNext().verifyComplete();
    }

    @Test
    void acceptMandateError() {
        when(mandateApi.acceptMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(AcceptRequestDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.acceptMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getAcceptRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void updateMandate() {
        when(mandateApi.updateMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(UpdateRequestDto.class)
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnMandateClient.updateMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getUpdateRequestMock()
        )).expectNext().verifyComplete();
    }

    @Test
    void updateMandateError() {
        when(mandateApi.updateMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(UpdateRequestDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.updateMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getUpdateRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void rejectMandate() {
        when(mandateApi.rejectMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnMandateClient.rejectMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void rejectMandateError() {
        when(mandateApi.rejectMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.rejectMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void revokeMandate() {
        when(mandateApi.revokeMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnMandateClient.revokeMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void revokeMandateError() {
        when(mandateApi.revokeMandate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.revokeMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                "MANDATE_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getMandatesByDelegate() {
        when(mandateApi.listMandatesByDelegate1(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Flux.fromIterable(mandateMock.getMandatesByDelegateMock()));

        StepVerifier.create(pnMandateClient.getMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectNextSequence(mandateMock.getMandatesByDelegateMock()).verifyComplete();
    }

    @Test
    void getMandatesByDelegateError() {
        when(mandateApi.listMandatesByDelegate1(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.getMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void searchMandatesByDelegate() {
        when(mandateApi.searchMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(SearchMandateRequestDto.class)
        )).thenReturn(Mono.just(mandateMock.getSearchMandatesByDelegateResponseMock()));

        StepVerifier.create(pnMandateClient.searchMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                10,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "NEXT_PAGE",
                mandateMock.getSearchMandatesByDelegateRequestMock()
        )).expectNext(mandateMock.getSearchMandatesByDelegateResponseMock()).verifyComplete();
    }

    @Test
    void searchMandatesByDelegateError() {
        when(mandateApi.searchMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(SearchMandateRequestDto.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.searchMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                10,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "NEXT_PAGE",
                mandateMock.getSearchMandatesByDelegateRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getMandatesByDelegator() {
        when(mandateApi.listMandatesByDelegator1(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.fromIterable(mandateMock.getMandatesByDelegatorMock()));

        StepVerifier.create(pnMandateClient.getMandatesByDelegator(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNextSequence(mandateMock.getMandatesByDelegatorMock()).verifyComplete();
    }

    @Test
    void getMandatesByDelegatorError() {
        when(mandateApi.listMandatesByDelegator1(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.getMandatesByDelegator(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }
}