package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BffTimelineMapperTest {

    @Test
    void testMapToBffTimelineCategory() {
        TimelineElementCategoryV28 category = TimelineElementCategoryV28.REQUEST_ACCEPTED;

        BffTimelineCategory result = BffTimelineMapper.modelMapper.mapToBffTimelineCategory(category);

        assertNotNull(result);
        assertEquals(category.getValue(), result.getValue());
    }

    @Test
    void testMapToBffLegalFactId() {
        LegalFactsIdV20 source = new LegalFactsIdV20();
        source.setKey("safestorage://123");
        source.setCategory("AAR");

        BffLegalFactId result = BffTimelineMapper.modelMapper.mapToBffLegalFactId(source);

        assertNotNull(result);
        assertEquals(source.getKey(), result.getKey());
        assertEquals(source.getCategory(), result.getCategory().getValue());
    }

    @Test
    void testMapToBffTimeline_ShouldIgnoreReworkedStatus() {
        TimelineElementV28 source = new TimelineElementV28();
        source.setElementId("123");

        BffNotificationDetailTimeline result = BffTimelineMapper.modelMapper.mapToBffTimeline(source);

        assertNotNull(result);
        assertEquals("123", result.getElementId());
        assertNull(result.getReworkedStatus());
    }
}