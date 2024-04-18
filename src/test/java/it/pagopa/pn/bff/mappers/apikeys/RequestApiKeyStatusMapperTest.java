package it.pagopa.pn.bff.mappers.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestApiKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestApiKeyStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestApiKeyStatusMapperTest {
    @Test
    void testRequestApiKeyStatusMapper() {
        BffRequestApiKeyStatus bffRequestApiKeyStatus = new BffRequestApiKeyStatus();
        bffRequestApiKeyStatus.setStatus(BffRequestApiKeyStatus.StatusEnum.BLOCK);
        RequestApiKeyStatus requestApiKeyStatus = RequestApiKeyStatusMapper.modelMapper.mapRequestApiKeyStatus(bffRequestApiKeyStatus);
        assertNotNull(requestApiKeyStatus);
        assertThat(requestApiKeyStatus).usingRecursiveComparison().isEqualTo(bffRequestApiKeyStatus);

        requestApiKeyStatus = RequestApiKeyStatusMapper.modelMapper.mapRequestApiKeyStatus(null);
        assertNull(requestApiKeyStatus);
    }
}