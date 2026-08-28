package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationTimelineMapper;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;

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

        List<BffNotificationTimelineEvent> analogPrepareFailures = new ArrayList<>();

        List<BffNotificationTimelineEvent> digitalScheduleWorkflows = new ArrayList<>();

        // Events with no attempt of their own
        List<BffNotificationTimelineEvent> closingEvents = new ArrayList<>();

        for (BffNotificationDetailTimeline sourceStep : CommonUtility.safeList(sourceStatusHistory.getSteps())) {
            // Discard hidden events that do not contain legal facts
            if (!shouldKeep(sourceStep)) {
                continue;
            }

            BffNotificationTimelineEvent event = mapper.mapTimelineElement(sourceStep);

            if (event.getCategory() == BffTimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE) {
                analogPrepareFailures.add(event);
                continue;
            }

            if (event.getCategory() == BffTimelineCategory.SCHEDULE_DIGITAL_WORKFLOW) {
                digitalScheduleWorkflows.add(event);
                continue;
            }

            if (TimelineEventUtility.extractClosingTargetCategories(event.getCategory()) != null) {
                closingEvents.add(event);
                continue;
            }

            handleGroupableEvent(event, recipients, groups, outputSteps);
        }

        associateAnalogPrepareFailures(analogPrepareFailures, recipients, groups, outputSteps);
        associateDigitalScheduleWorkflows(digitalScheduleWorkflows, groups, outputSteps);
        associateClosingEvents(closingEvents, groups, outputSteps);

        // Complete group metadata and apply the ordering
        sortAndEnrichGroups(groups.values());
        sortOutputSteps(outputSteps);

        return outputSteps;
    }

    /**
     * Gouping an event: resolves its group category, channel,
     * recipient index and attempt, then joins or creates the matching group. Falls back to a
     * plain event when the category is not groupable
     *
     * @param event       timeline event
     * @param recipients  notification recipients, used to populate a newly created group
     * @param groups      groups accumulated so far, keyed by group id
     * @param outputSteps timeline steps being built
     */
    private static void handleGroupableEvent(
            BffNotificationTimelineEvent event,
            List<NotificationRecipientV24> recipients,
            Map<String, BffNotificationTimelineGroup> groups,
            List<BffNotificationTimelineStep> outputSteps) {

        BffNotificationTimelineGroupCategory groupCategory =
                TimelineEventUtility.extractGroupCategory(event.getCategory());

        // Fallback: Non-groupable categories remain plain events
        if (groupCategory == null) {
            outputSteps.add(asStep(event));
            return;
        }

        // Extract all data required by the grouping key
        Integer recIndex = TimelineEventUtility.extractRecIndex(event);
        BffNotificationTimelineGroupChannel channel = TimelineEventUtility.extractChannel(event, groupCategory);
        Integer attempt = TimelineEventUtility.extractAttempt(
                event,
                groupCategory,
                channel
        );

        Optional<NotificationRecipientV24> recipient = findRecipient(recipients, recIndex);

        // Fallback to a plain event when the grouping data is incomplete
        if (!canBeGrouped(
                groupCategory,
                channel,
                recIndex,
                attempt,
                recipient
        )) {
            outputSteps.add(asStep(event));
            return;
        }

        String groupId = buildGroupId(
                groupCategory,
                recIndex,
                attempt
        );

        BffNotificationTimelineGroup group = getOrCreateGroup(
                groupId,
                groupCategory,
                channel,
                recIndex,
                attempt,
                recipient.orElseThrow(),
                groups,
                outputSteps
        );

        // Add the event to the group
        group.addEventsItem(event);
    }

    /**
     * Returns the existing group for the given ID, or creates it and registers it as a new step
     * when it doesn't exist yet.
     *
     * @param groupId     ID of the group
     * @param category    Group category
     * @param channel     Group channel
     * @param recIndex    Recipient index
     * @param attempt     Group attempt
     * @param recipient   The current recipient, used only if the group needs to be created
     * @param groups      groups accumulated so far, keyed by group id
     * @param outputSteps timeline steps being built
     * @return the existing or newly created group
     */
    private static BffNotificationTimelineGroup getOrCreateGroup(
            String groupId,
            BffNotificationTimelineGroupCategory category,
            BffNotificationTimelineGroupChannel channel,
            Integer recIndex,
            Integer attempt,
            NotificationRecipientV24 recipient,
            Map<String, BffNotificationTimelineGroup> groups,
            List<BffNotificationTimelineStep> outputSteps) {

        return groups.computeIfAbsent(groupId, id -> {
            BffNotificationTimelineGroup group = createGroup(id, category, channel, recIndex, attempt, recipient);
            outputSteps.add(asStep(group));
            return group;
        });
    }

    /**
     * Associates each event of a deferred list with the group resolved by the given resolver, or
     * falls back to a plain event when no group can be resolved.
     *
     * @param events      deferred events collected from the main loop
     * @param resolver    resolves (and creates, if needed) the group for a given event
     * @param outputSteps timeline steps being built
     */
    private static void associateOrFallback(
            List<BffNotificationTimelineEvent> events,
            Function<BffNotificationTimelineEvent, Optional<BffNotificationTimelineGroup>> resolver,
            List<BffNotificationTimelineStep> outputSteps) {

        for (BffNotificationTimelineEvent event : events) {
            resolver.apply(event)
                    .ifPresentOrElse(
                            group -> group.addEventsItem(event),
                            () -> outputSteps.add(asStep(event))
                    );
        }
    }

    /**
     * Groups each analog prepare failure into a dedicated ANALOG_FAILURE group for its
     * recipient, carrying the real attempt number of the failed preparation
     *
     * @param analogPrepareFailures prepare failure events
     * @param recipients            notification recipients
     * @param groups                groups accumulated, keyed by group id
     * @param outputSteps           timeline steps
     */
    private static void associateAnalogPrepareFailures(
            List<BffNotificationTimelineEvent> analogPrepareFailures,
            List<NotificationRecipientV24> recipients,
            Map<String, BffNotificationTimelineGroup> groups,
            List<BffNotificationTimelineStep> outputSteps) {

        associateOrFallback(analogPrepareFailures, event -> {
            Integer recIndex = TimelineEventUtility.extractRecIndex(event);
            Integer attempt = TimelineEventUtility.extractPrepareFailureAttempt(event);
            Optional<NotificationRecipientV24> recipient = findRecipient(recipients, recIndex);

            // No group when the failed attempt or the recipient cannot be resolved
            if (recIndex == null || attempt == null || recipient.isEmpty()) {
                return Optional.empty();
            }

            String groupId = buildGroupId(BffNotificationTimelineGroupCategory.ANALOG_FAILURE, recIndex, attempt);

            return Optional.of(getOrCreateGroup(
                    groupId,
                    BffNotificationTimelineGroupCategory.ANALOG_FAILURE,
                    null,
                    recIndex,
                    attempt,
                    recipient.get(),
                    groups,
                    outputSteps
            ));
        }, outputSteps);
    }

    /**
     * Associates each closing event with the most recent group it can close, resolved for the
     * same recipient among the group categories its own category maps.
     * None of these events carry an attempt of their own
     */
    private static void associateClosingEvents(
            List<BffNotificationTimelineEvent> closingEvents,
            Map<String, BffNotificationTimelineGroup> groups,
            List<BffNotificationTimelineStep> outputSteps) {

        associateOrFallback(closingEvents, event -> {
            Integer recIndex = TimelineEventUtility.extractRecIndex(event);
            Set<BffNotificationTimelineGroupCategory> targetCategories =
                    TimelineEventUtility.extractClosingTargetCategories(event.getCategory());

            return findLatestGroup(groups.values(), targetCategories, recIndex);
        }, outputSteps);
    }

    /**
     * Finds the highest attempt among the groups of the given categories for a recipient.
     * Groups without an attempt, such as simple registered letters, cannot be used as fallback.
     */
    private static Optional<BffNotificationTimelineGroup> findLatestGroup(
            Collection<BffNotificationTimelineGroup> groups,
            Set<BffNotificationTimelineGroupCategory> categories,
            Integer recIndex) {

        if (recIndex == null) {
            return Optional.empty();
        }

        return groups.stream()
                .filter(group -> categories.contains(group.getCategory()))
                .filter(group -> Objects.equals(group.getRecIndex(), recIndex))
                .filter(group -> group.getAttempt() != null)
                .max(Comparator.comparing(BffNotificationTimelineGroup::getAttempt));
    }

    /**
     * Associates each digital schedule-workflow event with the DIGITAL group of the attempt it announces
     */
    private static void associateDigitalScheduleWorkflows(
            List<BffNotificationTimelineEvent> digitalScheduleWorkflows,
            Map<String, BffNotificationTimelineGroup> groups,
            List<BffNotificationTimelineStep> outputSteps) {

        associateOrFallback(digitalScheduleWorkflows, event -> {
            Integer recIndex = TimelineEventUtility.extractRecIndex(event);

            return findNextDigitalGroup(groups.values(), recIndex, event.getTimestamp());
        }, outputSteps);
    }

    /**
     * Finds the DIGITAL group of a recipient whose earliest event starts right after the given
     * timestamp: the closest such group is the attempt cycle a schedule-workflow event, which
     * has no attempt of its own, is announcing.
     */
    private static Optional<BffNotificationTimelineGroup> findNextDigitalGroup(
            Collection<BffNotificationTimelineGroup> groups,
            Integer recIndex,
            OffsetDateTime after) {

        if (recIndex == null || after == null) {
            return Optional.empty();
        }

        return groups.stream()
                .filter(group -> group.getCategory() == BffNotificationTimelineGroupCategory.DIGITAL)
                .filter(group -> Objects.equals(group.getRecIndex(), recIndex))
                .filter(group -> earliestEventTimestamp(group) != null && earliestEventTimestamp(group).isAfter(after))
                .min(Comparator.comparing(NotificationTimelineUtility::earliestEventTimestamp));
    }

    /**
     * Returns the timestamp of the earliest event in a group
     */
    private static OffsetDateTime earliestEventTimestamp(BffNotificationTimelineGroup group) {
        return group.getEvents().stream()
                .map(BffNotificationTimelineEvent::getTimestamp)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
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
            BffNotificationTimelineGroupChannel channel,
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
     * Builds the identifier used both as group ID and grouping map key.
     * Scoped to the steps of a single status: category and recipient index already make the
     * group unique there (the channel cannot change across attempts of the same category for a
     * given recipient), attempt is only added when the flow has one.
     *
     * @param category Current category
     * @param recIndex Recipient Index
     * @param attempt  Current attempt
     * @return the group identifier
     */
    private static String buildGroupId(
            BffNotificationTimelineGroupCategory category,
            Integer recIndex,
            Integer attempt) {

        List<String> parts = new ArrayList<>(List.of(
                category.getValue(),
                "RECINDEX_" + recIndex
        ));

        if (attempt != null) {
            parts.add("ATTEMPT_" + attempt);
        }

        return String.join("_", parts);
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
            BffNotificationTimelineGroupChannel channel,
            Integer recIndex,
            Integer attempt,
            NotificationRecipientV24 recipient) {

        BffNotificationTimelineGroup group = new BffNotificationTimelineGroup();

        group.setGroupId(groupId);
        group.setCategory(category);
        group.setChannel(channel);
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
     * Orders groups by recipient and descending attempt, and plain events from newest to oldest,
     * each independently of the other while preserving their original slot positions.
     *
     * @param outputSteps timeline steps to order
     */
    private static void sortOutputSteps(List<BffNotificationTimelineStep> outputSteps) {

        if (outputSteps.isEmpty()) {
            return;
        }

        // Sort groups independently of plain events
        List<BffNotificationTimelineGroup> sortedGroups = outputSteps.stream()
                .map(BffNotificationTimelineStep::getGroup)
                .filter(Objects::nonNull)
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
            for (BffNotificationTimelineStep step : outputSteps) {
                if (step.getGroup() != null) {
                    step.setGroup(sortedGroupIterator.next());
                }
            }
        }

        // Sort plain events independently of groups
        List<BffNotificationTimelineEvent> sortedEvents = outputSteps.stream()
                .map(BffNotificationTimelineStep::getEvent)
                .filter(Objects::nonNull)
                .sorted(
                        Comparator.comparing(
                                BffNotificationTimelineEvent::getTimestamp,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ).reversed()
                )
                .toList();

        if (sortedEvents.size() > 1) {
            Iterator<BffNotificationTimelineEvent> sortedEventIterator = sortedEvents.iterator();

            // Reinsert sorted events into their original slots, preserving group positions
            for (BffNotificationTimelineStep step : outputSteps) {
                if (step.getEvent() != null) {
                    step.setEvent(sortedEventIterator.next());
                }
            }
        }
    }

    /**
     * Wraps a plain event into a timeline step
     *
     * @param event timeline event
     * @return the step exposing the event
     */
    private static BffNotificationTimelineStep asStep(BffNotificationTimelineEvent event) {
        BffNotificationTimelineStep step = new BffNotificationTimelineStep();

        step.setStepType(BffNotificationTimelineStepType.EVENT);
        step.setEvent(event);

        return step;
    }

    /**
     * Wraps a group into a timeline step
     *
     * @param group group of events
     * @return the step exposing the group
     */
    private static BffNotificationTimelineStep asStep(BffNotificationTimelineGroup group) {
        BffNotificationTimelineStep step = new BffNotificationTimelineStep();

        step.setStepType(BffNotificationTimelineStepType.GROUP);
        step.setGroup(group);

        return step;
    }
}
