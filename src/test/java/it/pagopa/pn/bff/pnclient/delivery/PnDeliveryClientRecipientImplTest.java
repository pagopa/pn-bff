package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnDeliveryClientRecipientImpl.class})
@ExtendWith(SpringExtension.class)
class PnDeliveryClientRecipientImplTest {
    @Autowired
    private PnDeliveryClientRecipientImpl pnDeliveryClientRecipientImpl;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi")
    private RecipientReadApi recipientReadApi;

    @Test
    void getNotification() throws RestClientException {
        Mono<FullReceivedNotificationV23> fullReceivedNotificationV21 = Mono.just(new FullReceivedNotificationV23());

        when(recipientReadApi
                .getReceivedNotificationV23(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<java.util.List<String>>any(),
                        Mockito.<String>any()
                )).thenReturn(fullReceivedNotificationV21);


        assertSame(fullReceivedNotificationV21,
                pnDeliveryClientRecipientImpl.getReceivedNotification(
                        "UID",
                        CxTypeAuthFleet.PF,
                        "CX_ID",
                        "IUN",
                        null,
                        "MANDATE_ID")
        );

        verify(recipientReadApi).getReceivedNotificationV23(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any(),
                Mockito.<String>any()
        );
    }

}
