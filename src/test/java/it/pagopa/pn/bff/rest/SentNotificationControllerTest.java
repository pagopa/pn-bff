package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import it.pagopa.pn.bff.mappers.notification.NotificationSentMapper;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.mocks.NotificationSentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.NotificationDetailPAService;
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
@WebFluxTest(SentNotificationController.class)
class SentNotificationControllerTest {
    private final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    private final String RECIPIENT_ID = "PA-001";
    private final String STATUS = NotificationStatus.ACCEPTED.getValue();
    private final String SUBJECT_REGEXP = "test";
    private final String START_DATE = "2014-04-30T00:00:00.000Z";
    private final String END_DATE = "2024-04-30T00:00:00.000Z";
    private final int SIZE = 10;
    private final String NEXT_PAGES_KEY = "XXXYYYZZZ";


    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationSentMock notificationSentMock = new NotificationSentMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private NotificationDetailPAService notificationDetailPAService;
    @SpyBean
    private SentNotificationController sentNotificationController;

    @Test
    void getSentNotification() {
        BffFullNotificationV1 response = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDetailPaMock.getNotificationMultiRecipientMock());
        Mockito.when(notificationDetailPAService.getSentNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffFullNotificationV1.class)
                .isEqualTo(response);

        Mockito.verify(notificationDetailPAService).getSentNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationError() {
        Mockito.when(notificationDetailPAService.getSentNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationDetailPAService).getSentNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void searchSentNotification() {
        BffNotificationsResponseV1 response = NotificationSentMapper.modelMapper.toBffNotificationsResponseV1(notificationSentMock.getNotificationSentPNMock());
        Mockito.when(notificationDetailPAService.searchSentNotifications(
                        Mockito.any(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(NotificationStatus.class),
                        Mockito.anyString(),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.anyInt(),
                        Mockito.anyString()
                    )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SEARCH_PATH)
                                .queryParam("startDate", START_DATE)
                                .queryParam("endDate", END_DATE)
                                .queryParam("size", SIZE)
                                .queryParam("iunMatch", IUN)
                                .queryParam("recipientId", RECIPIENT_ID)
                                .queryParam("status", STATUS)
                                .queryParam("subjectRegExp", SUBJECT_REGEXP)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponseV1.class)
                .isEqualTo(response);

        Mockito.verify(notificationDetailPAService).searchSentNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                IUN,
                RECIPIENT_ID,
                NotificationStatus.ACCEPTED,
                "test",
                OffsetDateTime.parse(START_DATE),
                OffsetDateTime.parse(END_DATE),
                10,
                "XXXYYYZZZ"
        );
    }

    @Test
    void searchSentNotificationError() {
        Mockito.when(notificationDetailPAService.searchSentNotifications(
                        Mockito.any(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(NotificationStatus.class),
                        Mockito.anyString(),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.any(OffsetDateTime.class),
                        Mockito.anyInt(),
                        Mockito.anyString()
                )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SEARCH_PATH)
                                .queryParam("startDate", START_DATE)
                                .queryParam("endDate", END_DATE)
                                .queryParam("size", SIZE)
                                .queryParam("iunMatch", IUN)
                                .queryParam("recipientId", RECIPIENT_ID)
                                .queryParam("status", STATUS)
                                .queryParam("subjectRegExp", SUBJECT_REGEXP)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationDetailPAService).searchSentNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                IUN,
                RECIPIENT_ID,
                NotificationStatus.ACCEPTED,
                "test",
                OffsetDateTime.parse(START_DATE),
                OffsetDateTime.parse(END_DATE),
                10,
                "XXXYYYZZZ"
        );
    }
}