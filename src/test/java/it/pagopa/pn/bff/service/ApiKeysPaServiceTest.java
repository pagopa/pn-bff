package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.apikeys.PnApikeyManagerClientPAImpl;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApiKeysPaService.class})
public class ApiKeysPaServiceTest {
    private final ApiKeysMock apiKeysMock = new ApiKeysMock();
    private final UserMock userMock = new UserMock();
    @Autowired
    private ApiKeysPaService apiKeysPaService;
    private PnApikeyManagerClientPAImpl pnApikeyManagerClientPA;
    private PnInfoPaClientImpl pnInfoPaClient;

    @BeforeEach
    void setup() {
        this.pnApikeyManagerClientPA = mock(PnApikeyManagerClientPAImpl.class);
        this.pnInfoPaClient = mock(PnInfoPaClientImpl.class);

        this.apiKeysPaService = new ApiKeysPaService(pnApikeyManagerClientPA, pnInfoPaClient);
    }

    @Test
    void testGetApiKeys() {
        when(pnApikeyManagerClientPA.getApiKeys(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.just(apiKeysMock.getApiKeysMock()));

        apiKeysPaService.getApiKeys(
                "UID",
                CxTypeAuthFleet.PF,
                "CX_ID",
                Collections.singletonList("group"),
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        );

        Mockito.verify(pnApikeyManagerClientPA).getApiKeys(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        );
    }
}