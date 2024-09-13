package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;

public class VirtualKeysMock {

    public RequestNewVirtualKey getRequestNewVirtualKeyMock() {
        RequestNewVirtualKey requestNewVirtualKey = new RequestNewVirtualKey();
        requestNewVirtualKey.setName("mock-api-key-name");
        return requestNewVirtualKey;
    }

    public ResponseNewVirtualKey getResponseNewVirtualKeyMock() {
        ResponseNewVirtualKey responseNewVirtualKey = new ResponseNewVirtualKey();
        responseNewVirtualKey.setId("mock-virtual-key-id");
        responseNewVirtualKey.setVirtualKey("mock-virtual-key");
        return responseNewVirtualKey;
    }
}
