package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.*;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementCategoryV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementDetailsV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationTimelineMapper;

import java.util.*;

/**
 * Builds the parts of the informal notification timeline API response that require business
 * logic beyond a direct field mapping
 */
public class InformalNotificationTimelineUtility {

    /**
     * Categories of timeline elements that are exposed to the frontend as timeline steps
     */
    private static final List<InformalTimelineElementCategoryV1> VISIBLE_CATEGORIES = Arrays.asList(
            InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK,
            InformalTimelineElementCategoryV1.SEND_ANALOG_MESSAGE_FEEDBACK,
            InformalTimelineElementCategoryV1.DELIVERED,
            InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED,
            InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_SKIP
    );

    /**
     * Source channel of a notification viewed from the SEND web portal
     */
    private static final String WEB_SOURCE_CHANNEL = "WEB";

    /**
     * Computes the communication outcomes by checking the presence of DELIVERED and INFORMAL_NOTIFICATION_VIEWED events
     *
     * @param timeline the notification timeline
     * @return the communication outcomes object
     */
    public static CommunicationOutcomes computeCommunicationOutcomes(List<InformalTimelineElementV1> timeline) {
        boolean delivered = false;
        boolean viewed = false;

        for (InformalTimelineElementV1 element : CommonUtility.safeList(timeline)) {
            if (element.getCategory() == InformalTimelineElementCategoryV1.DELIVERED) {
                delivered = true;
            } else if (element.getCategory() == InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED) {
                viewed = true;
            }

            if (delivered && viewed) {
                break;
            }
        }

        return new CommunicationOutcomes()
                .delivered(delivered)
                .viewed(viewed);
    }

    /**
     * Maps and populates the status history of the timeline API response.
     * The events of each status are grouped by channel. Statuses and the events inside each
     * group go from the most recent to the oldest, while groups are ordered by the first event
     * of their channel, from the most recent to the oldest.
     *
     * @param source source notification
     * @param target target timeline response
     * @param mapper MapStruct mapper used for status and event mapping
     */
    public static void populateNotificationStatusHistory(
            FullSentInformalNotificationV1 source,
            BffFullSentInformalNotificationTimelineV1 target,
            InformalNotificationTimelineMapper mapper) {

        List<BffInformalNotificationTimelineStatusHistoryV1> mappedStatuses = new ArrayList<>();

        Map<String, InformalTimelineElementV1> timelineByElementId = indexTimelineByElementId(source.getTimeline());

        for (InformalNotificationStatusHistoryElementV1 sourceStatus : CommonUtility.safeList(source.getNotificationStatusHistory())) {
            BffInformalNotificationTimelineStatusHistoryV1 mappedStatus = mapper.mapStatusHistory(sourceStatus);

            List<InformalTimelineElementV1> events = resolveVisibleEvents(sourceStatus, timelineByElementId);
            mappedStatus.setSteps(groupByChannel(events, mapper));

            mappedStatuses.add(mappedStatus);
        }

        Collections.reverse(mappedStatuses);

        target.setNotificationStatusHistory(mappedStatuses);
    }

    /**
     * Indexes the timeline by element id, keeping the first element for each id so that the
     * first-match semantics are preserved when resolving related timeline elements.
     *
     * @param timeline the notification timeline
     * @return a map from element id to the first timeline element with that id
     */
    private static Map<String, InformalTimelineElementV1> indexTimelineByElementId(
            List<InformalTimelineElementV1> timeline) {

        Map<String, InformalTimelineElementV1> timelineByElementId = new HashMap<>();

        for (InformalTimelineElementV1 element : CommonUtility.safeList(timeline)) {
            timelineByElementId.putIfAbsent(element.getElementId(), element);
        }

        return timelineByElementId;
    }

    /**
     * Resolves a status history element relatedTimelineElements into the corresponding
     * timeline events, keeping only the categories visible to the frontend
     *
     * @param status               the source status history element
     * @param timelineByElementId  the timeline indexed by element id
     * @return the visible events, in the order returned by pn-delivery
     */
    private static List<InformalTimelineElementV1> resolveVisibleEvents(
            InformalNotificationStatusHistoryElementV1 status,
            Map<String, InformalTimelineElementV1> timelineByElementId) {

        List<InformalTimelineElementV1> events = new ArrayList<>();

        for (String elementId : CommonUtility.safeList(status.getRelatedTimelineElements())) {
            InformalTimelineElementV1 element = timelineByElementId.get(elementId);
            if (element != null && VISIBLE_CATEGORIES.contains(element.getCategory())) {
                events.add(element);
            }
        }

        return events;
    }

    /**
     * Groups the events by channel. Groups are created in the order in which their channel first
     * appears and then reversed, so they are ordered by the first event of their channel, from
     * the most recent to the oldest. The events inside each group are reversed too, so they go
     * from the most recent to the oldest. Events whose channel cannot be resolved are grouped
     * under the UNKNOWN channel
     *
     * @param events the events of a status, in the order returned by pn-delivery
     * @param mapper MapStruct mapper used for event mapping
     * @return the groups of events, ordered by the first event of their channel
     */
    private static List<BffInformalNotificationTimelineGroup> groupByChannel(
            List<InformalTimelineElementV1> events,
            InformalNotificationTimelineMapper mapper) {

        Map<BffNotificationChannelType, BffInformalNotificationTimelineGroup> groups = new LinkedHashMap<>();

        for (InformalTimelineElementV1 event : events) {
            BffNotificationChannelType channel = resolveChannel(event);

            if (channel == null) {
                channel = BffNotificationChannelType.UNKNOWN;
            }

            groups.computeIfAbsent(channel, key -> new BffInformalNotificationTimelineGroup().channel(key))
                    .addEventsItem(mapper.mapTimelineElement(event));
        }

        List<BffInformalNotificationTimelineGroup> steps = new ArrayList<>(groups.values());
        steps.forEach(group -> Collections.reverse(group.getEvents()));
        Collections.reverse(steps);

        return steps;
    }

    /**
     * Resolves the channel of a visible event. Analog feedbacks don't have the channel in their
     * details, while readings are identified by their source channel (a reading from the web
     * portal belongs to SEND)
     *
     * @param event the timeline event
     * @return the channel of the event, or null when it cannot be resolved
     */
    private static BffNotificationChannelType resolveChannel(InformalTimelineElementV1 event) {
        if (event.getCategory() == InformalTimelineElementCategoryV1.SEND_ANALOG_MESSAGE_FEEDBACK) {
            return BffNotificationChannelType.ANALOG;
        }

        InformalTimelineElementDetailsV1 details = event.getDetails();
        if (details == null) {
            return null;
        }

        if (event.getCategory() == InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED) {
            return WEB_SOURCE_CHANNEL.equals(details.getSourceChannel())
                    ? BffNotificationChannelType.SEND
                    : toChannelType(details.getSourceChannel());
        }

        return toChannelType(details.getChannel());
    }

    /**
     * Converts a channel value into the channel type, without failing on unknown values
     *
     * @param value the channel value
     * @return the channel type, or null when the value is unknown
     */
    private static BffNotificationChannelType toChannelType(String value) {
        return Arrays.stream(BffNotificationChannelType.values())
                .filter(channel -> channel.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}
