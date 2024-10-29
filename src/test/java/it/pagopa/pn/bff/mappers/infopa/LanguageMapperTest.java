package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_private.model.AdditionalLanguages;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffAdditionalLanguages;
import it.pagopa.pn.bff.mocks.PaInfoMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LanguageMapperTest {
    private final PaInfoMock paInfoMock = new PaInfoMock();

    @Test
    void testMapAdditionalLanguages() {
        AdditionalLanguages additionalLanguages = paInfoMock.getAdditionalLanguagesMock();

        BffAdditionalLanguages bffAdditionalLanguages = LanguageMapper.modelMapper.toBffAdditionalLanguages(additionalLanguages);

        assertNotNull(bffAdditionalLanguages);
        assertEquals(bffAdditionalLanguages.getAdditionalLanguages().size(), additionalLanguages.getAdditionalLanguages().size());
    }
}
