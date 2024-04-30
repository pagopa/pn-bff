package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
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
    private final String SENDER_ID = "PA-001";
    private final String MANDATE_ID = "MANDATE-001";
    private final String RECIPIENT_ID = "RECIPIENT-001";
    private final String GROUP = "GROUP";
    private final NotificationStatus STATUS = NotificationStatus.ACCEPTED;
    private final String SUBJECT_REGEXP = "test";
    private final String START_DATE = "2014-04-30T00:00:00.000Z";
    private final String END_DATE = "2024-04-30T00:00:00.000Z";
    private final int SIZE = 10;
    private final String NEXT_PAGES_KEY = "XXXYYYZZZ";
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
        BffNotificationsResponseV1 response = NotificationReceivedMapper.modelMapper.toBffNotificationsResponseV1(notificationReceivedMock.getNotificationReceivedPNMock());
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
                                .queryParam("iunMatch", IUN)
                                .queryParam("mandateId", MANDATE_ID)
                                .queryParam("senderId", SENDER_ID)
                                .queryParam("status", STATUS.getValue())
                                .queryParam("startDate", START_DATE)
                                .queryParam("endDate", END_DATE)
                                .queryParam("subjectRegExp", SUBJECT_REGEXP)
                                .queryParam("size", SIZE)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponseV1.class)
                .isEqualTo(response);

        Mockito.verify(notificationDetailRecipientService).searchReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS,
                MANDATE_ID,
                SENDER_ID,
                STATUS,
                OffsetDateTime.parse(START_DATE),
                OffsetDateTime.parse(END_DATE),
                SUBJECT_REGEXP,
                SIZE,
                NEXT_PAGES_KEY
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
                                .queryParam("iunMatch", IUN)
                                .queryParam("mandateId", MANDATE_ID)
                                .queryParam("senderId", SENDER_ID)
                                .queryParam("status", STATUS)
                                .queryParam("startDate", START_DATE)
                                .queryParam("endDate", END_DATE)
                                .queryParam("subjectRegExp", SUBJECT_REGEXP)
                                .queryParam("size", SIZE)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
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
                IUN,
                UserMock.PN_CX_GROUPS,
                MANDATE_ID,
                SENDER_ID,
                STATUS,
                OffsetDateTime.parse(START_DATE),
                OffsetDateTime.parse(END_DATE),
                SUBJECT_REGEXP,
                SIZE,
                NEXT_PAGES_KEY
        );
    }

    @Test
    void searchReceivedDelegatedNotifications() {
        BffNotificationsResponseV1 response = NotificationReceivedMapper.modelMapper.toBffNotificationsResponseV1(notificationReceivedMock.getNotificationReceivedPNMock());
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
                                .queryParam("iunMatch", IUN)
                                .queryParam("senderId", SENDER_ID)
                                .queryParam("recipientId", RECIPIENT_ID)
                                .queryParam("group", GROUP)
                                .queryParam("status", STATUS.getValue())
                                .queryParam("startDate", START_DATE)
                                .queryParam("endDate", END_DATE)
                                .queryParam("size", SIZE)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponseV1.class)
                .isEqualTo(response);

        Mockito.verify(notificationDetailRecipientService).searchReceivedDelegatedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS,
                SENDER_ID,
                RECIPIENT_ID,
                GROUP,
                STATUS,
                OffsetDateTime.parse(START_DATE),
                OffsetDateTime.parse(END_DATE),
                SIZE,
                NEXT_PAGES_KEY
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
                                .queryParam("iunMatch", IUN)
                                .queryParam("senderId", SENDER_ID)
                                .queryParam("recipientId", RECIPIENT_ID)
                                .queryParam("group", GROUP)
                                .queryParam("status", STATUS)
                                .queryParam("startDate", START_DATE)
                                .queryParam("endDate", END_DATE)
                                .queryParam("size", SIZE)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
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
                IUN,
                UserMock.PN_CX_GROUPS,
                SENDER_ID,
                RECIPIENT_ID,
                GROUP,
                STATUS,
                OffsetDateTime.parse(START_DATE),
                OffsetDateTime.parse(END_DATE),
                SIZE,
                NEXT_PAGES_KEY
        );
    }
}