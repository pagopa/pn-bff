package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
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

@ContextConfiguration(classes = {PnDeliveryClientPAImpl.class})
@ExtendWith(SpringExtension.class)
class PnDeliveryClientPAImplTest {
    @Autowired
    private PnDeliveryClientPAImpl pnDeliveryClientPAImpl;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi")
    private SenderReadB2BApi senderReadB2BApi;

    @Test
    void getSentNotificationV23() throws RestClientException {
        Mono<FullSentNotificationV23> fullSentNotification = Mono.just(new FullSentNotificationV23());

        when(senderReadB2BApi
                .getSentNotificationV23(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<java.util.List<String>>any()
                )).thenReturn(fullSentNotification);


        assertSame(fullSentNotification,
                pnDeliveryClientPAImpl.getSentNotification(
                        "UID",
                        CxTypeAuthFleet.PA,
                        "CX_ID",
                        "IUN",
                        null
                )
        );

        verify(senderReadB2BApi).getSentNotificationV23(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<java.util.List<String>>any()
        );
    }

}
