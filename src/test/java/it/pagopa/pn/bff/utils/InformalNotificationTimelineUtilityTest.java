package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalNotificationStatusHistoryElementV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalNotificationStatusV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementCategoryV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalNotificationTimelineStatusHistoryV1;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationTimelineMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformalNotificationTimelineUtilityTest {

    @Test
    void populateNotificationStatusHistoryKeepsOnlyVisibleCategoriesInReverseOrder() {
        FullSentInformalNotificationV1 notification = new FullSentInformalNotificationV1()
                .timeline(List.of(
                        new InformalTimelineElementV1().elementId("e1").category(InformalTimelineElementCategoryV1.REQUEST_ACCEPTED),
                        new InformalTimelineElementV1().elementId("e2").category(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK),
                        new InformalTimelineElementV1().elementId("e3").category(InformalTimelineElementCategoryV1.DELIVERED)
                ))
                .notificationStatusHistory(List.of(
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.ACCEPTED)
                                .activeFrom(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                                .relatedTimelineElements(List.of("e1", "e2", "e3"))
                ));

        BffFullSentInformalNotificationTimelineV1 target = new BffFullSentInformalNotificationTimelineV1();

        InformalNotificationTimelineUtility.populateNotificationStatusHistory(
                notification, target, InformalNotificationTimelineMapper.modelMapper);

        assertEquals(1, target.getNotificationStatusHistory().size());
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalTimelineElementV1> steps =
                target.getNotificationStatusHistory().get(0).getSteps();

        assertEquals(2, steps.size());
        assertEquals("e3", steps.get(0).getElementId());
        assertEquals("e2", steps.get(1).getElementId());
    }

    @Test
    void populateNotificationStatusHistorySkipsElementIdsMissingFromTimeline() {
        FullSentInformalNotificationV1 notification = new FullSentInformalNotificationV1()
                .timeline(List.of(
                        new InformalTimelineElementV1().elementId("e1").category(InformalTimelineElementCategoryV1.DELIVERED)
                ))
                .notificationStatusHistory(List.of(
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.ACCEPTED)
                                .activeFrom(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                                .relatedTimelineElements(List.of("missing", "e1"))
                ));

        BffFullSentInformalNotificationTimelineV1 target = new BffFullSentInformalNotificationTimelineV1();

        InformalNotificationTimelineUtility.populateNotificationStatusHistory(
                notification, target, InformalNotificationTimelineMapper.modelMapper);

        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalTimelineElementV1> steps =
                target.getNotificationStatusHistory().get(0).getSteps();

        assertEquals(1, steps.size());
        assertEquals("e1", steps.get(0).getElementId());
    }

    @Test
    void populateNotificationStatusHistorySkipsNullRelatedTimelineElementIds() {
        FullSentInformalNotificationV1 notification = new FullSentInformalNotificationV1()
                .timeline(List.of(
                        new InformalTimelineElementV1().elementId("e1").category(InformalTimelineElementCategoryV1.DELIVERED)
                ))
                .notificationStatusHistory(List.of(
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.ACCEPTED)
                                .activeFrom(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                                .relatedTimelineElements(Arrays.asList(null, "e1"))
                ));

        BffFullSentInformalNotificationTimelineV1 target = new BffFullSentInformalNotificationTimelineV1();

        InformalNotificationTimelineUtility.populateNotificationStatusHistory(
                notification, target, InformalNotificationTimelineMapper.modelMapper);

        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalTimelineElementV1> steps =
                target.getNotificationStatusHistory().get(0).getSteps();

        assertEquals(1, steps.size());
        assertEquals("e1", steps.get(0).getElementId());
    }

    @Test
    void populateNotificationStatusHistorySkipsElementsWithNullCategory() {
        FullSentInformalNotificationV1 notification = new FullSentInformalNotificationV1()
                .timeline(List.of(
                        new InformalTimelineElementV1().elementId("e1"),
                        new InformalTimelineElementV1().elementId("e2").category(InformalTimelineElementCategoryV1.DELIVERED)
                ))
                .notificationStatusHistory(List.of(
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.ACCEPTED)
                                .activeFrom(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                                .relatedTimelineElements(List.of("e1", "e2"))
                ));

        BffFullSentInformalNotificationTimelineV1 target = new BffFullSentInformalNotificationTimelineV1();

        InformalNotificationTimelineUtility.populateNotificationStatusHistory(
                notification, target, InformalNotificationTimelineMapper.modelMapper);

        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalTimelineElementV1> steps =
                target.getNotificationStatusHistory().get(0).getSteps();

        assertEquals(1, steps.size());
        assertEquals("e2", steps.get(0).getElementId());
    }

    @Test
    void populateNotificationStatusHistoryReversesTheStatusList() {
        FullSentInformalNotificationV1 notification = new FullSentInformalNotificationV1()
                .timeline(List.of())
                .notificationStatusHistory(List.of(
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.ACCEPTED)
                                .activeFrom(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                                .relatedTimelineElements(List.of()),
                        new InformalNotificationStatusHistoryElementV1()
                                .status(InformalNotificationStatusV1.COMPLETED_REACHED)
                                .activeFrom(OffsetDateTime.parse("2026-01-02T00:00:00Z"))
                                .relatedTimelineElements(List.of())
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
}
