package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffChannelDeliveryStatusV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffChannelStatusV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationChannelType;

import java.util.*;

/**
 * Calculate the delivery status for each campaign channel
 */
public class SentInformalNotificationChannelStatusResolver {

    // Statuses allowed for each channel (except SEND)
    private static final Map<BffNotificationChannelType, Set<BffChannelStatusV1>> ALLOWED_STATUSES = Map.of(
            BffNotificationChannelType.IO, EnumSet.of(
                    BffChannelStatusV1.WAITING_TO_SEND,
                    BffChannelStatusV1.SENT,
                    BffChannelStatusV1.DELIVERED,
                    BffChannelStatusV1.VIEWED,
                    BffChannelStatusV1.UNAVAILABLE,
                    BffChannelStatusV1.NOT_DELIVERED),
            BffNotificationChannelType.SMS, EnumSet.of(
                    BffChannelStatusV1.WAITING_TO_SEND,
                    BffChannelStatusV1.SENDING,
                    BffChannelStatusV1.SENT,
                    BffChannelStatusV1.UNAVAILABLE,
                    BffChannelStatusV1.NOT_DELIVERED,
                    BffChannelStatusV1.WORKFLOW_ENDED),
            BffNotificationChannelType.EMAIL, EnumSet.of(
                    BffChannelStatusV1.WAITING_TO_SEND,
                    BffChannelStatusV1.SENDING,
                    BffChannelStatusV1.SENT,
                    BffChannelStatusV1.DELIVERED,
                    BffChannelStatusV1.UNAVAILABLE,
                    BffChannelStatusV1.NOT_DELIVERED,
                    BffChannelStatusV1.WORKFLOW_ENDED),
            BffNotificationChannelType.PEC, EnumSet.of(
                    BffChannelStatusV1.WAITING_TO_SEND,
                    BffChannelStatusV1.SENDING,
                    BffChannelStatusV1.SENT,
                    BffChannelStatusV1.DELIVERED,
                    BffChannelStatusV1.UNAVAILABLE,
                    BffChannelStatusV1.NOT_DELIVERED,
                    BffChannelStatusV1.WORKFLOW_ENDED),
            BffNotificationChannelType.ANALOG, EnumSet.of(
                    BffChannelStatusV1.WAITING_TO_SEND,
                    BffChannelStatusV1.SENDING,
                    BffChannelStatusV1.SENT,
                    BffChannelStatusV1.DELIVERED,
                    BffChannelStatusV1.NOT_DELIVERED,
                    BffChannelStatusV1.WORKFLOW_ENDED));

    public static List<BffChannelDeliveryStatusV1> populateChannelStatuses(
            InformalNotificationStatusV1 notificationStatus,
            List<InformalTimelineElementV1> timeline,
            List<BffNotificationChannelType> channels) {

        List<InformalTimelineElementV1> events = timeline == null ? List.of() : timeline;

        return channels.stream()
                .map(channel -> new BffChannelDeliveryStatusV1()
                        .channel(channel)
                        .status(channel == BffNotificationChannelType.SEND
                                ? resolveSendStatus(events)
                                : resolveChannelStatus(notificationStatus, events, channel)))
                .toList();
    }

    private static BffChannelStatusV1 resolveSendStatus(List<InformalTimelineElementV1> timeline) {
        return hasViewedFrom(timeline, "WEB") ? BffChannelStatusV1.VIEWED : BffChannelStatusV1.FILED;
    }

    private static BffChannelStatusV1 resolveChannelStatus(
            InformalNotificationStatusV1 notificationStatus,
            List<InformalTimelineElementV1> events,
            BffNotificationChannelType channel) {

        List<InformalTimelineElementV1> channelEvents = events.stream()
                .filter(el -> matchesChannel(el, channel))
                .toList();

        // 1. Read: IO only (sourceChannel == "IO")
        if (supportsStatus(channel, BffChannelStatusV1.VIEWED) && hasViewedFrom(events, "IO")) {
            return BffChannelStatusV1.VIEWED;
        }

        // 2. Delivered / not delivered: from the channel's most recent feedback
        ResponseStatus feedback = latestFeedbackOutcome(channelEvents);
        if (feedback == ResponseStatus.OK && supportsStatus(channel, BffChannelStatusV1.DELIVERED)) {
            return BffChannelStatusV1.DELIVERED;
        }
        if (feedback == ResponseStatus.KO && supportsStatus(channel, BffChannelStatusV1.NOT_DELIVERED)) {
            return BffChannelStatusV1.NOT_DELIVERED;
        }

        // 3. Channel unavailable
        if (supportsStatus(channel, BffChannelStatusV1.UNAVAILABLE)
                && hasCategory(channelEvents, InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_SKIP)) {
            return BffChannelStatusV1.UNAVAILABLE;
        }

        // 4. Sent: a dispatch exists but no feedback has arrived yet
        if (supportsStatus(channel, BffChannelStatusV1.SENT)
                && channelEvents.stream().anyMatch(el -> isDispatch(el.getCategory()))) {
            return BffChannelStatusV1.SENT;
        }

        // 5. Sending in progress
        if (supportsStatus(channel, BffChannelStatusV1.SENDING)
                && notificationStatus == InformalNotificationStatusV1.PROCESSING
                && channelEvents.isEmpty()) {
            return BffChannelStatusV1.SENDING;
        }

        // 6. Ready to send
        if (supportsStatus(channel, BffChannelStatusV1.WAITING_TO_SEND)
                && notificationStatus == InformalNotificationStatusV1.ACCEPTED) {
            return BffChannelStatusV1.WAITING_TO_SEND;
        }

        // 7. Workflow ended without this channel ever being attempted
        if (supportsStatus(channel, BffChannelStatusV1.WORKFLOW_ENDED)
                && hasCategory(events, InformalTimelineElementCategoryV1.WORKFLOW_DONE_REACHED)
                && channelEvents.isEmpty()) {
            return BffChannelStatusV1.WORKFLOW_ENDED;
        }

        // 8. No rule matched
        return BffChannelStatusV1.WAITING_TO_SEND;
    }

    private static boolean supportsStatus(BffNotificationChannelType channel, BffChannelStatusV1 status) {
        return ALLOWED_STATUSES.get(channel).contains(status);
    }

    private static boolean matchesChannel(InformalTimelineElementV1 element, BffNotificationChannelType channel) {
        if (channel == BffNotificationChannelType.ANALOG) {
            return isAnalogSend(element.getCategory());
        }
        return isDigitalSend(element.getCategory())
                && element.getDetails() != null
                && channel.getValue().equals(element.getDetails().getChannel());
    }

    private static boolean hasViewedFrom(List<InformalTimelineElementV1> events, String sourceChannel) {
        return events.stream().anyMatch(el ->
                el.getCategory() == InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED
                        && el.getDetails() != null
                        && sourceChannel.equals(el.getDetails().getSourceChannel()));
    }

    private static boolean hasCategory(List<InformalTimelineElementV1> events,
                                       InformalTimelineElementCategoryV1 category) {
        return events.stream().anyMatch(el -> el.getCategory() == category);
    }

    private static final Comparator<InformalTimelineElementV1> BY_EVENT_TIMESTAMP =
            Comparator.comparing(InformalTimelineElementV1::getEventTimestamp,
                    Comparator.nullsFirst(Comparator.naturalOrder()));

    private static ResponseStatus latestFeedbackOutcome(List<InformalTimelineElementV1> channelEvents) {
        InformalTimelineElementV1 latestFeedback = channelEvents.stream()
                .filter(el -> isFeedback(el.getCategory()))
                .filter(el -> responseStatusOf(el) != null)
                .max(BY_EVENT_TIMESTAMP)
                .orElse(null);

        return latestFeedback == null ? null : responseStatusOf(latestFeedback);
    }

    private static ResponseStatus responseStatusOf(InformalTimelineElementV1 element) {
        InformalTimelineElementDetailsV1 details = element.getDetails();
        return details == null ? null : details.getResponseStatus();
    }

    private static boolean isDigitalSend(InformalTimelineElementCategoryV1 category) {
        if (category == null) {
            return false;
        }
        return switch (category) {
            case SEND_DIGITAL_MESSAGE,
                 SEND_DIGITAL_MESSAGE_PROGRESS,
                 SEND_DIGITAL_MESSAGE_FEEDBACK,
                 SEND_DIGITAL_MESSAGE_SKIP -> true;
            default -> false;
        };
    }

    private static boolean isAnalogSend(InformalTimelineElementCategoryV1 category) {
        if (category == null) {
            return false;
        }
        return switch (category) {
            case SEND_ANALOG_MESSAGE,
                 SEND_ANALOG_MESSAGE_PROGRESS,
                 SEND_ANALOG_MESSAGE_FEEDBACK -> true;
            default -> false;
        };
    }

    private static boolean isDispatch(InformalTimelineElementCategoryV1 category) {
        if (category == null) {
            return false;
        }
        return switch (category) {
            case SEND_DIGITAL_MESSAGE,
                 SEND_DIGITAL_MESSAGE_PROGRESS,
                 SEND_ANALOG_MESSAGE,
                 SEND_ANALOG_MESSAGE_PROGRESS -> true;
            default -> false;
        };
    }

    private static boolean isFeedback(InformalTimelineElementCategoryV1 category) {
        if (category == null) {
            return false;
        }
        return switch (category) {
            case SEND_DIGITAL_MESSAGE_FEEDBACK,
                 SEND_ANALOG_MESSAGE_FEEDBACK -> true;
            default -> false;
        };
    }
}