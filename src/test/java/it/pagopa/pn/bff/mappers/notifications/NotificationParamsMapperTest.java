package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.LegalFactCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationParamsMapperTest {
    @Test
    void testConvertDeliveryPushLegalFactCategory() {
        LegalFactCategory legalFactCategory = LegalFactCategory.DIGITAL_DELIVERY;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory mappedLegalFactCategory = NotificationParamsMapper.modelMapper.mapLegalFactCategory(legalFactCategory);
        assertNotNull(mappedLegalFactCategory);
        assertEquals(mappedLegalFactCategory.getValue(), legalFactCategory.getValue());

        it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory mappedLegalFactCategoryNull = NotificationParamsMapper.modelMapper.mapLegalFactCategory(null);
        assertNull(mappedLegalFactCategoryNull);
    }
}