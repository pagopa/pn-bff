package it.pagopa.pn.bff.mappers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class CxTypeMapperTest {

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.class)
    void testConvertDeliveryRecipientCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.BO) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.class)
    void testConvertDeliveryB2bPACXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.BO) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.class)
    void testConvertDeliveryWebPACXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertDeliveryWebPACXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.BO) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.class)
    void testConvertApiKeysPACXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertApiKeysPACXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BO ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BS) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.CxTypeAuthFleet.class)
    void testConvertExternalRegistriesSelfCareCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.CxTypeAuthFleet.BO) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.class)
    void testConvertExternalRegistriesNotificationsCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.BO) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.class)
    void testConvertUserAttributesCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.BO ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.BS) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.class)
    void testConvertUserAttributesFromApiKeysCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BO ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BS) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.class)
    void testConvertDeliveryPushCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.BO ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.BS) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.CxTypeAuthFleet.class)
    void testConvertMandateCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertMandateCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.CxTypeAuthFleet.BO ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.CxTypeAuthFleet.BS) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.class)
    void testConvertPublicKeysPGCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BO ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BS) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }

    @ParameterizedTest
    @EnumSource(value = it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.class)
    void testConvertVirtualKeysPGCXType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet cxType) {
        it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet result =
                CxTypeMapper.cxTypeMapper.convertVirtualKeysPGCXType(cxType);

        if (cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.RADD ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BO ||
                cxType == it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.BS) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals(cxType.getValue(), result.getValue());
        }
    }
}