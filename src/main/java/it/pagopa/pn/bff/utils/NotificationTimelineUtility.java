package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationTimelineMapper;

import java.util.*;

/**
 * Builds the status history exposed by the notification timeline API
 */
public class NotificationTimelineUtility {

    /**
     * Maps and populates the status history of the timeline API response
     *
     * @param source source notification already processed
     * @param target target timeline response
     * @param mapper MapStruct mapper used for status and event mapping
     */
    public static void populateNotificationStatusHistory(
            BffFullNotificationV1 source,
            BffNotificationTimelineResponse target,
            NotificationTimelineMapper mapper) {

        List<BffNotificationTimelineStatusHistory> mappedStatuses = new ArrayList<>();

        for (BffNotificationStatusHistory sourceStatus : CommonUtility.safeList(source.getNotificationStatusHistory())) {
            BffNotificationTimelineStatusHistory mappedStatus = mapper.mapStatusHistory(sourceStatus);

            mappedStatus.setSteps(mapSteps(sourceStatus, source.getRecipients(), mapper));

            mappedStatuses.add(mappedStatus);
        }

        target.setNotificationStatusHistory(mappedStatuses);
    }

    /**
     * Filters, maps and groups the steps of a notification status
     *
     * @param sourceStatusHistory Source notification status history
     * @param recipients          List of notification's recipients
     * @param mapper              MapStruct mapper
     * @return transformed timeline steps
     */
    private static List<BffNotificationTimelineStep> mapSteps(
            BffNotificationStatusHistory sourceStatusHistory,
            List<NotificationRecipientV24> recipients,
            NotificationTimelineMapper mapper
    ) {
        Map<String, BffNotificationTimelineGroup> groups =
                new LinkedHashMap<>();

        List<BffNotificationTimelineStep> outputSteps =
                new ArrayList<>();

        for (BffNotificationDetailTimeline sourceStep : CommonUtility.safeList(sourceStatusHistory.getSteps())) {
            // Discard hidden events that do not contain legal facts
            if (!shouldKeep(sourceStep)) {
                continue;
            }

            BffNotificationTimelineEvent event = mapper.mapTimelineElement(sourceStep);

            // Check the event to determine whether it belongs to a delivery group
            BffNotificationTimelineGroupCategory groupCategory =
                    TimelineEventUtility.extractGroupCategory(event.getCategory());

            // Non-groupable categories remain plain events
            if (groupCategory == null) {
                outputSteps.add(event);
                continue;
            }

            // Extract all data required by the grouping key
            Integer recIndex = TimelineEventUtility.extractRecIndex(event);
            String channel = TimelineEventUtility.extractChannel(event, groupCategory);
            Integer attempt = TimelineEventUtility.extractAttempt(
                    event,
                    groupCategory,
                    channel
            );

            Optional<NotificationRecipientV24> recipient = findRecipient(recipients, recIndex);

            // Fall back to a plain event when the grouping data is incomplete
            if (!canBeGrouped(
                    groupCategory,
                    channel,
                    recIndex,
                    attempt,
                    recipient
            )) {
                outputSteps.add(event);
                continue;
            }

            String groupId = buildGroupId(
                    sourceStatusHistory,
                    groupCategory,
                    channel,
                    recIndex,
                    attempt
            );

            BffNotificationTimelineGroup group = groups.get(groupId);

            // Reuse an existing group or create it at the first matching event
            if (group == null) {
                group = createGroup(
                        groupId,
                        groupCategory,
                        channel,
                        recIndex,
                        attempt,
                        recipient.orElseThrow()
                );

                groups.put(groupId, group);

                outputSteps.add(group);
            }

            // Add the event to the group
            group.addEventsItem(event);
        }

        // Complete group metadata and apply the ordering
        sortAndEnrichGroups(groups.values());
        sortOutputSteps(outputSteps);

        return outputSteps;
    }

    /**
     * Keeps visible events and hidden events containing at least one legal fact
     *
     * @param event timeline event
     * @return true when the event must be returned
     */
    private static boolean shouldKeep(BffNotificationDetailTimeline event) {
        boolean hidden = Boolean.TRUE.equals(event.getHidden());
        boolean hasLegalFacts = event.getLegalFactsIds() != null && !event.getLegalFactsIds().isEmpty();

        return !hidden || hasLegalFacts;
    }

    /**
     * Finds the recipient associated with a recipient index
     *
     * @param recipients notification recipients
     * @param recIndex   recipient index
     * @return the recipient, or an empty optional for an invalid index
     */
    private static Optional<NotificationRecipientV24> findRecipient(
            List<NotificationRecipientV24> recipients,
            Integer recIndex) {

        if (recIndex == null
                || recIndex < 0
                || recipients == null
                || recIndex >= recipients.size()) {
            return Optional.empty();
        }

        return Optional.ofNullable(recipients.get(recIndex));
    }

    /**
     * Checks whether all data required to create a group is available
     *
     * @param category  Event category
     * @param channel   Event channel
     * @param recIndex  Recipient Index
     * @param attempt   Current attempt
     * @param recipient resolved recipient
     * @return true when the event can be grouped
     */
    private static boolean canBeGrouped(
            BffNotificationTimelineGroupCategory category,
            String channel,
            Integer recIndex,
            Integer attempt,
            Optional<NotificationRecipientV24> recipient) {

        if (category == null
                || channel == null
                || recIndex == null
                || recipient.isEmpty()) {
            return false;
        }

        return !TimelineEventUtility.requiresAttempt(category, channel) || attempt != null;
    }

    /**
     * Builds the identifier used both as group ID and grouping map key
     *
     * @param status   The current status
     * @param category Current category
     * @param channel  Channel
     * @param recIndex Recipient Index
     * @param attempt  Current attempt
     * @return the group identifier
     */
    private static String buildGroupId(
            BffNotificationStatusHistory status,
            BffNotificationTimelineGroupCategory category,
            String channel,
            Integer recIndex,
            Integer attempt) {

        String activeFrom = status.getActiveFrom() != null
                ? status.getActiveFrom().toInstant().toString()
                : "NO_ACTIVE_FROM";

        String reworkStatus = status.getReworkedStatus() != null
                ? status.getReworkedStatus().getValue()
                : "NO_REWORK";

        return String.join(
                "_",
                status.getStatus().getValue(),
                activeFrom,
                reworkStatus,
                category.getValue(),
                TimelineEventUtility.normalizeChannel(channel),
                "RECINDEX_" + recIndex,
                attempt != null ? "ATTEMPT_" + attempt : "NO_ATTEMPT"
        );
    }


    /**
     * Creates an empty timeline group for the resolved recipient and delivery data
     *
     * @param groupId   ID of the group
     * @param category  Group category (ANALOG, DIGITAL, COURTESY)
     * @param channel   Group channel
     * @param recIndex  Recipient index
     * @param attempt   Group attempt
     * @param recipient The current recipient
     * @return a timeline group without events
     */
    private static BffNotificationTimelineGroup createGroup(
            String groupId,
            BffNotificationTimelineGroupCategory category,
            String channel,
            Integer recIndex,
            Integer attempt,
            NotificationRecipientV24 recipient) {

        BffNotificationTimelineGroup group = new BffNotificationTimelineGroup();

        group.setStepType(BffNotificationTimelineStepType.GROUP);
        group.setGroupId(groupId);
        group.setCategory(category);
        group.setChannel(TimelineEventUtility.normalizeChannel(channel));
        group.setRecIndex(recIndex);
        group.setAttempt(attempt);
        group.setDenomination(recipient.getDenomination());
        group.setTaxId(recipient.getTaxId());
        group.setHasReworkedEvents(false);
        group.setEvents(new ArrayList<>());

        return group;
    }

    /**
     * Sorts group events from newest to oldest and populates derived group fields
     *
     * @param groups Timeline groups
     */
    private static void sortAndEnrichGroups(
            Collection<BffNotificationTimelineGroup> groups) {

        Comparator<BffNotificationTimelineEvent> latestFirst =
                Comparator.comparing(
                        BffNotificationTimelineEvent::getTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())
                );

        for (BffNotificationTimelineGroup group : groups) {
            // Sort first so the following enrichment can use the latest event
            group.getEvents().sort(latestFirst);

            // Mark groups containing valid or invalid reworked events
            group.setHasReworkedEvents(
                    group.getEvents().stream()
                            .anyMatch(event ->
                                    event.getReworkedStatus() != null)
            );

            // Expose the latest registered letter code available in the group
            group.setRegisteredLetterCode(
                    extractLatestRegisteredLetterCode(
                            group.getEvents()
                    )
            );
        }
    }

    /**
     * Returns the registered letter code from the latest event that contains one
     *
     * @param events Grouped events
     * @return the latest registered letter code, or null
     */
    private static String extractLatestRegisteredLetterCode(
            List<BffNotificationTimelineEvent> events) {

        return events.stream()
                .filter(event -> event.getDetails() != null)
                .map(event ->
                        event.getDetails().getRegisteredLetterCode())
                .filter(code ->
                        code != null && !code.isBlank())
                .findFirst()
                .orElse(null);
    }

    /**
     * Orders groups by recipient and descending attempt while preserving plain-event positions.
     * A list containing only events is ordered from newest to oldest.
     *
     * @param outputSteps timeline steps to order
     */
    private static void sortOutputSteps(List<BffNotificationTimelineStep> outputSteps) {

        if (outputSteps.isEmpty()) {
            return;
        }

        // Sort groups independently of plain events
        List<BffNotificationTimelineGroup> sortedGroups = outputSteps.stream()
                .filter(BffNotificationTimelineGroup.class::isInstance)
                .map(BffNotificationTimelineGroup.class::cast)
                .sorted(
                        Comparator
                                .comparing(BffNotificationTimelineGroup::getRecIndex)
                                .thenComparing(
                                        BffNotificationTimelineGroup::getAttempt,
                                        Comparator.nullsLast(Comparator.reverseOrder())
                                )
                )
                .toList();

        if (sortedGroups.size() > 1) {
            Iterator<BffNotificationTimelineGroup> sortedGroupIterator = sortedGroups.iterator();

            // Reinsert sorted groups into their original slots, preserving plain-event positions
            for (int index = 0; index < outputSteps.size(); index++) {
                if (outputSteps.get(index) instanceof BffNotificationTimelineGroup) {
                    outputSteps.set(index, sortedGroupIterator.next());
                }
            }
        }

        boolean onlyEvents = outputSteps.stream().allMatch(BffNotificationTimelineEvent.class::isInstance);

        // When no groups are present, sort the whole event list by timestamp
        if (onlyEvents) {
            outputSteps.sort(
                    Comparator.comparing(
                            step -> ((BffNotificationTimelineEvent) step).getTimestamp(),
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ).reversed()
            );
        }
    }
}
