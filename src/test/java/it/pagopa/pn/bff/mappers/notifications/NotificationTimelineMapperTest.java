package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.utils.CommonUtility;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTimelineMapperTest {

    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();

    private BffFullNotificationV1 mockNotificationDetail() {
        return NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(
                notificationDetailPaMock.getNotificationMultiRecipientMock(), null);
    }

    private List<BffNotificationTimelineGroup> groupsOf(BffNotificationTimelineResponse timeline) {
        return timeline.getNotificationStatusHistory().stream()
                .flatMap(status -> CommonUtility.safeList(status.getSteps()).stream())
                .filter(step -> step.getStepType() == BffNotificationTimelineStepType.GROUP)
                .map(BffNotificationTimelineStep::getGroup)
                .toList();
    }

    @Test
    void mapNotificationTimelineNull() {
        assertNull(NotificationTimelineMapper.modelMapper.mapNotificationTimeline(null));
    }

    @Test
    void mapNotificationTimelineCopiesNotificationData() {
        BffFullNotificationV1 detail = mockNotificationDetail();

        BffNotificationTimelineResponse timeline = NotificationTimelineMapper.modelMapper.mapNotificationTimeline(detail);

        assertEquals(detail.getIun(), timeline.getIun());
        assertEquals(detail.getSubject(), timeline.getSubject());
        assertEquals(detail.getRecipients(), timeline.getRecipients());
        assertEquals(detail.getNotificationStatusHistory().size(), timeline.getNotificationStatusHistory().size());

        for (int i = 0; i < timeline.getNotificationStatusHistory().size(); i++) {
            BffNotificationStatusHistory source = detail.getNotificationStatusHistory().get(i);
            BffNotificationTimelineStatusHistory mapped = timeline.getNotificationStatusHistory().get(i);

            assertEquals(source.getStatus(), mapped.getStatus());
            assertEquals(source.getActiveFrom(), mapped.getActiveFrom());
            assertEquals(source.getRecipient(), mapped.getViewedByMandate());
            assertNotNull(mapped.getSteps());
        }
    }

    @Test
    void mapNotificationTimelineGroupsDeliveriesByRecipientChannelAndAttempt() {
        BffNotificationTimelineResponse timeline = NotificationTimelineMapper.modelMapper
                .mapNotificationTimeline(mockNotificationDetail());

        List<BffNotificationTimelineGroup> groups = groupsOf(timeline);

        assertFalse(groups.isEmpty());
        assertEquals(groups.size(), groups.stream().map(BffNotificationTimelineGroup::getGroupId).distinct().count());

        // the mock sends one PEC delivery to each recipient, with retryNumber 0 mapped to attempt 1
        List<BffNotificationTimelineGroup> pecGroups = groups.stream()
                .filter(group -> group.getCategory() == BffNotificationTimelineGroupCategory.DIGITAL)
                .filter(group -> "PEC".equals(group.getChannel()))
                .toList();

        assertEquals(2, pecGroups.size());

        for (BffNotificationTimelineGroup group : pecGroups) {
            NotificationRecipientV24 recipient = timeline.getRecipients().get(group.getRecIndex());

            assertEquals(Integer.valueOf(1), group.getAttempt());
            assertEquals(recipient.getTaxId(), group.getTaxId());
            assertEquals(recipient.getDenomination(), group.getDenomination());
            assertFalse(group.getHasReworkedEvents());

            // the events of a delivery attempt are collapsed in the group, from the newest to the oldest
            assertTrue(group.getEvents().size() > 1);
            assertEquals(
                    group.getEvents().stream()
                            .sorted(Comparator.comparing(BffNotificationTimelineEvent::getTimestamp).reversed())
                            .toList(),
                    group.getEvents());
            assertTrue(group.getEvents().stream()
                    .allMatch(event -> group.getRecIndex().equals(event.getDetails().getRecIndex())));
        }

        // the courtesy message is a flow without attempts
        BffNotificationTimelineGroup courtesyGroup = groups.stream()
                .filter(group -> group.getCategory() == BffNotificationTimelineGroupCategory.COURTESY)
                .findFirst()
                .orElseThrow();

        assertEquals("COURTESY", courtesyGroup.getChannel());
        assertNull(courtesyGroup.getAttempt());

        // the groups of a status are ordered by recipient
        for (BffNotificationTimelineStatusHistory status : timeline.getNotificationStatusHistory()) {
            List<Integer> recIndexes = CommonUtility.safeList(status.getSteps()).stream()
                    .filter(step -> step.getStepType() == BffNotificationTimelineStepType.GROUP)
                    .map(step -> step.getGroup().getRecIndex())
                    .toList();

            assertEquals(recIndexes.stream().sorted().toList(), recIndexes);
        }
    }

    @Test
    void mapNotificationTimelineKeepsHiddenEventsOnlyWhenTheyHaveLegalFacts() {
        BffNotificationDetailTimeline visible = new BffNotificationDetailTimeline()
                .elementId("VISIBLE")
                .timestamp(OffsetDateTime.parse("2023-08-25T10:00:00Z"))
                .category(BffTimelineCategory.REQUEST_ACCEPTED)
                .hidden(false);

        BffNotificationDetailTimeline hiddenWithLegalFacts = new BffNotificationDetailTimeline()
                .elementId("HIDDEN_WITH_LEGAL_FACTS")
                .timestamp(OffsetDateTime.parse("2023-08-25T11:00:00Z"))
                .category(BffTimelineCategory.DIGITAL_SUCCESS_WORKFLOW)
                .legalFactsIds(List.of(new BffLegalFactId().key("legal-fact-key").category(BffLegalFactType.PEC_RECEIPT)))
                .hidden(true);

        BffNotificationDetailTimeline hiddenWithoutLegalFacts = new BffNotificationDetailTimeline()
                .elementId("HIDDEN_WITHOUT_LEGAL_FACTS")
                .timestamp(OffsetDateTime.parse("2023-08-25T12:00:00Z"))
                .category(BffTimelineCategory.SCHEDULE_ANALOG_WORKFLOW)
                .hidden(true);

        BffFullNotificationV1 notification = new BffFullNotificationV1()
                .iun("HEUJ-UEPA-HGXT-202401-N-1")
                .notificationStatusHistory(List.of(new BffNotificationStatusHistory()
                        .status(BffNotificationStatus.DELIVERING)
                        .activeFrom(OffsetDateTime.parse("2023-08-25T10:00:00Z"))
                        .steps(List.of(visible, hiddenWithLegalFacts, hiddenWithoutLegalFacts))));

        BffNotificationTimelineResponse timeline = NotificationTimelineMapper.modelMapper
                .mapNotificationTimeline(notification);

        List<BffNotificationTimelineStep> steps = timeline.getNotificationStatusHistory().get(0).getSteps();

        // the hidden event without legal facts is dropped, the remaining ones are sorted from the newest
        assertEquals(
                List.of("HIDDEN_WITH_LEGAL_FACTS", "VISIBLE"),
                steps.stream().map(step -> step.getEvent().getElementId()).toList());
        assertTrue(steps.stream().allMatch(step -> step.getStepType() == BffNotificationTimelineStepType.EVENT));
    }

    @Test
    void mapStatusHistoryExposesTheMandateRecipientAndIgnoresTheSteps() {
        BffNotificationStatusHistory source = new BffNotificationStatusHistory()
                .status(BffNotificationStatus.VIEWED)
                .activeFrom(OffsetDateTime.parse("2023-08-25T10:00:00Z"))
                .recipient("TSTUTN00A07A001G")
                .steps(List.of(new BffNotificationDetailTimeline().elementId("NOTIFICATION_VIEWED")));

        BffNotificationTimelineStatusHistory mapped = NotificationTimelineMapper.modelMapper.mapStatusHistory(source);

        assertEquals(source.getStatus(), mapped.getStatus());
        assertEquals(source.getActiveFrom(), mapped.getActiveFrom());
        assertEquals("TSTUTN00A07A001G", mapped.getViewedByMandate());
        // the source steps are not copied: they are populated by NotificationTimelineUtility
        assertTrue(mapped.getSteps().isEmpty());
    }

    @Test
    void mapTimelineElementExposesTheHiddenFlag() {
        BffNotificationDetailTimeline hidden = new BffNotificationDetailTimeline()
                .elementId("SEND_DIGITAL.IUN_HEUJ-UEPA-HGXT-202401-N-1.RECINDEX_0")
                .timestamp(OffsetDateTime.parse("2023-08-25T10:00:00Z"))
                .category(BffTimelineCategory.SEND_DIGITAL_DOMICILE)
                .hidden(true);

        BffNotificationTimelineEvent mapped = NotificationTimelineMapper.modelMapper.mapTimelineElement(hidden);

        assertEquals(hidden.getElementId(), mapped.getElementId());
        assertEquals(hidden.getTimestamp(), mapped.getTimestamp());
        assertEquals(hidden.getCategory(), mapped.getCategory());
        assertTrue(mapped.getIsHidden());

        // a timeline element without the hidden flag is exposed as visible
        assertFalse(NotificationTimelineMapper.modelMapper
                .mapTimelineElement(new BffNotificationDetailTimeline()).getIsHidden());
    }
}
