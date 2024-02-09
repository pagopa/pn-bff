package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV23;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {NotificationDetailRecipientService.class})
public class NotificationDetailRecipientServiceTest {
    @Autowired
    private NotificationDetailRecipientService notificationDetailRecipientService;
    private PnDeliveryClientRecipientImpl pnDeliveryClient;

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

        when(NotificationDetailMapper.modelMapper.mapNotificationDetail(Mockito.<FullReceivedNotificationV23>any()))
                .thenReturn(new BffFullReceivedNotificationV23());

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
}
