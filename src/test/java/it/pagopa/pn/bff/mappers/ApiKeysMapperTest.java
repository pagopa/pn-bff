package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeyRow;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeyGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeyRow;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ApiKeysMapperTest {

    private final ApiKeysMock apiKeysMock = new ApiKeysMock();
    private final UserMock userMock = new UserMock();

    private BffApiKeyGroup getMappedApiKeyGroups(String group, List<PaGroup> paGroups) {
        BffApiKeyGroup bffApiKeyGroup = new BffApiKeyGroup();
        PaGroup paGroup = paGroups.stream()
                .filter(el -> el.getName() == group)
                .findFirst()
                .orElse(null);
        if (paGroup != null) {
            bffApiKeyGroup.setId(paGroup.getId());
            bffApiKeyGroup.setName(paGroup.getName());
        } else {
            bffApiKeyGroup.setId("no-id");
            bffApiKeyGroup.setName(group);
        }
        return bffApiKeyGroup;
    }

    @Test
    void testApiKeysMapper() {
        ApiKeysResponse apiKeysResponse = apiKeysMock.getApiKeysMock();
        List<PaGroup> paGroups = userMock.getPaGroupsMock();

        BffApiKeysResponse bffApiKeysResponse = ApiKeysMapper.modelMapper.mapApiKeysResponse(apiKeysResponse, paGroups);
        assertNotNull(bffApiKeysResponse);
        assertEquals(bffApiKeysResponse.getLastKey(), apiKeysResponse.getLastKey());
        assertEquals(bffApiKeysResponse.getLastUpdate(), apiKeysResponse.getLastUpdate());
        assertEquals(bffApiKeysResponse.getTotal(), apiKeysResponse.getTotal());
        assertEquals(bffApiKeysResponse.getItems().size(), apiKeysResponse.getItems().size());
        // checks on single api key
        for (int i = 0; i < bffApiKeysResponse.getItems().size(); i++) {
            ApiKeyRow apiKeyRow = apiKeysResponse.getItems().get(i);
            BffApiKeyRow bffApiKeyRow = bffApiKeysResponse.getItems().get(i);
            assertEquals(bffApiKeyRow.getId(), apiKeyRow.getId());
            assertEquals(bffApiKeyRow.getName(), apiKeyRow.getName());
            assertEquals(bffApiKeyRow.getValue(), apiKeyRow.getValue());
            assertNotNull(apiKeyRow.getStatus());
            assertEquals(bffApiKeyRow.getStatus().getValue(), apiKeyRow.getStatus().getValue());
            assertEquals(bffApiKeyRow.getLastUpdate(), apiKeyRow.getLastUpdate());
            assertThat(bffApiKeyRow.getStatusHistory()).usingRecursiveComparison().isEqualTo(apiKeyRow.getStatusHistory());
            // check on groups
            for (int j = 0; j < apiKeyRow.getGroups().size(); j++) {
                assertEquals(bffApiKeyRow.getGroups().get(j), getMappedApiKeyGroups(apiKeyRow.getGroups().get(j), paGroups));
            }
        }

        BffApiKeysResponse bffApiKeysResponseNull = ApiKeysMapper.modelMapper.mapApiKeysResponse(null, new ArrayList<>());
        assertNull(bffApiKeysResponseNull);
    }
}