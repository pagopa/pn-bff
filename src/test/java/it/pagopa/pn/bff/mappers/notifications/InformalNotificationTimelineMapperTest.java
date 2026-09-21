package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementCategoryV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.pn.bff.mocks.InformalSentNotificationDetailMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class InformalNotificationTimelineMapperTest {

    private final InformalSentNotificationDetailMock informalSentNotificationDetailMock = new InformalSentNotificationDetailMock();

    @Test
    void mapSentInformalNotificationTimelineNull() {
        assertNull(InformalNotificationTimelineMapper.modelMapper.mapSentInformalNotificationTimeline(null));
    }

    @Test
    void mapSentInformalNotificationTimelineDataAndOutcomesFalseWhenTimelineEmpty() {
        FullSentInformalNotificationV1 notification = informalSentNotificationDetailMock.getFullSentInformalNotificationMock();

        BffFullSentInformalNotificationTimelineV1 timeline =
                InformalNotificationTimelineMapper.modelMapper.mapSentInformalNotificationTimeline(notification);

        assertEquals(notification.getIun(), timeline.getIun());
        assertEquals(notification.getRecipients().size(), timeline.getRecipients().size());
        assertFalse(timeline.getCommunicationOutcomes().getDelivered());
        assertFalse(timeline.getCommunicationOutcomes().getViewed());
    }

    @Test
    void mapSentInformalNotificationTimelineOutcomesTrueWhenCategoriesPresent() {
        FullSentInformalNotificationV1 notification = informalSentNotificationDetailMock.getFullSentInformalNotificationMock()
                .timeline(List.of(
                        new InformalTimelineElementV1().category(InformalTimelineElementCategoryV1.DELIVERED),
                        new InformalTimelineElementV1().category(InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED)
                ));

        BffFullSentInformalNotificationTimelineV1 timeline =
                InformalNotificationTimelineMapper.modelMapper.mapSentInformalNotificationTimeline(notification);

        assertEquals(Boolean.TRUE, timeline.getCommunicationOutcomes().getDelivered());
        assertEquals(Boolean.TRUE, timeline.getCommunicationOutcomes().getViewed());
    }
}
