package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalNotificationTimelineGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationChannelType;
import it.pagopa.pn.bff.mocks.InformalSentNotificationDetailMock;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void mapSentInformalNotificationTimelineBuildsStatusHistory() {
        FullSentInformalNotificationV1 notification = informalSentNotificationDetailMock.getFullSentInformalNotificationMock()
                .timeline(List.of(
                        new InformalTimelineElementV1().elementId("e1").category(InformalTimelineElementCategoryV1.REQUEST_ACCEPTED),
                        new InformalTimelineElementV1().elementId("e2").category(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK)
                                .details(new InformalTimelineElementDetailsV1().channel("IO")),
                        new InformalTimelineElementV1().elementId("e3").category(InformalTimelineElementCategoryV1.DELIVERED)
                                .details(new InformalTimelineElementDetailsV1().channel("IO"))
                ))
                .notificationStatusHistory(List.of(
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.ACCEPTED)
                                .activeFrom(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                                .relatedTimelineElements(List.of("e1")),
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.PROCESSING)
                                .activeFrom(OffsetDateTime.parse("2026-01-02T00:00:00Z"))
                                .relatedTimelineElements(List.of("e2", "e3"))
                ));

        BffFullSentInformalNotificationTimelineV1 timeline =
                InformalNotificationTimelineMapper.modelMapper.mapSentInformalNotificationTimeline(notification);

        assertEquals(2, timeline.getNotificationStatusHistory().size());

        // most recent status first, with its events grouped by channel
        List<BffInformalNotificationTimelineGroup> steps = timeline.getNotificationStatusHistory().get(0).getSteps();
        assertEquals(1, steps.size());
        assertEquals(BffNotificationChannelType.IO, steps.get(0).getChannel());
        assertEquals("e3", steps.get(0).getEvents().get(0).getElementId());
        assertEquals("e2", steps.get(0).getEvents().get(1).getElementId());

        // oldest status has no visible steps (REQUEST_ACCEPTED is filtered out)
        assertTrue(timeline.getNotificationStatusHistory().get(1).getSteps().isEmpty());
    }
}
