package it.pagopa.pn.bff.mappers.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffRequestNewApiKey;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestNewApiKeyMapperTest {

    private final ApiKeysMock apiKeysMock = new ApiKeysMock();

    @Test
    void testRequestNewApiKeyMapper() {
        BffRequestNewApiKey bffRequestNewApiKey = new BffRequestNewApiKey();
        bffRequestNewApiKey.setName("mock-api-key-name");
        List<String> groups = new ArrayList<>();
        groups.add("mock-id-1");
        groups.add("mock-id-3");
        bffRequestNewApiKey.setGroups(groups);

        RequestNewApiKey requestNewApiKey = RequestNewApiKeyMapper.modelMapper.mapRequestNewApiKey(bffRequestNewApiKey);
        assertNotNull(requestNewApiKey);
        assertThat(requestNewApiKey).usingRecursiveComparison().isEqualTo(apiKeysMock.getRequestNewApiKeyMock());

        requestNewApiKey = RequestNewApiKeyMapper.modelMapper.mapRequestNewApiKey(null);
        assertNull(requestNewApiKey);
    }
}