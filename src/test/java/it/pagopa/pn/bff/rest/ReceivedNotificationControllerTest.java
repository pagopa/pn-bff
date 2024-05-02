package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import it.pagopa.pn.bff.mappers.notification.NotificationReceivedMapper;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationReceivedMock;
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

import java.time.OffsetDateTime;

@Slf4j
@WebFluxTest(ReceivedNotificationController.class)
class ReceivedNotificationControllerTest {
    private static final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationReceivedMock notificationReceivedMock = new NotificationReceivedMock();
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
    @Test
    void searchReceivedNotifications() {
        BffNotificationsResponse response = NotificationReceivedMapper.modelMapper.toBffNotificationsResponse(notificationReceivedMock.getNotificationReceivedPNMock());
        Mockito.when(notificationDetailRecipientService.searchReceivedNotification(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(NotificationStatus.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.anyString(),
                        Mockito.anyInt(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_SEARCH_PATH)
                                .queryParam("iunMatch", UserMock.IUN_MATCH)
                                .queryParam("mandateId", UserMock.MANDATE_ID)
                                .queryParam("senderId", UserMock.SENDER_ID)
                                .queryParam("status", UserMock.STATUS.getValue())
                                .queryParam("startDate", UserMock.START_DATE)
                                .queryParam("endDate", UserMock.END_DATE)
                                .queryParam("subjectRegExp", UserMock.SUBJECT_REG_EXP)
                                .queryParam("size", UserMock.SIZE)
                                .queryParam("nextPagesKey", UserMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationDetailRecipientService).searchReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID,
                UserMock.SENDER_ID,
                UserMock.STATUS,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SUBJECT_REG_EXP,
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchReceivedNotificationsError() {
        Mockito.when(notificationDetailRecipientService.searchReceivedNotification(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(NotificationStatus.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.anyString(),
                        Mockito.anyInt(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_SEARCH_PATH)
                                .queryParam("iunMatch", UserMock.IUN_MATCH)
                                .queryParam("mandateId", UserMock.MANDATE_ID)
                                .queryParam("senderId", UserMock.SENDER_ID)
                                .queryParam("status", UserMock.STATUS.getValue())
                                .queryParam("startDate", UserMock.START_DATE)
                                .queryParam("endDate", UserMock.END_DATE)
                                .queryParam("subjectRegExp", UserMock.SUBJECT_REG_EXP)
                                .queryParam("size", UserMock.SIZE)
                                .queryParam("nextPagesKey", UserMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationDetailRecipientService).searchReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID,
                UserMock.SENDER_ID,
                UserMock.STATUS,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SUBJECT_REG_EXP,
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchReceivedDelegatedNotifications() {
        BffNotificationsResponse response = NotificationReceivedMapper.modelMapper.toBffNotificationsResponse(notificationReceivedMock.getNotificationReceivedPNMock());
        Mockito.when(notificationDetailRecipientService.searchReceivedDelegatedNotification(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(NotificationStatus.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.anyInt(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DELEGATED_PATH)
                                .queryParam("iunMatch", UserMock.IUN_MATCH)
                                .queryParam("senderId", UserMock.SENDER_ID)
                                .queryParam("recipientId", UserMock.RECIPIENT_ID)
                                .queryParam("group", UserMock.GROUP)
                                .queryParam("status", UserMock.STATUS.getValue())
                                .queryParam("startDate", UserMock.START_DATE)
                                .queryParam("endDate", UserMock.END_DATE)
                                .queryParam("size", UserMock.SIZE)
                                .queryParam("nextPagesKey", UserMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationDetailRecipientService).searchReceivedDelegatedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.SENDER_ID,
                UserMock.RECIPIENT_ID,
                UserMock.GROUP,
                UserMock.STATUS,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchReceivedDelegatedNotificationsError() {
        Mockito.when(notificationDetailRecipientService.searchReceivedDelegatedNotification(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(NotificationStatus.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.anyInt(),
                        Mockito.anyString()
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DELEGATED_PATH)
                                .queryParam("iunMatch", UserMock.IUN_MATCH)
                                .queryParam("senderId", UserMock.SENDER_ID)
                                .queryParam("recipientId", UserMock.RECIPIENT_ID)
                                .queryParam("group", UserMock.GROUP)
                                .queryParam("status", UserMock.STATUS.getValue())
                                .queryParam("startDate", UserMock.START_DATE)
                                .queryParam("endDate", UserMock.END_DATE)
                                .queryParam("size", UserMock.SIZE)
                                .queryParam("nextPagesKey", UserMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationDetailRecipientService).searchReceivedDelegatedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.SENDER_ID,
                UserMock.RECIPIENT_ID,
                UserMock.GROUP,
                UserMock.STATUS,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );
    }
}