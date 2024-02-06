package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV23;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationDetailServiceTest {
    @Autowired
    private NotificationDetailService notificationDetailService;
    private PnDeliveryClientImpl pnDeliveryClient;
    private NotificationDetailMapper notificationDetailMapper;


    @BeforeEach
    void setup() {
        this.pnDeliveryClient = mock(PnDeliveryClientImpl.class);
        this.notificationDetailMapper = mock(NotificationDetailMapper.class);

        this.notificationDetailService = new NotificationDetailService(pnDeliveryClient, notificationDetailMapper);
    }

    @Test
    void testGetNotificationDetail() {
        when(pnDeliveryClient.retrieveNotification(Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any(),
                Mockito.<String>any()
        )).thenReturn(Mono.just(new FullReceivedNotificationV23()));

        when(notificationDetailMapper.mapNotificationDetail(Mockito.<FullReceivedNotificationV23>any()))
                .thenReturn(new BffFullReceivedNotificationV23());

        notificationDetailService.getNotificationDetail(
                "UID",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                "CX_ID",
                null,
                "IUN",
                "MANDATE_ID"
        );

        Mockito.verify(pnDeliveryClient).retrieveNotification(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any(),
                Mockito.<String>any()
        );
    }
}
