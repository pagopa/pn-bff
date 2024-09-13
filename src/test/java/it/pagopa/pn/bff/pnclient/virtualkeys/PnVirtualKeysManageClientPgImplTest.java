package it.pagopa.pn.bff.pnclient.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestApiKeyStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.api.VirtualKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestVirtualKeyStatus;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
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

import javax.jws.soap.SOAPBinding;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnVirtualKeysManagerClientPGImpl.class})
@ExtendWith(SpringExtension.class)
class PnVirtualKeysManageClientPgImplTest {
    @Autowired
    private PnVirtualKeysManagerClientPGImpl pgVirtualKeysManageClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.api.VirtualKeysApi")
    private VirtualKeysApi virtualKeysApi;

    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();


    @Test
    void deleteVirtualKeyError() {
        when(virtualKeysApi.deleteVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pgVirtualKeysManageClient.deleteVirtualKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void newVirtualKey() throws RestClientException {
        when(virtualKeysApi.createVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestNewVirtualKey.class),
                Mockito.anyList()
                )).thenReturn(Mono.just(virtualKeysMock.getResponseNewVirtualKeyMock()));

        StepVerifier.create(pgVirtualKeysManageClient.newVirtualKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                virtualKeysMock.getRequestNewVirtualKeyMock(),
                UserMock.PN_CX_GROUPS
        )).expectNext(virtualKeysMock.getResponseNewVirtualKeyMock()).verifyComplete();
    }

    @Test
    void newApiKeyError() {
        when(virtualKeysApi.createVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestNewVirtualKey.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pgVirtualKeysManageClient.newVirtualKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                virtualKeysMock.getRequestNewVirtualKeyMock(),
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void changeStatusVirtualKey() throws RestClientException {
        RequestVirtualKeyStatus requestVirtualKeyStatus = new RequestVirtualKeyStatus();
        requestVirtualKeyStatus.setStatus(RequestVirtualKeyStatus.StatusEnum.ROTATE);
        when(virtualKeysApi.changeStatusVirtualKeys(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestVirtualKeyStatus.class),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pgVirtualKeysManageClient.changeStatusVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "API_KEY_ID",
                requestVirtualKeyStatus,
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void changeStatusApiKeyError() {
        RequestVirtualKeyStatus requestVirtualKeyStatus = new RequestVirtualKeyStatus();
        requestVirtualKeyStatus.setStatus(RequestVirtualKeyStatus.StatusEnum.ROTATE);
        when(virtualKeysApi.changeStatusVirtualKeys(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(RequestVirtualKeyStatus.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pgVirtualKeysManageClient.changeStatusVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "API_KEY_ID",
                requestVirtualKeyStatus,
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }
}
