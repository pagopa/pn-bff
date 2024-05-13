package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffMandatesCount;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewMandateRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.mandate.MandateCountMapper;
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
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(MandateRecipientController.class)
class MandateRecipientControllerTest {
    private final MandateMock mandateMock = new MandateMock();
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
                                .path(PnBffRestConstants.MANDATE_PATH + "/count-by-delegate")
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
                                .path(PnBffRestConstants.MANDATE_PATH + "/count-by-delegate")
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
                .isOk()
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
                                .path(PnBffRestConstants.MANDATE_PATH + "/count-by-delegate")
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
}