package it.pagopa.pn.bff.pnclient.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.api.MandateServiceApi;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.MandateMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnMandateClientRecipientImpl.class})
@ExtendWith(SpringExtension.class)
class PnMandateClientRecipientImplTest {
    private final MandateMock mandateMock = new MandateMock();
    @Autowired
    private PnMandateClientRecipientImpl pnMandateClient;
    @MockBean
    private MandateServiceApi mandateApi;

    @Test
    void countMandatesByDelegate() {
        when(mandateApi.countMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.just(mandateMock.getCountMock()));

        StepVerifier.create(pnMandateClient.countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectNext(mandateMock.getCountMock()).verifyComplete();
    }

    @Test
    void countMandatesByDelegateError() {
        when(mandateApi.countMandatesByDelegate(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnMandateClient.countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectError(WebClientResponseException.class).verify();
    }
}