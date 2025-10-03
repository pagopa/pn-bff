package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.*;
import it.pagopa.pn.bff.mappers.mandate.MandateCountMapper;
import it.pagopa.pn.bff.mappers.mandate.MandatesMapper;
import it.pagopa.pn.bff.mappers.mandate.SearchMandateByDelegateMapper;
import it.pagopa.pn.bff.mocks.MandateMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.MandateRecipientService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(MandateRecipientController.class)
class MandateRecipientControllerTest {
    private final MandateMock mandateMock = new MandateMock();
    private final String mandateId = "47eb5694-0dab-406c-aee0-a0f8005f3314";
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private MandateRecipientService mandateRecipientService;

    @Test
    void countMandatesByDelegate() {
        BffMandatesCount response = MandateCountMapper.modelMapper.mapCount(mandateMock.getCountMock());
        Mockito.when(mandateRecipientService.countMandatesByDelegate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegate/count")
                                .queryParam("status", "pending")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffMandatesCount.class)
                .isEqualTo(response);

        Mockito.verify(mandateRecipientService).countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "pending"
        );
    }

    @Test
    void countMandatesByDelegateError() {
        Mockito.when(mandateRecipientService.countMandatesByDelegate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegate/count")
                                .queryParam("status", "pending")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "pending"
        );
    }

    @Test
    void createMandate() {
        BffNewMandateRequest request = mandateMock.getBffNewMandateRequestMock();
        Mockito.when(mandateRecipientService.createMandate(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.empty());


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Void.class);

        Mockito.verify(mandateRecipientService).createMandate(
                eq(UserMock.PN_UID),
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void createMandateError() {
        BffNewMandateRequest request = mandateMock.getBffNewMandateRequestMock();
        Mockito.when(mandateRecipientService.createMandate(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).createMandate(
                eq(UserMock.PN_UID),
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void acceptMandate() {
        BffAcceptRequest request = mandateMock.getBffAcceptRequestMock();
        Mockito.when(mandateRecipientService.acceptMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.empty());


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/accept")
                                .build(mandateId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);

        Mockito.verify(mandateRecipientService).acceptMandate(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(mandateId),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void acceptMandateError() {
        BffAcceptRequest request = mandateMock.getBffAcceptRequestMock();
        Mockito.when(mandateRecipientService.acceptMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/accept")
                                .build(mandateId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).acceptMandate(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(mandateId),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void updateMandate() {
        BffUpdateRequest request = mandateMock.getBffUpdateRequestMock();
        Mockito.when(mandateRecipientService.updateMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.empty());


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/update")
                                .build(mandateId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);

        Mockito.verify(mandateRecipientService).updateMandate(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(mandateId),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void updateMandateError() {
        BffUpdateRequest request = mandateMock.getBffUpdateRequestMock();
        Mockito.when(mandateRecipientService.updateMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/update")
                                .build(mandateId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).updateMandate(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(mandateId),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void rejectMandate() {
        Mockito.when(mandateRecipientService.rejectMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.empty());


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/reject")
                                .build(mandateId))
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);

        Mockito.verify(mandateRecipientService).rejectMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }

    @Test
    void rejectMandateError() {
        Mockito.when(mandateRecipientService.rejectMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/reject")
                                .build(mandateId))
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).rejectMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }

    @Test
    void revokeMandate() {
        Mockito.when(mandateRecipientService.revokeMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.empty());


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/revoke")
                                .build(mandateId))
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);

        Mockito.verify(mandateRecipientService).revokeMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }

    @Test
    void revokeMandateError() {
        Mockito.when(mandateRecipientService.revokeMandate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/{mandateId}/revoke")
                                .build(mandateId))
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).revokeMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }

    @Test
    void getMandatesByDelegate() {
        List<BffMandate> response = mandateMock.getMandatesByDelegateMock()
                .stream()
                .map(MandatesMapper.modelMapper::mapMandateByDelegate)
                .toList();
        Mockito.when(mandateRecipientService.getMandatesByDelegate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString()
                ))
                .thenReturn(Flux.fromIterable(response));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegate")
                                .queryParam("status", "pending")
                                .build())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BffMandate.class)
                .isEqualTo(response);

        Mockito.verify(mandateRecipientService).getMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "pending"
        );
    }

    @Test
    void getMandatesByDelegateError() {
        Mockito.when(mandateRecipientService.getMandatesByDelegate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString()
                ))
                .thenReturn(Flux.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegate")
                                .queryParam("status", "pending")
                                .build())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).getMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "pending"
        );
    }

    @Test
    void searchMandatesByDelegate() {
        BffSearchMandateRequest request = mandateMock.getBffSearchMandatesByDelegateRequestMock();
        BffSearchMandateResponse response = SearchMandateByDelegateMapper.modelMapper.mapResponse(mandateMock.getSearchMandatesByDelegateResponseMock());
        Mockito.when(mandateRecipientService.searchMandatesByDelegate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyInt(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegate")
                                .queryParam("size", "10")
                                .queryParam("nextPageKey", "NEXT_PAGE")
                                .build())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffSearchMandateResponse.class)
                .isEqualTo(response);

        Mockito.verify(mandateRecipientService).searchMandatesByDelegate(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(10),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                eq("NEXT_PAGE"),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void searchMandatesByDelegateError() {
        BffSearchMandateRequest request = mandateMock.getBffSearchMandatesByDelegateRequestMock();
        Mockito.when(mandateRecipientService.searchMandatesByDelegate(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyInt(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegate")
                                .queryParam("size", "10")
                                .queryParam("nextPageKey", "NEXT_PAGE")
                                .build())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).searchMandatesByDelegate(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(10),
                eq(UserMock.PN_CX_GROUPS),
                eq(UserMock.PN_CX_ROLE),
                eq("NEXT_PAGE"),
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void getMandatesByDelegator() {
        List<BffMandate> response = mandateMock.getMandatesByDelegatorMock()
                .stream()
                .map(MandatesMapper.modelMapper::mapMandateByDelegator)
                .toList();
        Mockito.when(mandateRecipientService.getMandatesByDelegator(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString()
                ))
                .thenReturn(Flux.fromIterable(response));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegator")
                                .build())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BffMandate.class)
                .isEqualTo(response);

        Mockito.verify(mandateRecipientService).getMandatesByDelegator(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }

    @Test
    void getMandatesByDelegatorError() {
        Mockito.when(mandateRecipientService.getMandatesByDelegator(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyList(),
                        Mockito.anyString()
                ))
                .thenReturn(Flux.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.MANDATE_PATH + "/delegator")
                                .build())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(mandateRecipientService).getMandatesByDelegator(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }
}