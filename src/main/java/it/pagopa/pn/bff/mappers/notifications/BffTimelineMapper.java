package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffLegalFactId;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffLegalFactType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationDetailTimeline;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffTimelineCategory;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.LegalFactsIdV20;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.TimelineElementCategoryV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.TimelineElementV28;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffNotificationDetailTimeline
 * to the TimelineElementV28 and vice versa
 */
@Mapper
public interface BffTimelineMapper {

    BffTimelineMapper modelMapper = Mappers.getMapper(BffTimelineMapper.class);

    @Mapping(target = "ingestionTimestamp", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "notificationSentAt", ignore = true)
    TimelineElementV28 mapToTimelineElement(BffNotificationDetailTimeline bffTimeline);

    @Mapping(target = "hidden", ignore = true)
    @Mapping(target = "index", ignore = true)
    @Mapping(target = "reworkedStatus", ignore = true)
    BffNotificationDetailTimeline mapToBffTimeline(TimelineElementV28 timelineElement);

    // Sub-mapper for LegalFactsId
    @Mapping(target = "category", expression = "java(bffLegalFactId.getCategory() != null ? bffLegalFactId.getCategory().getValue() : null)")
    LegalFactsIdV20 mapToLegalFactsId(BffLegalFactId bffLegalFactId);

    @Mapping(target = "category", expression = "java(legalFactsId.getCategory() != null ? BffLegalFactType.fromValue(legalFactsId.getCategory()) : null)")
    BffLegalFactId mapToBffLegalFactId(LegalFactsIdV20 legalFactsId);

    // Sub-mapper for Category enum
    default TimelineElementCategoryV28 mapToTimelineCategory(BffTimelineCategory bffCategory) {
        if (bffCategory == null) {
            return null;
        }
        return TimelineElementCategoryV28.fromValue(bffCategory.getValue());
    }

    default BffTimelineCategory mapToBffTimelineCategory(TimelineElementCategoryV28 category) {
        if (category == null) {
            return null;
        }
        return BffTimelineCategory.fromValue(category.getValue());
    }
}
