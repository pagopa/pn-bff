package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VirtualKeysMock {



    private VirtualKey getVirtualKeyMock(String id, String value, String name, OffsetDateTime lastUpdate, VirtualKeyStatus virtualKeyStatus) {
        VirtualKey virtualKey = new VirtualKey();
        virtualKey.setId(id);
        virtualKey.setValue(value);
        virtualKey.setName(name);
        virtualKey.setLastUpdate(lastUpdate);
        virtualKey.setStatus(virtualKey.getStatus());
        return virtualKey;
    }

    public VirtualKeysResponse getVirtualKeysMock() {
        VirtualKeysResponse virtualKeysResponse = new VirtualKeysResponse();
        virtualKeysResponse.setTotal(14);
        virtualKeysResponse.setLastKey("9e522ef5-a024-4dbd-8a59-3e090c637653");
        virtualKeysResponse.lastUpdate("22/09/2022");
        List<VirtualKey> virtualKeyRows = new ArrayList<>();
        // first virtual key
        VirtualKey firstApiKey = getVirtualKeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637659",
                "9e522ef5-a024-4dbd-8a59-3e090c637650",
                "Rimborso e multe",
                OffsetDateTime.parse("2022-09-21T09:33:58.709695008Z"),
                VirtualKeyStatus.ENABLED
        );
        // second virtual key
        VirtualKey secondApiKey = getVirtualKeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637651",
                "9e522ef5-a024-4dbd-8a59-3e090c637652",
                "Cartelle esattoriali",
                OffsetDateTime.parse("2022-09-22T09:33:58.709695008Z"),
                VirtualKeyStatus.BLOCKED
        );
        // third virtual key
        VirtualKey thirdApiKey = getVirtualKeyMock(
                "9e522ef5-a024-4dbd-8a59-3e090c637653",
                "9e522ef5-a024-4dbd-8a59-3e090c637654",
                "Rimborsi",
                OffsetDateTime.parse("2022-09-22T09:33:58.709695008Z"),
                VirtualKeyStatus.ROTATED
        );
        virtualKeyRows.add(firstApiKey);
        virtualKeyRows.add(secondApiKey);
        virtualKeyRows.add(thirdApiKey);
        virtualKeysResponse.setItems(virtualKeyRows);
        return virtualKeysResponse;
    }


    public RequestNewVirtualKey getRequestNewVirtualKeyMock() {
        RequestNewVirtualKey requestNewVirtualKey = new RequestNewVirtualKey();
        requestNewVirtualKey.setName("mock-virtual-key-name");
        return requestNewVirtualKey;
    }

    public ResponseNewVirtualKey getResponseNewVirtualKeyMock() {
        ResponseNewVirtualKey responseNewVirtualKey = new ResponseNewVirtualKey();
        responseNewVirtualKey.setId("mock-virtual-key-id");
        responseNewVirtualKey.setVirtualKey("mock-virtual-key");
        return responseNewVirtualKey;
    }
}
