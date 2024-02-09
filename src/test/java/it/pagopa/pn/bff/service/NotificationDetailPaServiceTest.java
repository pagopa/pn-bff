package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullSentNotificationV23;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {NotificationDetailPAService.class})
public class NotificationDetailPaServiceTest {
    @Autowired
    private NotificationDetailPAService notificationDetailPAService;
    private PnDeliveryClientPAImpl pnDeliveryClientPA;
    private NotificationDetailMapper notificationDetailMapper;


    @BeforeEach
    void setup() {
        this.pnDeliveryClientPA = mock(PnDeliveryClientPAImpl.class);
        this.notificationDetailMapper = mock(NotificationDetailMapper.class);

        this.notificationDetailPAService = new NotificationDetailPAService(pnDeliveryClientPA, notificationDetailMapper);
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

        when(notificationDetailMapper.mapSentNotificationDetail(Mockito.<FullSentNotificationV23>any()))
                .thenReturn(new BffFullSentNotificationV23());

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
}
