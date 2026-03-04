package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDetailUtilityReworkedTest {

    @Test
    void insertReworkedStatus() {
        // create notification with one step in timeline with category "NOTIFICATION_TIMELINE_REWORKED"
        BffFullNotificationV1 bffFullNotificationV1 = new BffFullNotificationV1();
        List<BffNotificationDetailTimeline> timeline = new ArrayList<>();
        BffNotificationDetailTimeline singleElementTimeline = new BffNotificationDetailTimeline();
        singleElementTimeline.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);

        // add element to timeline
        timeline.add(singleElementTimeline);
        bffFullNotificationV1.setTimeline(timeline);
        NotificationDetailUtility.insertReworkedStatus(bffFullNotificationV1);

        assertEquals(1, bffFullNotificationV1.getNotificationStatusHistory().stream()
                .filter(status -> status.getStatus().equals(BffNotificationStatus.NOTIFICATION_TIMELINE_REWORKED)).toList().size()
        );
    }

    @Test
    void insertInvalidateElementsInTimeline() {

        BffFullNotificationV1 bffFullNotificationV1 = new BffFullNotificationV1();
        List<BffNotificationStatusHistory> notificationStatusHistory = new ArrayList<>();
        BffNotificationStatusHistory elementStatusHistory = new BffNotificationStatusHistory();
        elementStatusHistory.setStatus(BffNotificationStatus.DELIVERING);
        notificationStatusHistory.add(elementStatusHistory);
        bffFullNotificationV1.setNotificationStatusHistory(notificationStatusHistory);
        List<BffNotificationDetailTimeline> timeline = new ArrayList<>();
        BffNotificationDetailTimeline reworkedElementTimeline = new BffNotificationDetailTimeline();
        reworkedElementTimeline.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedElementTimeline.setTimestamp(OffsetDateTime.now());
        BffNotificationDetailTimelineDetails reworkedDetails = new BffNotificationDetailTimelineDetails();

        List<NotificationStatusHistoryInvalidatedElement> invalidatedElements = new ArrayList<>();
        NotificationStatusHistoryInvalidatedElement invalidatedElement = new NotificationStatusHistoryInvalidatedElement();
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.TimelineElementV28> relatedTimelineElements = new ArrayList<>();
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.TimelineElementV28 relatedTimelineElement = new it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.TimelineElementV28();
        relatedTimelineElement.setElementId("relatedElementId");
        relatedTimelineElement.setCategory(
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.TimelineElementCategoryV28.SEND_ANALOG_PROGRESS);
        relatedTimelineElement.setTimestamp(OffsetDateTime.now());

        relatedTimelineElements.add(relatedTimelineElement);
        NotificationStatusV26 status = NotificationStatusV26.DELIVERING;
        invalidatedElement.setRelatedTimelineElements(relatedTimelineElements);
        invalidatedElement.setStatus(status);
        invalidatedElement.setActiveFrom(OffsetDateTime.now());

        invalidatedElements.add(invalidatedElement);

        reworkedDetails.setInvalidatedTimelineAndStatusHistory(invalidatedElements);

        reworkedElementTimeline.setDetails(reworkedDetails);
        timeline.add(reworkedElementTimeline);
        bffFullNotificationV1.setTimeline(timeline);

        NotificationDetailUtility.insertInvalidateElementsInTimeline(bffFullNotificationV1);
        assertTrue(bffFullNotificationV1.getTimeline().stream()
                .anyMatch(element -> element.getElementId() != null && element.getElementId().equals("relatedElementId"))
        );
        assertTrue(bffFullNotificationV1.getNotificationStatusHistory().stream()
                .filter(statusHistory -> statusHistory.getStatus().equals(BffNotificationStatus.DELIVERING))
                .anyMatch(statusHistory -> statusHistory.getRelatedTimelineElements().stream()
                        .anyMatch(relatedElementId -> relatedElementId.equals("relatedElementId"))
                )
        );
    }
}