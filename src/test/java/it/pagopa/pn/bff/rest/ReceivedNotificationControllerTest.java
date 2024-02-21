package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
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
public class ReceivedNotificationControllerTest {
    private static final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    public static final String UID = "Uid";
    public static final String CX_ID = "CxId";
    public static final CxTypeAuthFleet CX_TYPE = CxTypeAuthFleet.PF;
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private NotificationDetailRecipientService notificationDetailRecipientService;
    @SpyBean
    private ReceivedNotificationController receivedNotificationController;

    @Test
    void getReceivedNotification() {

        Mockito.when(notificationDetailRecipientService.getNotificationDetail(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<java.util.List<String>>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any()
                ))
                .thenReturn(Mono.just(new BffFullNotificationV1()));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UID)
                .header(PnBffRestConstants.CX_ID_HEADER, CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffFullNotificationV1.class);

        Mockito.verify(notificationDetailRecipientService).getNotificationDetail(
                UID,
                CX_TYPE,
                CX_ID,
                null,
                IUN,
                null
        );
    }

    @Test
    void getReceivedNotificationError() {
        Mockito.when(notificationDetailRecipientService.getNotificationDetail(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<java.util.List<String>>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any()
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UID)
                .header(PnBffRestConstants.CX_ID_HEADER, CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationDetailRecipientService).getNotificationDetail(
                UID,
                CX_TYPE,
                CX_ID,
                null,
                IUN,
                null
        );
    }
}