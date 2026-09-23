package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalNotificationTimelineGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalNotificationTimelineItem;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalNotificationTimelineStatusHistoryV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationChannelType;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationTimelineMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformalNotificationTimelineUtilityTest {

    @Test
    void populateNotificationStatusHistoryKeepsOnlyVisibleCategories() {
        List<BffInformalNotificationTimelineGroup> steps = populateSteps(
                List.of(
                        event("e1", InformalTimelineElementCategoryV1.REQUEST_ACCEPTED, null),
                        event("e2", InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC"),
                        event("e3", InformalTimelineElementCategoryV1.DELIVERED, "PEC")
                ),
                List.of("e1", "e2", "e3"));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationChannelType.PEC, steps.get(0).getChannel());
        assertEquals(List.of("e3", "e2"), elementIds(steps.get(0)));
    }

    @Test
    void populateNotificationStatusHistoryGroupsEventsByChannelMostRecentFirst() {
        List<BffInformalNotificationTimelineGroup> steps = populateSteps(
                List.of(
                        event("e1", InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_SKIP, "IO"),
                        event("e2", InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC"),
                        event("e3", InformalTimelineElementCategoryV1.SEND_ANALOG_MESSAGE_FEEDBACK, null),
                        event("e4", InformalTimelineElementCategoryV1.DELIVERED, "ANALOG")
                ),
                List.of("e1", "e2", "e3", "e4"));

        assertEquals(3, steps.size());
        assertEquals(BffNotificationChannelType.ANALOG, steps.get(0).getChannel());
        assertEquals(List.of("e4", "e3"), elementIds(steps.get(0)));
        assertEquals(BffNotificationChannelType.PEC, steps.get(1).getChannel());
        assertEquals(List.of("e2"), elementIds(steps.get(1)));
        assertEquals(BffNotificationChannelType.IO, steps.get(2).getChannel());
        assertEquals(List.of("e1"), elementIds(steps.get(2)));
    }

    @Test
    void populateNotificationStatusHistoryGroupsViewedEventsBySourceChannel() {
        List<BffInformalNotificationTimelineGroup> steps = populateSteps(
                List.of(
                        viewed("e1", "IO"),
                        viewed("e2", "WEB")
                ),
                List.of("e1", "e2"));

        assertEquals(2, steps.size());
        assertEquals(BffNotificationChannelType.SEND, steps.get(0).getChannel());
        assertEquals(List.of("e2"), elementIds(steps.get(0)));
        assertEquals(BffNotificationChannelType.IO, steps.get(1).getChannel());
        assertEquals(List.of("e1"), elementIds(steps.get(1)));
    }

    @Test
    void populateNotificationStatusHistoryGroupsEventsWithUnresolvableChannelUnderUnknown() {
        List<BffInformalNotificationTimelineGroup> steps = populateSteps(
                List.of(
                        new InformalTimelineElementV1().elementId("e1").category(InformalTimelineElementCategoryV1.DELIVERED),
                        event("e2", InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "SERCQ"),
                        event("e3", InformalTimelineElementCategoryV1.DELIVERED, "IO")
                ),
                List.of("e1", "e2", "e3"));

        assertEquals(2, steps.size());
        assertEquals(BffNotificationChannelType.IO, steps.get(0).getChannel());
        assertEquals(List.of("e3"), elementIds(steps.get(0)));
        assertEquals(BffNotificationChannelType.UNKNOWN, steps.get(1).getChannel());
        assertEquals(List.of("e2", "e1"), elementIds(steps.get(1)));
    }

    @Test
    void populateNotificationStatusHistorySkipsElementIdsMissingFromTimeline() {
        List<BffInformalNotificationTimelineGroup> steps = populateSteps(
                List.of(event("e1", InformalTimelineElementCategoryV1.DELIVERED, "IO")),
                List.of("missing", "e1"));

        assertEquals(1, steps.size());
        assertEquals(List.of("e1"), elementIds(steps.get(0)));
    }

    @Test
    void populateNotificationStatusHistorySkipsNullRelatedTimelineElementIds() {
        List<BffInformalNotificationTimelineGroup> steps = populateSteps(
                List.of(event("e1", InformalTimelineElementCategoryV1.DELIVERED, "IO")),
                Arrays.asList(null, "e1"));

        assertEquals(1, steps.size());
        assertEquals(List.of("e1"), elementIds(steps.get(0)));
    }

    @Test
    void populateNotificationStatusHistorySkipsElementsWithNullCategory() {
        List<BffInformalNotificationTimelineGroup> steps = populateSteps(
                List.of(
                        event("e1", null, "IO"),
                        event("e2", InformalTimelineElementCategoryV1.DELIVERED, "IO")
                ),
                List.of("e1", "e2"));

        assertEquals(1, steps.size());
        assertEquals(List.of("e2"), elementIds(steps.get(0)));
    }

    @Test
    void populateNotificationStatusHistoryReversesTheStatusList() {
        FullSentInformalNotificationV1 notification = new FullSentInformalNotificationV1()
                .timeline(List.of())
                .notificationStatusHistory(List.of(
                        status(InformalNotificationStatusV1.ACCEPTED, "2026-01-01T00:00:00Z", List.of()),
                        status(InformalNotificationStatusV1.COMPLETED_REACHED, "2026-01-02T00:00:00Z", List.of())
                ));

        BffFullSentInformalNotificationTimelineV1 target = new BffFullSentInformalNotificationTimelineV1();

        InformalNotificationTimelineUtility.populateNotificationStatusHistory(
                notification, target, InformalNotificationTimelineMapper.modelMapper);

        List<BffInformalNotificationTimelineStatusHistoryV1> history = target.getNotificationStatusHistory();

        assertEquals(2, history.size());
        assertEquals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalNotificationStatusV1.COMPLETED_REACHED,
                history.get(0).getStatus());
        assertEquals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalNotificationStatusV1.ACCEPTED,
                history.get(1).getStatus());
        assertTrue(history.get(0).getSteps().isEmpty());
        assertTrue(history.get(1).getSteps().isEmpty());
    }

    private static List<BffInformalNotificationTimelineGroup> populateSteps(
            List<InformalTimelineElementV1> timeline,
            List<String> relatedTimelineElements) {

        FullSentInformalNotificationV1 notification = new FullSentInformalNotificationV1()
                .timeline(timeline)
                .notificationStatusHistory(List.of(
                        status(InformalNotificationStatusV1.PROCESSING, "2026-01-01T00:00:00Z", relatedTimelineElements)
                ));

        BffFullSentInformalNotificationTimelineV1 target = new BffFullSentInformalNotificationTimelineV1();

        InformalNotificationTimelineUtility.populateNotificationStatusHistory(
                notification, target, InformalNotificationTimelineMapper.modelMapper);

        return target.getNotificationStatusHistory().get(0).getSteps();
    }

    private static InformalNotificationStatusHistoryElementV1 status(
            InformalNotificationStatusV1 status,
            String activeFrom,
            List<String> relatedTimelineElements) {

        return new InformalNotificationStatusHistoryElementV1()
                .status(status)
                .activeFrom(OffsetDateTime.parse(activeFrom))
                .relatedTimelineElements(relatedTimelineElements);
    }

    private static InformalTimelineElementV1 event(String elementId, InformalTimelineElementCategoryV1 category, String channel) {
        return new InformalTimelineElementV1()
                .elementId(elementId)
                .category(category)
                .details(new InformalTimelineElementDetailsV1().channel(channel));
    }

    private static InformalTimelineElementV1 viewed(String elementId, String sourceChannel) {
        return new InformalTimelineElementV1()
                .elementId(elementId)
                .category(InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED)
                .details(new InformalTimelineElementDetailsV1().sourceChannel(sourceChannel));
    }

    private static List<String> elementIds(BffInformalNotificationTimelineGroup group) {
        return group.getEvents().stream()
                .map(BffInformalNotificationTimelineItem::getElementId)
                .toList();
    }
}
