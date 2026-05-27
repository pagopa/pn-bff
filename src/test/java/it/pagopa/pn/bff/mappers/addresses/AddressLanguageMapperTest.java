package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class AddressLanguageMapperTest {

    @ParameterizedTest
    @EnumSource(CxLanguage.class)
    void shouldMapAllLanguagesCorrectly(CxLanguage language) {
        CxLanguage result = AddressLanguageMapper.modelMapper.mapAddressesLanguage(language.name());
        assertEquals(language, result);
    }

    @Test
    void testAddressLanguageNotValidMapper() {
        CxLanguage language = AddressLanguageMapper.modelMapper.mapAddressesLanguage("GP");
        assertNotNull(language);
        assertEquals(CxLanguage.IT, language);
    }

    @Test
    void testAddressLanguageNotValidNullMapper() {
        CxLanguage language = AddressLanguageMapper.modelMapper.mapAddressesLanguage(null);
        assertNotNull(language);
        assertEquals(CxLanguage.IT, language);
    }
}
