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
    void testMapToBffAdditionalLanguages() {
        AdditionalLanguages additionalLanguages = paInfoMock.getAdditionalLanguagesMock();

        BffAdditionalLanguages bffAdditionalLanguages = LanguageMapper.modelMapper.toBffAdditionalLanguages(additionalLanguages);

        assertNotNull(bffAdditionalLanguages);
        for (int i = 0; i < additionalLanguages.getAdditionalLanguages().size(); i++) {
            assertEquals(bffAdditionalLanguages.getAdditionalLanguages().get(i), additionalLanguages.getAdditionalLanguages().get(i));
        }
    }

    @Test
    void testMapToAdditionalLanguages() {
        String paId = "mock-pa-id";
        BffAdditionalLanguages bffAdditionalLanguages = new BffAdditionalLanguages();
        bffAdditionalLanguages.setAdditionalLanguages(paInfoMock.getAdditionalLanguagesMock().getAdditionalLanguages());

        AdditionalLanguages additionalLanguages = LanguageMapper.modelMapper.toAdditionalLanguages(paId, bffAdditionalLanguages);

        assertNotNull(additionalLanguages);
        for (int i = 0; i < bffAdditionalLanguages.getAdditionalLanguages().size(); i++) {
            assertEquals(additionalLanguages.getAdditionalLanguages().get(i), bffAdditionalLanguages.getAdditionalLanguages().get(i));
        }
        assertEquals(additionalLanguages.getPaId(), paId);
    }
}
