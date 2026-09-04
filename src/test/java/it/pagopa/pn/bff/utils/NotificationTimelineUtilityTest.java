package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationTimelineMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTimelineUtilityTest {

    private BffNotificationDetailTimeline step(String elementId, String timestamp, BffTimelineCategory category,
                                               BffNotificationDetailTimelineDetails details) {
        return new BffNotificationDetailTimeline()
                .elementId(elementId)
                .timestamp(OffsetDateTime.parse(timestamp))
                .category(category)
                .hidden(false)
                .details(details);
    }

    private static NotificationRecipientV24 recipient(String taxId, String denomination) {
        NotificationRecipientV24 recipient = new NotificationRecipientV24();
        recipient.setTaxId(taxId);
        recipient.setDenomination(denomination);
        return recipient;
    }

    private static final List<NotificationRecipientV24> RECIPIENTS = List.of(
            recipient("TSTUTN00A07A001A", "Utente test 1"),
            recipient("TSTUTN00A07A001B", "Utente test 2"));

    /**
     * Runs the utility on a single DELIVERING status and returns its steps
     */
    private List<BffNotificationTimelineStep> deliveringSteps(BffNotificationDetailTimeline... steps) {
        return statusSteps(BffNotificationStatus.DELIVERING, OffsetDateTime.parse("2023-08-25T09:00:00Z"), steps);
    }

    /**
     * Runs the utility on a single status with the given status/activeFrom and returns its steps
     */
    private List<BffNotificationTimelineStep> statusSteps(BffNotificationStatus status, OffsetDateTime activeFrom,
                                                            BffNotificationDetailTimeline... steps) {
        BffFullNotificationV1 source = new BffFullNotificationV1()
                .iun("RTRD-UDGU-QTQY-202308-P-1")
                .recipients(RECIPIENTS)
                .notificationStatusHistory(List.of(new BffNotificationStatusHistory()
                        .status(status)
                        .activeFrom(activeFrom)
                        .steps(List.of(steps))));

        BffNotificationTimelineResponse target = new BffNotificationTimelineResponse();
        NotificationTimelineUtility.populateNotificationStatusHistory(source, target, NotificationTimelineMapper.modelMapper);

        return target.getNotificationStatusHistory().get(0).getSteps();
    }

    private BffNotificationDetailTimelineDetails analogDetails() {
        return new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)
                .serviceLevel(ServiceLevel.AR_REGISTERED_LETTER);
    }

    private BffNotificationDetailTimeline digitalDelivery(int recIndex, int retryNumber, String channel, String timestamp) {
        return step("SEND_DIGITAL.RECINDEX_" + recIndex + ".ATTEMPT_" + retryNumber, timestamp,
                BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                new BffNotificationDetailTimelineDetails().recIndex(recIndex).retryNumber(retryNumber)
                        .digitalAddress(new DigitalAddress().type(channel)));
    }

    @Test
    void groupDeliveriesByChannelAndAttempt() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_DIGITAL.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                        new BffNotificationDetailTimelineDetails().recIndex(0).retryNumber(0)
                                .digitalAddress(new DigitalAddress().type("PEC"))),
                step("SEND_DIGITAL_FEEDBACK.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:20:00Z", BffTimelineCategory.SEND_DIGITAL_FEEDBACK,
                        new BffNotificationDetailTimelineDetails().recIndex(0).retryNumber(0)
                                .digitalAddress(new DigitalAddress().type("PEC"))),
                step("SEND_DIGITAL.RECINDEX_0.ATTEMPT_1", "2023-08-25T09:30:00Z", BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                        new BffNotificationDetailTimelineDetails().recIndex(0).retryNumber(1)
                                .digitalAddress(new DigitalAddress().type("PEC"))));

        assertEquals(2, steps.size());
        assertTrue(steps.stream().allMatch(step -> step.getStepType() == BffNotificationTimelineStepType.GROUP));

        BffNotificationTimelineGroup secondAttempt = steps.get(0).getGroup();
        assertEquals(2, secondAttempt.getAttempt());

        BffNotificationTimelineGroup firstAttempt = steps.get(1).getGroup();
        assertEquals(1, firstAttempt.getAttempt());
        assertEquals(BffNotificationTimelineGroupChannel.PEC, firstAttempt.getChannel());
        assertEquals(BffNotificationTimelineGroupCategory.DIGITAL, firstAttempt.getCategory());
        assertEquals(RECIPIENTS.get(0).getTaxId(), firstAttempt.getTaxId());
        assertEquals(RECIPIENTS.get(0).getDenomination(), firstAttempt.getDenomination());
        assertEquals(
                List.of("SEND_DIGITAL_FEEDBACK.RECINDEX_0.ATTEMPT_0", "SEND_DIGITAL.RECINDEX_0.ATTEMPT_0"),
                firstAttempt.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());

        assertNotEquals(firstAttempt.getGroupId(), secondAttempt.getGroupId());
    }

    @Test
    void keepHiddenEventsOnlyWithLegalFacts() {
        BffNotificationDetailTimeline withLegalFacts = step("HIDDEN_WITH_LEGAL_FACTS", "2023-08-25T09:20:00Z",
                BffTimelineCategory.REFINEMENT, null)
                .legalFactsIds(List.of(new BffLegalFactId().key("legal-fact-key")))
                .hidden(true);
        BffNotificationDetailTimeline withoutLegalFacts = step("HIDDEN_WITHOUT_LEGAL_FACTS", "2023-08-25T09:30:00Z",
                BffTimelineCategory.SCHEDULE_ANALOG_WORKFLOW, null)
                .hidden(true);
        BffNotificationDetailTimeline visible = step("VISIBLE", "2023-08-25T09:10:00Z",
                BffTimelineCategory.REQUEST_ACCEPTED, null);

        List<BffNotificationTimelineStep> steps = deliveringSteps(withLegalFacts, withoutLegalFacts, visible);

        // the hidden event without legal facts is discarded, the others are sorted from the newest
        assertEquals(
                List.of("HIDDEN_WITH_LEGAL_FACTS", "VISIBLE"),
                steps.stream().map(step -> step.getEvent().getElementId()).toList());
    }

    @Test
    void groupsEventEvenWhenChannelIsMissing() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_ANALOG_PROGRESS.RECINDEX_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_ANALOG_PROGRESS,
                        new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.GROUP, steps.get(0).getStepType());
        assertNull(steps.get(0).getGroup().getChannel());
    }

    @Test
    void groupsEventEvenWhenDetailsAreMissing() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_DIGITAL.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                        null));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.GROUP, steps.get(0).getStepType());
        assertEquals(1, steps.get(0).getGroup().getAttempt());
        assertNull(steps.get(0).getGroup().getChannel());
    }

    @Test
    void fallbackToEventWhenAttemptCannotBeResolved() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_DIGITAL.RECINDEX_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                        null));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.EVENT, steps.get(0).getStepType());
        assertEquals("SEND_DIGITAL.RECINDEX_0", steps.get(0).getEvent().getElementId());
    }

    @Test
    void groupIdIsIndependentOfStatusAndActiveFrom() {
        List<BffNotificationTimelineStep> withStatus = deliveringSteps(
                digitalDelivery(0, 0, "PEC", "2023-08-25T09:10:00Z"));
        List<BffNotificationTimelineStep> withoutStatus = statusSteps(null, null,
                digitalDelivery(0, 0, "PEC", "2023-08-25T09:10:00Z"));

        assertEquals(
                withStatus.get(0).getGroup().getGroupId(),
                withoutStatus.get(0).getGroup().getGroupId());
    }

    @Test
    void prepareFailureFormsItsOwnGroupWithARealAttempt() {
        // the prepare failure of a second attempt that never produced a send forms its own
        // ANALOG_FAILURE group, carrying the real attempt number from its prepare request id;
        // the earlier send stays in its own, unrelated attempt group
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_ANALOG.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_ANALOG_DOMICILE,
                        new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)
                                .serviceLevel(ServiceLevel.AR_REGISTERED_LETTER)),
                step("PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_0", "2023-08-25T09:20:00Z",
                        BffTimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE,
                        new BffNotificationDetailTimelineDetails().recIndex(0)
                                .prepareRequestId("PREPARE_ANALOG_DOMICILE.RECINDEX_0.ATTEMPT_1")));

        assertEquals(2, steps.size());

        BffNotificationTimelineGroup failureGroup = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG_FAILURE, failureGroup.getCategory());
        assertNull(failureGroup.getChannel());
        assertEquals(2, failureGroup.getAttempt());
        assertEquals(
                List.of("PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_0"),
                failureGroup.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());

        BffNotificationTimelineGroup sendGroup = steps.get(1).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG, sendGroup.getCategory());
        assertEquals(1, sendGroup.getAttempt());
    }

    @Test
    void workflowFailureJoinsThePrepareFailureGroupWhenBothExist() {
        // the workflow failure has no attempt of its own: when a prepare failure already created
        // an ANALOG_FAILURE group more recent than the send group, it joins that one
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_ANALOG.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_ANALOG_DOMICILE,
                        new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)
                                .serviceLevel(ServiceLevel.AR_REGISTERED_LETTER)),
                step("PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_0", "2023-08-25T09:20:00Z",
                        BffTimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE,
                        new BffNotificationDetailTimelineDetails().recIndex(0)
                                .prepareRequestId("PREPARE_ANALOG_DOMICILE.RECINDEX_0.ATTEMPT_1")),
                step("ANALOG_FAILURE_WORKFLOW.RECINDEX_0", "2023-08-25T09:30:00Z",
                        BffTimelineCategory.ANALOG_FAILURE_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(2, steps.size());

        BffNotificationTimelineGroup failureGroup = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG_FAILURE, failureGroup.getCategory());
        assertEquals(2, failureGroup.getAttempt());
        assertEquals(
                List.of("ANALOG_FAILURE_WORKFLOW.RECINDEX_0", "PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_0"),
                failureGroup.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());

        BffNotificationTimelineGroup sendGroup = steps.get(1).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG, sendGroup.getCategory());
        assertEquals(1, sendGroup.getAttempt());
        assertEquals(
                List.of("SEND_ANALOG.RECINDEX_0.ATTEMPT_0"),
                sendGroup.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());
    }

    @Test
    void workflowFailureJoinsTheLatestSendGroupWhenNoPrepareFailureExists() {
        // a successfully prepared attempt that still fails on its own outcome closes out with a
        // workflow failure alone: it joins the existing send group, no ANALOG_FAILURE group is
        // created for it
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_ANALOG.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_ANALOG_DOMICILE,
                        new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)
                                .serviceLevel(ServiceLevel.AR_REGISTERED_LETTER)),
                step("ANALOG_FAILURE_WORKFLOW.RECINDEX_0", "2023-08-25T09:20:00Z",
                        BffTimelineCategory.ANALOG_FAILURE_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(1, steps.size());

        BffNotificationTimelineGroup group = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG, group.getCategory());
        assertEquals(1, group.getAttempt());
        assertEquals(
                List.of("ANALOG_FAILURE_WORKFLOW.RECINDEX_0", "SEND_ANALOG.RECINDEX_0.ATTEMPT_0"),
                group.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());
    }

    @Test
    void completelyUnreachableJoinsThePrepareFailureGroupWhenBothExist() {
        // COMPLETELY_UNREACHABLE has no attempt of its own either: it is resolved exactly like
        // ANALOG_FAILURE_WORKFLOW, joining the ANALOG_FAILURE group already created by the
        // prepare failure
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_ANALOG.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_ANALOG_DOMICILE,
                        new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)
                                .serviceLevel(ServiceLevel.AR_REGISTERED_LETTER)),
                step("PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_0", "2023-08-25T09:20:00Z",
                        BffTimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE,
                        new BffNotificationDetailTimelineDetails().recIndex(0)
                                .prepareRequestId("PREPARE_ANALOG_DOMICILE.RECINDEX_0.ATTEMPT_1")),
                step("COMPLETELY_UNREACHABLE.RECINDEX_0", "2023-08-25T09:30:00Z",
                        BffTimelineCategory.COMPLETELY_UNREACHABLE,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(2, steps.size());

        BffNotificationTimelineGroup failureGroup = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG_FAILURE, failureGroup.getCategory());
        assertEquals(2, failureGroup.getAttempt());
        assertEquals(
                List.of("COMPLETELY_UNREACHABLE.RECINDEX_0", "PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_0"),
                failureGroup.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());

        BffNotificationTimelineGroup sendGroup = steps.get(1).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG, sendGroup.getCategory());
    }

    @Test
    void completelyUnreachableJoinsTheLatestSendGroupWhenNoPrepareFailureExists() {
        // no prepare failure exists: COMPLETELY_UNREACHABLE joins the existing send group, no
        // ANALOG_FAILURE group is created for it
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_ANALOG.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_ANALOG_DOMICILE,
                        new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)
                                .serviceLevel(ServiceLevel.AR_REGISTERED_LETTER)),
                step("COMPLETELY_UNREACHABLE.RECINDEX_0", "2023-08-25T09:20:00Z",
                        BffTimelineCategory.COMPLETELY_UNREACHABLE,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(1, steps.size());

        BffNotificationTimelineGroup group = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.ANALOG, group.getCategory());
        assertEquals(1, group.getAttempt());
        assertEquals(
                List.of("COMPLETELY_UNREACHABLE.RECINDEX_0", "SEND_ANALOG.RECINDEX_0.ATTEMPT_0"),
                group.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());
    }

    @Test
    void prepareFailureFallsBackToEventWhenRecipientCannotBeResolved() {
        // an out-of-range recipient index cannot be resolved: the failure cannot join any group
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_9", "2023-08-25T09:10:00Z",
                        BffTimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE,
                        new BffNotificationDetailTimelineDetails().recIndex(9)
                                .prepareRequestId("PREPARE_ANALOG_DOMICILE.RECINDEX_9.ATTEMPT_0")));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.EVENT, steps.get(0).getStepType());
        assertEquals("PREPARE_ANALOG_DOMICILE_FAILURE.RECINDEX_9", steps.get(0).getEvent().getElementId());
    }

    @Test
    void workflowFailureFallsBackToEventWhenNoAnalogGroupExists() {
        // no analog activity at all for this recipient: the workflow failure cannot join any group
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("ANALOG_FAILURE_WORKFLOW.RECINDEX_0", "2023-08-25T09:10:00Z",
                        BffTimelineCategory.ANALOG_FAILURE_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.EVENT, steps.get(0).getStepType());
        assertEquals("ANALOG_FAILURE_WORKFLOW.RECINDEX_0", steps.get(0).getEvent().getElementId());
    }

    @Test
    void digitalFailureWorkflowJoinsTheLatestDigitalGroup() {
        // DIGITAL_FAILURE_WORKFLOW has no attempt of its own either: there is no digital
        // prepare-failure category, so it always joins the most recent existing DIGITAL group
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                digitalDelivery(0, 0, "PEC", "2023-08-25T09:10:00Z"),
                digitalDelivery(0, 1, "PEC", "2023-08-25T09:20:00Z"),
                step("DIGITAL_FAILURE_WORKFLOW.RECINDEX_0", "2023-08-25T09:30:00Z",
                        BffTimelineCategory.DIGITAL_FAILURE_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(2, steps.size());

        BffNotificationTimelineGroup latestAttempt = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.DIGITAL, latestAttempt.getCategory());
        assertEquals(2, latestAttempt.getAttempt());
        assertEquals(
                List.of("DIGITAL_FAILURE_WORKFLOW.RECINDEX_0", "SEND_DIGITAL.RECINDEX_0.ATTEMPT_1"),
                latestAttempt.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());

        BffNotificationTimelineGroup firstAttempt = steps.get(1).getGroup();
        assertEquals(1, firstAttempt.getAttempt());
    }

    @Test
    void digitalFailureWorkflowFallsBackToEventWhenNoDigitalGroupExists() {
        // no digital activity at all for this recipient: the workflow failure cannot join any group
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("DIGITAL_FAILURE_WORKFLOW.RECINDEX_0", "2023-08-25T09:10:00Z",
                        BffTimelineCategory.DIGITAL_FAILURE_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.EVENT, steps.get(0).getStepType());
        assertEquals("DIGITAL_FAILURE_WORKFLOW.RECINDEX_0", steps.get(0).getEvent().getElementId());
    }

    @Test
    void digitalSuccessWorkflowJoinsTheLatestDigitalGroup() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                digitalDelivery(0, 0, "PEC", "2023-08-25T09:10:00Z"),
                digitalDelivery(0, 1, "PEC", "2023-08-25T09:20:00Z"),
                step("DIGITAL_SUCCESS_WORKFLOW.RECINDEX_0", "2023-08-25T09:30:00Z",
                        BffTimelineCategory.DIGITAL_SUCCESS_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(2, steps.size());

        BffNotificationTimelineGroup latestAttempt = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.DIGITAL, latestAttempt.getCategory());
        assertEquals(2, latestAttempt.getAttempt());
        assertEquals(
                List.of("DIGITAL_SUCCESS_WORKFLOW.RECINDEX_0", "SEND_DIGITAL.RECINDEX_0.ATTEMPT_1"),
                latestAttempt.getEvents().stream().map(BffNotificationTimelineEvent::getElementId).toList());

        BffNotificationTimelineGroup firstAttempt = steps.get(1).getGroup();
        assertEquals(1, firstAttempt.getAttempt());
    }

    @Test
    void digitalSuccessWorkflowFallsBackToEventWhenNoDigitalGroupExists() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("DIGITAL_SUCCESS_WORKFLOW.RECINDEX_0", "2023-08-25T09:10:00Z",
                        BffTimelineCategory.DIGITAL_SUCCESS_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0)));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.EVENT, steps.get(0).getStepType());
        assertEquals("DIGITAL_SUCCESS_WORKFLOW.RECINDEX_0", steps.get(0).getEvent().getElementId());
    }

    @Test
    void scheduleDigitalWorkflowJoinsTheMatchingDigitalGroup() {
        // the schedule event carries the same zero-based attempt as the send it precedes (in its
        // own element ID): it creates the DIGITAL group first, with no channel yet, and the
        // matching send later joins that same group and backfills its channel
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SCHEDULE_DIGITAL_WORKFLOW.RECINDEX_1.ATTEMPT_1", "2023-08-25T09:00:00Z",
                        BffTimelineCategory.SCHEDULE_DIGITAL_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(1).sentAttemptMade(1)),
                digitalDelivery(1, 0, "PEC", "2023-08-25T09:10:00Z"),
                digitalDelivery(1, 1, "PEC", "2023-08-25T09:20:00Z"));

        assertEquals(2, steps.size());
        assertTrue(steps.stream().allMatch(step -> step.getStepType() == BffNotificationTimelineStepType.GROUP));

        BffNotificationTimelineGroup secondAttempt = steps.get(0).getGroup();
        assertEquals(2, secondAttempt.getAttempt());
        assertEquals(BffNotificationTimelineGroupChannel.PEC, secondAttempt.getChannel());
        assertTrue(secondAttempt.getEvents().stream()
                .anyMatch(event -> event.getElementId().equals("SCHEDULE_DIGITAL_WORKFLOW.RECINDEX_1.ATTEMPT_1")));

        BffNotificationTimelineGroup firstAttempt = steps.get(1).getGroup();
        assertEquals(1, firstAttempt.getAttempt());
        assertTrue(firstAttempt.getEvents().stream()
                .noneMatch(event -> event.getElementId().equals("SCHEDULE_DIGITAL_WORKFLOW.RECINDEX_1.ATTEMPT_1")));
    }

    @Test
    void scheduleDigitalWorkflowCreatesItsOwnGroupWhenNoneExistsYet() {
        // no other digital activity yet for this recipient: the schedule event still forms its
        // own DIGITAL group (channel is not required, and will backfill later if a matching send
        // ever joins it)
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SCHEDULE_DIGITAL_WORKFLOW.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:00:00Z",
                        BffTimelineCategory.SCHEDULE_DIGITAL_WORKFLOW,
                        new BffNotificationDetailTimelineDetails().recIndex(0).sentAttemptMade(0)));

        assertEquals(1, steps.size());
        assertEquals(BffNotificationTimelineStepType.GROUP, steps.get(0).getStepType());

        BffNotificationTimelineGroup group = steps.get(0).getGroup();
        assertEquals(BffNotificationTimelineGroupCategory.DIGITAL, group.getCategory());
        assertEquals(1, group.getAttempt());
        assertNull(group.getChannel());
    }

    @Test
    void enrichGroupMetadata() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                step("SEND_ANALOG.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:10:00Z", BffTimelineCategory.SEND_ANALOG_DOMICILE,
                        analogDetails().registeredLetterCode("RACC-001")),
                step("SEND_ANALOG_PROGRESS.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:20:00Z", BffTimelineCategory.SEND_ANALOG_PROGRESS,
                        analogDetails().registeredLetterCode("RACC-002")),
                step("SEND_ANALOG_FEEDBACK.RECINDEX_0.ATTEMPT_0", "2023-08-25T09:30:00Z", BffTimelineCategory.SEND_ANALOG_FEEDBACK,
                        analogDetails())
                        .reworkedStatus(BffNotificationReworkedStatus.VALID));

        BffNotificationTimelineGroup group = steps.get(0).getGroup();

        // the code of the most recent event that carries one
        assertEquals("RACC-002", group.getRegisteredLetterCode());
        assertTrue(group.getHasReworkedEvents());
    }

    @Test
    void sortGroupsByRecipient() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                digitalDelivery(1, 0, "PEC", "2023-08-25T09:10:00Z"),
                step("REQUEST_ACCEPTED", "2023-08-25T09:15:00Z", BffTimelineCategory.REQUEST_ACCEPTED, null),
                digitalDelivery(0, 0, "PEC", "2023-08-25T09:20:00Z"),
                digitalDelivery(0, 1, "PEC", "2023-08-25T09:30:00Z"),
                digitalDelivery(1, 1, "PEC", "2023-08-25T09:40:00Z"));

        assertEquals(BffNotificationTimelineStepType.EVENT, steps.get(1).getStepType());
        assertEquals(
                List.of("0/2", "0/1", "1/2", "1/1"),
                steps.stream()
                        .filter(step -> step.getStepType() == BffNotificationTimelineStepType.GROUP)
                        .map(step -> step.getGroup().getRecIndex() + "/" + step.getGroup().getAttempt())
                        .toList());
    }

    @Test
    void plainEventsKeepOriginalOrderWhenMixedWithGroups() {
        // plain events are not reordered: they keep their original relative order alongside groups
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                digitalDelivery(0, 0, "PEC", "2023-08-25T09:10:00Z"),
                step("REQUEST_ACCEPTED", "2023-08-25T09:15:00Z", BffTimelineCategory.REQUEST_ACCEPTED, null),
                step("NOTIFICATION_VIEWED", "2023-08-25T09:25:00Z", BffTimelineCategory.NOTIFICATION_VIEWED, null));

        assertEquals(3, steps.size());
        assertEquals(BffNotificationTimelineStepType.GROUP, steps.get(0).getStepType());

        assertEquals(
                List.of("REQUEST_ACCEPTED", "NOTIFICATION_VIEWED"),
                steps.stream()
                        .filter(step -> step.getStepType() == BffNotificationTimelineStepType.EVENT)
                        .map(step -> step.getEvent().getElementId())
                        .toList());
    }

    @Test
    void groupFlowsWithoutAttempt() {
        List<BffNotificationTimelineStep> steps = deliveringSteps(
                digitalDelivery(0, 0, "SERCQ", "2023-08-25T09:10:00Z"),
                step("SEND_SIMPLE_REGISTERED_LETTER.RECINDEX_0", "2023-08-25T09:20:00Z",
                        BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER,
                        new BffNotificationDetailTimelineDetails().recIndex(0)
                                .serviceLevel(ServiceLevel.REGISTERED_LETTER_890)));

        // SERCQ deliveries and simple registered letters are grouped without any attempt
        assertEquals(2, steps.size());
        assertTrue(steps.stream().allMatch(step -> step.getStepType() == BffNotificationTimelineStepType.GROUP));
        assertTrue(steps.stream().allMatch(step -> step.getGroup().getAttempt() == null));
        assertEquals(
                List.of(BffNotificationTimelineGroupChannel.SIMPLE_REGISTERED_LETTER, BffNotificationTimelineGroupChannel.SERCQ),
                steps.stream().map(step -> step.getGroup().getChannel()).sorted().toList());

        // the group id has no placeholder for the missing attempt
        assertTrue(steps.stream().noneMatch(step -> step.getGroup().getGroupId().contains("ATTEMPT")));
    }
}
