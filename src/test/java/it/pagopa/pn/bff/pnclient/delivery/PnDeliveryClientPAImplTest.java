package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
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
        when(senderReadB2BApi
                .getSentNotificationV23(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<java.util.List<String>>any()
                )).thenReturn(Mono.just(mock(FullSentNotificationV23.class)));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotification(
                "UID",
                CxTypeAuthFleet.PA,
                "CX_ID",
                "IUN",
                null
        )).expectNextCount(1).verifyComplete();
    }

    @Test
    void getSentNotificationV23Error() {
        when(senderReadB2BApi
                .getSentNotificationV23(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<java.util.List<String>>any()
                )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotification(
                "UID",
                CxTypeAuthFleet.PA,
                "CX_ID",
                "IUN",
                null
        )).expectError(PnBffException.class).verify();
    }
}
