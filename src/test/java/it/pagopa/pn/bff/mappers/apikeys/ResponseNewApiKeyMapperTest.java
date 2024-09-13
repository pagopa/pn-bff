package it.pagopa.pn.bff.mappers.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ResponseNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffResponseNewApiKey;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResponseNewApiKeyMapperTest {

    private final ApiKeysMock apiKeysMock = new ApiKeysMock();

    @Test
    void testResponseNewApiKeyMapper() {
        ResponseNewApiKey responseNewApiKey = apiKeysMock.getResponseNewApiKeyMock();
        BffResponseNewApiKey bffResponseNewApiKey = ResponseNewApiKeyMapper.modelMapper.mapResponseNewApiKey(responseNewApiKey);
        assertNotNull(bffResponseNewApiKey);
        assertThat(bffResponseNewApiKey).usingRecursiveComparison().isEqualTo(responseNewApiKey);

        bffResponseNewApiKey = ResponseNewApiKeyMapper.modelMapper.mapResponseNewApiKey(null);
        assertNull(bffResponseNewApiKey);
    }
}