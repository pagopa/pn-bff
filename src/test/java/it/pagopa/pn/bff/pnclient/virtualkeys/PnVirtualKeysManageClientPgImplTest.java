package it.pagopa.pn.bff.pnclient.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.api.VirtualKeysApi;
import it.pagopa.pn.bff.generated.openapi.virtualkey.apikey_pg.model.CxTypeAuthFleet;
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

@ContextConfiguration(classes = {PnVirtualKeysManagerClientPGImpl.class})
@ExtendWith(SpringExtension.class)
class PnVirtualKeysManageClientPgImplTest {
    @Autowired
    private PnVirtualKeysManagerClientPGImpl pgVirtualKeysManageClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.api.VirtualKeysApi")
    private VirtualKeysApi virtualKeysApi;

    @Test
    void deleteVirtualKeyError() {
        when(virtualKeysApi.deleteVirtualKey(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.virtualkey.apikey_pg.model.CxTypeAuthFleet.class),
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
}
