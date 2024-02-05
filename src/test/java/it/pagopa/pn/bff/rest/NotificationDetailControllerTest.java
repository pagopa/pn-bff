package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.service.NotificationDetailService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebFluxTest(NotificationDetailController.class)
public class NotificationDetailControllerTest {
    private static final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    public static final String UID = "Uid";
    public static final String CX_ID = "CxId";
    public static final CxTypeAuthFleet CX_TYPE = CxTypeAuthFleet.PF;
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private NotificationDetailService notificationDetailService;

    void getReceivedNotification() {

        Mono<BffFullReceivedNotificationV23> notification = Mono.just(new BffFullReceivedNotificationV23());

        Mockito.when(notificationDetailService.getNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString()
                ))
                .thenReturn(notification);


        webTestClient.get()
//                .uri(uriBuilder ->
//                        uriBuilder
//                                .path("/" + PnBffRestConstants.NOTIFICATION_RECEIVED_PATH)
//                                .build(IUN))
                .uri("/bff/notifications/received/" + IUN)
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UID)
                .header(PnBffRestConstants.CX_ID_HEADER, CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffFullReceivedNotificationV23.class);

        Mockito.verify(notificationDetailService).getNotificationDetail(
                UID,
                CX_TYPE,
                CX_ID,
                null,
                IUN,
                null
        );
    }

    ;

    private Mono<BffFullReceivedNotificationV23> getFullNotification() {
        BffFullReceivedNotificationV23 notification = new BffFullReceivedNotificationV23();
        notification.setSentAt(OffsetDateTime.now());
        notification.setRecipients(
                List.of(
                        NotificationRecipientV23.builder()
                                .internalId("internalId")
                                .recipientType(NotificationRecipientV23.RecipientTypeEnum.PF)
                                .taxId("taxId")
                                .physicalAddress(NotificationPhysicalAddress.builder().build())
                                .digitalDomicile(NotificationDigitalAddress.builder().build())
                                .payments(List.of(NotificationPaymentItem.builder().build()))
                                .build()));
        notification.setIun("IUN_01");
        notification.setPaProtocolNumber("protocol_01");
        notification.setSubject("Subject 01");
        notification.setCancelledIun("IUN_05");
        notification.setCancelledIun("IUN_00");
        notification.setSenderPaId("PA_ID");
        notification.setNotificationStatus(BffNotificationStatus.ACCEPTED);
        notification.setRecipients(Collections.singletonList(
                NotificationRecipientV23.builder()
                        .taxId("Codice Fiscale 01")
                        .denomination("Nome Cognome/Ragione Sociale")
                        .internalId("recipientInternalId")
                        .digitalDomicile(NotificationDigitalAddress.builder()
                                .type(NotificationDigitalAddress.TypeEnum.PEC)
                                .address("account@dominio.it")
                                .build()).build()));

        log.info("notification: {}", notification);
        return Mono.just(notification);
    }
}
