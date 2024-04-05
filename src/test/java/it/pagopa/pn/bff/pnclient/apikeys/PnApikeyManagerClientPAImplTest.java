package it.pagopa.pn.bff.pnclient.apikeys;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.api.ApiKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
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

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnApikeyManagerClientPAImpl.class})
@ExtendWith(SpringExtension.class)
class PnApikeyManagerClientPAImplTest {
    private final ApiKeysMock apiKeysMock = new ApiKeysMock();
    @Autowired
    private PnApikeyManagerClientPAImpl paApikeyManagerClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.api.ApiKeysApi")
    private ApiKeysApi apiKeysApi;

    @Test
    void getApiKeys() throws RestClientException {
        when(apiKeysApi.getApiKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.just(apiKeysMock.getApiKeysMock()));

        StepVerifier.create(paApikeyManagerClient.getApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectNext(apiKeysMock.getApiKeysMock()).verifyComplete();
    }

    @Test
    void getApiKeysError() {
        when(apiKeysApi.getApiKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(paApikeyManagerClient.getApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectError(PnBffException.class).verify();
    }
}