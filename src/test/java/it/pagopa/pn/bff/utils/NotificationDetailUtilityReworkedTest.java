package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDetailUtilityReworkedTest {

    // region insertReworkedStatus

    @Test
    void insertReworkedStatus() {
        BffFullNotificationV1 bffFullNotificationV1 = new BffFullNotificationV1();
        List<BffNotificationDetailTimeline> timeline = new ArrayList<>();
        BffNotificationDetailTimeline singleElementTimeline = new BffNotificationDetailTimeline();
        singleElementTimeline.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);

        timeline.add(singleElementTimeline);
        bffFullNotificationV1.setTimeline(timeline);
        NotificationDetailUtility.insertReworkedStatus(bffFullNotificationV1);

        assertEquals(1, bffFullNotificationV1.getNotificationStatusHistory().stream()
                .filter(status -> status.getStatus().equals(BffNotificationStatus.NOTIFICATION_TIMELINE_REWORKED)).toList().size()
        );
    }

    // endregion

    // region insertInvalidateElementsInTimeline

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
        List<TimelineElementV28> relatedTimelineElements = new ArrayList<>();
        TimelineElementV28 relatedTimelineElement = new TimelineElementV28();
        relatedTimelineElement.setElementId("relatedElementId");
        relatedTimelineElement.setCategory(TimelineElementCategoryV28.SEND_ANALOG_PROGRESS);
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

    // endregion

    // region setReworkedStatusOnSteps

    @Test
    void setReworkedStatusOnSteps_noReworkedEvents_noChanges() {
        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>());
        BffNotificationStatusHistory statusHistory = new BffNotificationStatusHistory();
        statusHistory.setStatus(BffNotificationStatus.ACCEPTED);
        statusHistory.setSteps(new ArrayList<>());
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(statusHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        assertNull(statusHistory.getReworkedStatus());
        assertEquals(1, notification.getNotificationStatusHistory().size());
    }

    @Test
    void setReworkedStatusOnSteps_deliveringStatusSkipped() {
        // elem1 is invalidated, elem1.REWORK_1 corrects it - both live in the DELIVERING status history
        BffNotificationDetailTimeline invalidatedStep = new BffNotificationDetailTimeline();
        invalidatedStep.setElementId("elem1");
        invalidatedStep.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        invalidatedStep.setTimestamp(OffsetDateTime.now());

        BffNotificationDetailTimeline reworkStep = new BffNotificationDetailTimeline();
        reworkStep.setElementId("elem1.REWORK_1");
        reworkStep.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        reworkStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("elem1");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.DELIVERING);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory deliveringHistory = new BffNotificationStatusHistory();
        deliveringHistory.setStatus(BffNotificationStatus.DELIVERING);
        deliveringHistory.setSteps(new ArrayList<>(List.of(invalidatedStep, reworkStep)));
        deliveringHistory.setRelatedTimelineElements(new ArrayList<>(List.of("elem1", "elem1.REWORK_1")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(deliveringHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        // no NOT_VALID DELIVERING entry created (DELIVERING is skipped in first step)
        assertEquals(1, notification.getNotificationStatusHistory().size());
        // the status history itself has no reworked label
        assertNull(deliveringHistory.getReworkedStatus());
        // invalidated step marked NOT_VALID, rework step marked VALID
        assertEquals(BffNotificationReworkedStatus.NOT_VALID, invalidatedStep.getReworkedStatus());
        assertEquals(BffNotificationReworkedStatus.VALID, reworkStep.getReworkedStatus());
    }

    @Test
    void setReworkedStatusOnSteps_invalidatedStatusCreatesNotValidEntry_andStepMovedFromOriginal() {
        BffNotificationDetailTimeline step = new BffNotificationDetailTimeline();
        step.setElementId("stepId");
        step.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        step.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("stepId");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.ACCEPTED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory originalAccepted = new BffNotificationStatusHistory();
        originalAccepted.setStatus(BffNotificationStatus.ACCEPTED);
        originalAccepted.setSteps(new ArrayList<>(List.of(step)));
        originalAccepted.setRelatedTimelineElements(new ArrayList<>(List.of("stepId")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(originalAccepted)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        BffNotificationStatusHistory notValidEntry = notification.getNotificationStatusHistory().stream()
                .filter(sh -> sh.getStatus() == BffNotificationStatus.ACCEPTED
                        && sh.getReworkedStatus() == BffNotificationReworkedStatus.NOT_VALID)
                .findFirst().orElse(null);

        long acceptedCount = notification.getNotificationStatusHistory().stream()
                .filter(sh -> sh.getStatus() == BffNotificationStatus.ACCEPTED)
                .count();
        assertEquals(2, acceptedCount);
        assertNull(originalAccepted.getReworkedStatus());
        assertTrue(originalAccepted.getSteps().isEmpty());
        assertNotNull(notValidEntry);
        assertEquals(1, notValidEntry.getSteps().size());
        assertEquals("stepId", notValidEntry.getSteps().get(0).getElementId());
    }

    @Test
    void setReworkedStatusOnSteps_invalidatedStepMarkedNotValid() {
        BffNotificationDetailTimeline step = new BffNotificationDetailTimeline();
        step.setElementId("invalidatedStep");
        step.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        step.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("invalidatedStep");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.ACCEPTED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory originalAccepted = new BffNotificationStatusHistory();
        originalAccepted.setStatus(BffNotificationStatus.ACCEPTED);
        originalAccepted.setSteps(new ArrayList<>(List.of(step)));
        originalAccepted.setRelatedTimelineElements(new ArrayList<>(List.of("invalidatedStep")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(originalAccepted)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        BffNotificationStatusHistory notValidEntry = notification.getNotificationStatusHistory().stream()
                .filter(sh -> sh.getStatus() == BffNotificationStatus.ACCEPTED
                        && sh.getReworkedStatus() == BffNotificationReworkedStatus.NOT_VALID)
                .findFirst().orElseThrow();

        assertEquals(BffNotificationReworkedStatus.NOT_VALID, notValidEntry.getSteps().get(0).getReworkedStatus());
    }

    @Test
    void setReworkedStatusOnSteps_reworkedStepCorrectingInvalidated_markedValidAndParentMarkedValid() {
        BffNotificationDetailTimeline reworkedStep = new BffNotificationDetailTimeline();
        reworkedStep.setElementId("baseId.REWORK_1");
        reworkedStep.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        reworkedStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("baseId");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.ACCEPTED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory deliveredHistory = new BffNotificationStatusHistory();
        deliveredHistory.setStatus(BffNotificationStatus.DELIVERED);
        deliveredHistory.setSteps(new ArrayList<>(List.of(reworkedStep)));
        deliveredHistory.setRelatedTimelineElements(new ArrayList<>(List.of("baseId.REWORK_1")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(deliveredHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        assertEquals(BffNotificationReworkedStatus.VALID, reworkedStep.getReworkedStatus());
        assertEquals(BffNotificationReworkedStatus.VALID, deliveredHistory.getReworkedStatus());
    }

    @Test
    void setReworkedStatusOnSteps_viewedStatusNotMarkedValid() {
        BffNotificationDetailTimeline reworkedStep = new BffNotificationDetailTimeline();
        reworkedStep.setElementId("baseId.REWORK_1");
        reworkedStep.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        reworkedStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("baseId");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.ACCEPTED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory viewedHistory = new BffNotificationStatusHistory();
        viewedHistory.setStatus(BffNotificationStatus.VIEWED);
        viewedHistory.setSteps(new ArrayList<>(List.of(reworkedStep)));
        viewedHistory.setRelatedTimelineElements(new ArrayList<>(List.of("baseId.REWORK_1")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(viewedHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        assertNull(viewedHistory.getReworkedStatus());
    }

    @Test
    void setReworkedStatusOnSteps_unreachableStatusNotMarkedValid() {
        BffNotificationDetailTimeline reworkedStep = new BffNotificationDetailTimeline();
        reworkedStep.setElementId("baseId.REWORK_1");
        reworkedStep.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        reworkedStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("baseId");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.ACCEPTED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory unreachableHistory = new BffNotificationStatusHistory();
        unreachableHistory.setStatus(BffNotificationStatus.UNREACHABLE);
        unreachableHistory.setSteps(new ArrayList<>(List.of(reworkedStep)));
        unreachableHistory.setRelatedTimelineElements(new ArrayList<>(List.of("baseId.REWORK_1")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(unreachableHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        assertNull(unreachableHistory.getReworkedStatus());
    }

    @Test
    void setReworkedStatusOnSteps_deliveringStatusNotMarkedValid() {
        BffNotificationDetailTimeline reworkedStep = new BffNotificationDetailTimeline();
        reworkedStep.setElementId("baseId.REWORK_1");
        reworkedStep.setCategory(BffTimelineCategory.SEND_DIGITAL_DOMICILE);
        reworkedStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("baseId");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.ACCEPTED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory deliveringHistory = new BffNotificationStatusHistory();
        deliveringHistory.setStatus(BffNotificationStatus.DELIVERING);
        deliveringHistory.setSteps(new ArrayList<>(List.of(reworkedStep)));
        deliveringHistory.setRelatedTimelineElements(new ArrayList<>(List.of("baseId.REWORK_1")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(deliveringHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        assertNull(deliveringHistory.getReworkedStatus());
    }

    @Test
    void setReworkedStatusOnSteps_deliveryModeSetForInvalidatedDeliveredStatus() {
        BffNotificationDetailTimeline digitalSuccessStep = new BffNotificationDetailTimeline();
        digitalSuccessStep.setElementId("digitalSuccess");
        digitalSuccessStep.setCategory(BffTimelineCategory.DIGITAL_SUCCESS_WORKFLOW);
        digitalSuccessStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("digitalSuccess");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.DELIVERED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory deliveredHistory = new BffNotificationStatusHistory();
        deliveredHistory.setStatus(BffNotificationStatus.DELIVERED);
        deliveredHistory.setSteps(new ArrayList<>(List.of(digitalSuccessStep)));
        deliveredHistory.setRelatedTimelineElements(new ArrayList<>(List.of("digitalSuccess")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(deliveredHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        BffNotificationStatusHistory notValidDelivered = notification.getNotificationStatusHistory().stream()
                .filter(sh -> sh.getStatus() == BffNotificationStatus.DELIVERED
                        && sh.getReworkedStatus() == BffNotificationReworkedStatus.NOT_VALID)
                .findFirst().orElseThrow();

        assertEquals(BffNotificationDeliveryMode.DIGITAL, notValidDelivered.getDeliveryMode());
    }

    @Test
    void setReworkedStatusOnSteps_recipientSetForInvalidatedViewedStatusWithDelegate() {
        BffNotificationDetailTimeline viewedStep = new BffNotificationDetailTimeline();
        viewedStep.setElementId("viewedStep");
        viewedStep.setCategory(BffTimelineCategory.NOTIFICATION_VIEWED);
        viewedStep.setTimestamp(OffsetDateTime.now());
        DelegateInfo delegate = new DelegateInfo();
        delegate.setDenomination("Mario Rossi");
        delegate.setTaxId("RSSMRA80A01H501U");
        BffNotificationDetailTimelineDetails viewedDetails = new BffNotificationDetailTimelineDetails();
        viewedDetails.setDelegateInfo(delegate);
        viewedStep.setDetails(viewedDetails);

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("viewedStep");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.VIEWED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory viewedHistory = new BffNotificationStatusHistory();
        viewedHistory.setStatus(BffNotificationStatus.VIEWED);
        viewedHistory.setSteps(new ArrayList<>(List.of(viewedStep)));
        viewedHistory.setRelatedTimelineElements(new ArrayList<>(List.of("viewedStep")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(viewedHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        BffNotificationStatusHistory notValidViewed = notification.getNotificationStatusHistory().stream()
                .filter(sh -> sh.getStatus() == BffNotificationStatus.VIEWED
                        && sh.getReworkedStatus() == BffNotificationReworkedStatus.NOT_VALID)
                .findFirst().orElseThrow();

        assertEquals("Mario Rossi (RSSMRA80A01H501U)", notValidViewed.getRecipient());
    }

    @Test
    void setReworkedStatusOnSteps_effectiveDateNotDuplicated_whenFirstRefinementNotInvalidated() {
        // multi-recipient: RECINDEX_0 perfects the notification (first refinement), only RECINDEX_1 is invalidated
        BffNotificationDetailTimeline refinement0 = new BffNotificationDetailTimeline();
        refinement0.setElementId("REFINEMENT.RECINDEX_0");
        refinement0.setCategory(BffTimelineCategory.REFINEMENT);
        refinement0.setTimestamp(OffsetDateTime.parse("2023-01-01T10:00:00Z"));

        BffNotificationDetailTimeline refinement1 = new BffNotificationDetailTimeline();
        refinement1.setElementId("REFINEMENT.RECINDEX_1");
        refinement1.setCategory(BffTimelineCategory.REFINEMENT);
        refinement1.setTimestamp(OffsetDateTime.parse("2023-01-01T11:00:00Z"));

        // a rework step correcting RECINDEX_1 is present in the EFFECTIVE_DATE status history
        BffNotificationDetailTimeline reworkStep = new BffNotificationDetailTimeline();
        reworkStep.setElementId("REFINEMENT.RECINDEX_1.REWORK_1");
        reworkStep.setCategory(BffTimelineCategory.REFINEMENT);
        reworkStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("REFINEMENT.RECINDEX_1");

        NotificationStatusHistoryInvalidatedElement invalidatedEffectiveDate = new NotificationStatusHistoryInvalidatedElement();
        invalidatedEffectiveDate.setStatus(NotificationStatusV26.EFFECTIVE_DATE);
        invalidatedEffectiveDate.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidatedEffectiveDate.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidatedEffectiveDate)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory effectiveDateHistory = new BffNotificationStatusHistory();
        effectiveDateHistory.setStatus(BffNotificationStatus.EFFECTIVE_DATE);
        effectiveDateHistory.setSteps(new ArrayList<>(List.of(reworkStep)));
        effectiveDateHistory.setRelatedTimelineElements(new ArrayList<>(List.of("REFINEMENT.RECINDEX_1.REWORK_1")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent, refinement0, refinement1)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(effectiveDateHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        // no NOT_VALID EFFECTIVE_DATE entry created
        assertTrue(notification.getNotificationStatusHistory().stream()
                .noneMatch(sh -> sh.getStatus() == BffNotificationStatus.EFFECTIVE_DATE
                        && sh.getReworkedStatus() == BffNotificationReworkedStatus.NOT_VALID));
        // original EFFECTIVE_DATE not marked VALID
        assertNull(effectiveDateHistory.getReworkedStatus());
    }

    @Test
    void setReworkedStatusOnSteps_effectiveDateDuplicated_whenFirstRefinementInvalidated() {
        // first (and only) refinement IS invalidated → EFFECTIVE_DATE must be duplicated
        BffNotificationDetailTimeline refinement0 = new BffNotificationDetailTimeline();
        refinement0.setElementId("REFINEMENT.RECINDEX_0");
        refinement0.setCategory(BffTimelineCategory.REFINEMENT);
        refinement0.setTimestamp(OffsetDateTime.parse("2023-01-01T10:00:00Z"));

        BffNotificationDetailTimeline refinementStep = new BffNotificationDetailTimeline();
        refinementStep.setElementId("REFINEMENT.RECINDEX_0");
        refinementStep.setCategory(BffTimelineCategory.REFINEMENT);
        refinementStep.setTimestamp(OffsetDateTime.parse("2023-01-01T10:00:00Z"));

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("REFINEMENT.RECINDEX_0");

        NotificationStatusHistoryInvalidatedElement invalidatedEffectiveDate = new NotificationStatusHistoryInvalidatedElement();
        invalidatedEffectiveDate.setStatus(NotificationStatusV26.EFFECTIVE_DATE);
        invalidatedEffectiveDate.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidatedEffectiveDate.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidatedEffectiveDate)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory effectiveDateHistory = new BffNotificationStatusHistory();
        effectiveDateHistory.setStatus(BffNotificationStatus.EFFECTIVE_DATE);
        effectiveDateHistory.setSteps(new ArrayList<>(List.of(refinementStep)));
        effectiveDateHistory.setRelatedTimelineElements(new ArrayList<>(List.of("REFINEMENT.RECINDEX_0")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent, refinement0)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(effectiveDateHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        BffNotificationStatusHistory notValidEntry = notification.getNotificationStatusHistory().stream()
                .filter(sh -> sh.getStatus() == BffNotificationStatus.EFFECTIVE_DATE
                        && sh.getReworkedStatus() == BffNotificationReworkedStatus.NOT_VALID)
                .findFirst().orElse(null);

        assertNotNull(notValidEntry);
    }

    @Test
    void setReworkedStatusOnSteps_analogProgressReworkedAlwaysMarkedValid() {
        // PN-18239: SEND_ANALOG_PROGRESS with REWORK_SUFFIX is always marked VALID
        BffNotificationDetailTimeline analogProgressStep = new BffNotificationDetailTimeline();
        analogProgressStep.setElementId("someId.REWORK_1");
        analogProgressStep.setCategory(BffTimelineCategory.SEND_ANALOG_PROGRESS);
        analogProgressStep.setTimestamp(OffsetDateTime.now());

        TimelineElementV28 relatedElem = new TimelineElementV28();
        relatedElem.setElementId("unrelated");

        NotificationStatusHistoryInvalidatedElement invalidated = new NotificationStatusHistoryInvalidatedElement();
        invalidated.setStatus(NotificationStatusV26.ACCEPTED);
        invalidated.setRelatedTimelineElements(new ArrayList<>(List.of(relatedElem)));
        invalidated.setActiveFrom(OffsetDateTime.now());

        BffNotificationDetailTimelineDetails details = new BffNotificationDetailTimelineDetails();
        details.setInvalidatedTimelineAndStatusHistory(new ArrayList<>(List.of(invalidated)));

        BffNotificationDetailTimeline reworkedEvent = new BffNotificationDetailTimeline();
        reworkedEvent.setCategory(BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED);
        reworkedEvent.setTimestamp(OffsetDateTime.now());
        reworkedEvent.setDetails(details);

        BffNotificationStatusHistory deliveredHistory = new BffNotificationStatusHistory();
        deliveredHistory.setStatus(BffNotificationStatus.DELIVERED);
        deliveredHistory.setSteps(new ArrayList<>(List.of(analogProgressStep)));
        deliveredHistory.setRelatedTimelineElements(new ArrayList<>(List.of("someId.REWORK_1")));

        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setTimeline(new ArrayList<>(List.of(reworkedEvent)));
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(deliveredHistory)));

        NotificationDetailUtility.setReworkedStatusOnSteps(notification);

        assertEquals(BffNotificationReworkedStatus.VALID, analogProgressStep.getReworkedStatus());
    }

    // endregion
}