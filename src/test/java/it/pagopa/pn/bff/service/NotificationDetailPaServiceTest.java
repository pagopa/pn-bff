package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {NotificationDetailPAService.class})
class NotificationDetailPaServiceTest {
    @Autowired
    private NotificationDetailPAService notificationDetailPAService;
    private PnDeliveryClientPAImpl pnDeliveryClientPA;
    NotificationDetailMapper modelMapperMock = mock(NotificationDetailMapper.class);

    @BeforeEach
    void setup() {
        this.pnDeliveryClientPA = mock(PnDeliveryClientPAImpl.class);

        this.notificationDetailPAService = new NotificationDetailPAService(pnDeliveryClientPA);
    }

    @Test
    void testGetNotificationDetail() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any()
        )).thenReturn(Mono.just(new FullSentNotificationV23()));

        when(modelMapperMock.mapSentNotificationDetail(any(FullSentNotificationV23.class)))
                .thenReturn(new BffFullNotificationV1());

        notificationDetailPAService.getSentNotificationDetail(
                "UID",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                "CX_ID",
                "IUN",
                null
        );

        Mockito.verify(pnDeliveryClientPA).getSentNotification(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any()
        );
    }

    @Test
    void testGetNotificationDetailIunNotFound() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(notificationDetailPAService.getSentNotificationDetail(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        "CX_ID",
                        "IUN",
                        null
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}