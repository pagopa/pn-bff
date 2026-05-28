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
}
