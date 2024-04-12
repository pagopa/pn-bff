package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.NotificationDetailRecipientService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@WebFluxTest(ReceivedNotificationController.class)
class ReceivedNotificationControllerTest {
    private static final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private NotificationDetailRecipientService notificationDetailRecipientService;
    @SpyBean
    private ReceivedNotificationController receivedNotificationController;

    @Test
    void getReceivedNotification() {
        BffFullNotificationV1 response = NotificationDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationDetailRecipientMock.getNotificationMultiRecipientMock());
        Mockito.when(notificationDetailRecipientService.getNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.any()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffFullNotificationV1.class)
                .isEqualTo(response);

        Mockito.verify(notificationDetailRecipientService).getNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationError() {
        Mockito.when(notificationDetailRecipientService.getNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.any()
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationDetailRecipientService).getNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS,
                null
        );
    }
}