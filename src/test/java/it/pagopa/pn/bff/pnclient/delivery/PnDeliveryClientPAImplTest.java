package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderReadWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.mocks.NotificationSentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnDeliveryClientPAImpl.class})
@ExtendWith(SpringExtension.class)
class PnDeliveryClientPAImplTest {
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationSentMock notificationSentMock = new NotificationSentMock();
    @Autowired
    private PnDeliveryClientPAImpl pnDeliveryClientPAImpl;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi")
    private SenderReadB2BApi senderReadB2BApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadWebApi")
    private SenderReadWebApi senderReadWebApi;

    @Test
    void getSentNotificationV23() throws RestClientException {
        when(senderReadB2BApi.getSentNotificationV23(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationDetailPaMock.getNotificationMultiRecipientMock()));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                NotificationSentMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationDetailPaMock.getNotificationMultiRecipientMock()).verifyComplete();
    }

    @Test
    void getSentNotificationV23Error() {
        when(senderReadB2BApi.getSentNotificationV23(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                NotificationSentMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS
        )).expectError(PnBffException.class).verify();
    }

    @Test
    void searchSentNotification() {
        when(senderReadWebApi.searchSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationSentMock.getNotificationSentPNMock()));

        StepVerifier.create(pnDeliveryClientPAImpl.searchSentNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.parse(NotificationSentMock.START_DATE),
                OffsetDateTime.parse(NotificationSentMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                NotificationSentMock.RECIPIENT_ID,
                NotificationStatus.ACCEPTED,
                NotificationSentMock.SUBJECT_REG_EXP,
                NotificationSentMock.IUN_MATCH,
                NotificationSentMock.SIZE,
                NotificationSentMock.NEXT_PAGES_KEY
        )).expectNext(notificationSentMock.getNotificationSentPNMock()).verifyComplete();
    }

    @Test
    void searchSentNotificationError() {
        when(senderReadWebApi.searchSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.searchSentNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.parse(NotificationSentMock.START_DATE),
                OffsetDateTime.parse(NotificationSentMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                NotificationSentMock.RECIPIENT_ID,
                NotificationStatus.ACCEPTED,
                NotificationSentMock.SUBJECT_REG_EXP,
                NotificationSentMock.IUN_MATCH,
                NotificationSentMock.SIZE,
                NotificationSentMock.NEXT_PAGES_KEY
        )).expectError(PnBffException.class).verify();
    }
}