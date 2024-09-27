package it.pagopa.pn.bff.pnclient.apikeys;

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

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnVirtualKeysManagerClientPGImpl.class})
@ExtendWith(SpringExtension.class)
class PnVirtualKeysManagerClientPGImplTest {
    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();
    @Autowired
    private PnVirtualKeysManagerClientPGImpl pgVirtualKeysManageClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.api.VirtualKeysApi")
    private VirtualKeysApi virtualKeysApi;

    @Test
    void getVirtualKeys() throws RestClientException {
        when(virtualKeysApi.getVirtualKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.just(virtualKeysMock.getVirtualKeysMock()));

        StepVerifier.create(pgVirtualKeysManageClient.getVirtualKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectNext(virtualKeysMock.getVirtualKeysMock()).verifyComplete();
    }

    @Test
    void getVirtualKeysError() {
        when(virtualKeysApi.getVirtualKeys(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pgVirtualKeysManageClient.getVirtualKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectError(WebClientResponseException.class).verify();
    }


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
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void deleteVirtualKey() {
        when(virtualKeysApi.deleteVirtualKey(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pgVirtualKeysManageClient.deleteVirtualKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
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
                virtualKeysMock.getRequestNewVirtualKeyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(virtualKeysMock.getResponseNewVirtualKeyMock()).verifyComplete();
    }

    @Test
    void newVirtualKeyError() {
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
                virtualKeysMock.getRequestNewVirtualKeyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
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
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                "VIRTUALKEY_ID",
                requestVirtualKeyStatus,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void changeStatusVirtualKeyError() {
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
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                requestVirtualKeyStatus,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }
}