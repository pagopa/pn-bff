package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {NotificationDetailRecipientService.class})
class NotificationDetailRecipientServiceTest {
    @Autowired
    private NotificationDetailRecipientService notificationDetailRecipientService;
    private PnDeliveryClientRecipientImpl pnDeliveryClient;
    NotificationDetailMapper modelMapperMock = mock(NotificationDetailMapper.class);

    @BeforeEach
    void setup() {
        this.pnDeliveryClient = mock(PnDeliveryClientRecipientImpl.class);

        this.notificationDetailRecipientService = new NotificationDetailRecipientService(pnDeliveryClient);
    }

    @Test
    void testGetNotificationDetail() {
        when(pnDeliveryClient.getReceivedNotification(Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any(),
                Mockito.<String>any()
        )).thenReturn(Mono.just(new FullReceivedNotificationV23()));

        when(modelMapperMock.mapReceivedNotificationDetail(Mockito.<FullReceivedNotificationV23>any()))
                .thenReturn(new BffFullNotificationV1());

        notificationDetailRecipientService.getNotificationDetail(
                "UID",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                "CX_ID",
                null,
                "IUN",
                "MANDATE_ID"
        );

        Mockito.verify(pnDeliveryClient).getReceivedNotification(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any(),
                Mockito.<String>any()
        );
    }

    @Test
    void testGetNotificationDetailIunNotFound() {
        when(pnDeliveryClient.getReceivedNotification(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any(),
                Mockito.<String>any()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(notificationDetailRecipientService.getNotificationDetail(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "CX_ID",
                        null,
                        "IUN",
                        "MANDATE_ID"
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}