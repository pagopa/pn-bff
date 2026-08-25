package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationDetailTimelineDetails;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationTimelineEvent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationTimelineGroupCategory;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffTimelineCategory;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.DigitalAddress;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.ServiceLevel;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods used to classify timeline events and extract their grouping data
 */
public class TimelineEventUtility {
    private static final Pattern REC_INDEX_PATTERN =
            Pattern.compile("(?:^|\\.)RECINDEX_(\\d+)(?:\\.|$)");

    private static final Pattern ATTEMPT_PATTERN =
            Pattern.compile("(?:^|\\.)ATTEMPT_(\\d+)(?:\\.|$)");

    private static final String SERCQ_CHANNEL = "SERCQ";

    private static final String COURTESY_CHANNEL = "COURTESY";

    public static final String SIMPLE_REGISTERED_LETTER_CHANNEL = "SIMPLE_REGISTERED_LETTER";

    /**
     * Maps each groupable event category to the related group category
     */
    private static final Map<BffTimelineCategory, BffNotificationTimelineGroupCategory> GROUP_CATEGORY_BY_EVENT =
            Map.ofEntries(
                    Map.entry(
                            BffTimelineCategory.SEND_COURTESY_MESSAGE,
                            BffNotificationTimelineGroupCategory.COURTESY
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                            BffNotificationTimelineGroupCategory.DIGITAL
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_DIGITAL_PROGRESS,
                            BffNotificationTimelineGroupCategory.DIGITAL
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_DIGITAL_FEEDBACK,
                            BffNotificationTimelineGroupCategory.DIGITAL
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_ANALOG_DOMICILE,
                            BffNotificationTimelineGroupCategory.ANALOG
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_ANALOG_PROGRESS,
                            BffNotificationTimelineGroupCategory.ANALOG
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_ANALOG_FEEDBACK,
                            BffNotificationTimelineGroupCategory.ANALOG
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER,
                            BffNotificationTimelineGroupCategory.ANALOG
                    ),
                    Map.entry(
                            BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                            BffNotificationTimelineGroupCategory.ANALOG
                    )
            );

    /**
     * Returns the group category associated with a timeline event category
     *
     * @param eventCategory timeline event category
     * @return the related group category, or null when the event is not groupable
     */
    public static BffNotificationTimelineGroupCategory extractGroupCategory(BffTimelineCategory eventCategory) {
        return eventCategory != null
                ? GROUP_CATEGORY_BY_EVENT.get(eventCategory)
                : null;
    }

    /**
     * Extracts the recipient index from event details, falling back to the element ID.
     *
     * @param event timeline event
     * @return the recipient index, or null when it cannot be determined
     */
    public static Integer extractRecIndex(BffNotificationTimelineEvent event) {
        BffNotificationDetailTimelineDetails details = event.getDetails();

        if (details != null && details.getRecIndex() != null) {
            return details.getRecIndex();
        }

        return extractNumber(REC_INDEX_PATTERN, event.getElementId(), false);
    }

    /**
     * Extracts the channel used to group the event.
     * Courtesy and simple registered letter events use a conventional channel.
     *
     * @param event         timeline event
     * @param groupCategory group category of the event
     * @return the normalized channel, or null when it cannot be determined
     */
    public static String extractChannel(
            BffNotificationTimelineEvent event,
            BffNotificationTimelineGroupCategory groupCategory) {

        if (groupCategory == null) {
            return null;
        }

        // Use conventional channels for flows without a meaningful source channel
        if (isSimpleRegisteredLetter(event.getCategory())) {
            return SIMPLE_REGISTERED_LETTER_CHANNEL;
        }

        if (groupCategory == BffNotificationTimelineGroupCategory.COURTESY) {
            return COURTESY_CHANNEL;
        }

        BffNotificationDetailTimelineDetails details = event.getDetails();

        if (details == null) {
            return null;
        }

        // Digital events expose the channel in the digital address type. Progress events may not
        // carry it: in that case the event cannot be grouped and is returned as a plain event.
        if (groupCategory == BffNotificationTimelineGroupCategory.DIGITAL) {
            return Optional.ofNullable(details.getDigitalAddress())
                    .map(DigitalAddress::getType)
                    .map(TimelineEventUtility::normalizeChannel)
                    .orElse(null);
        }

        // Analog events expose the channel in the delivery service level. Progress events may not
        // carry it: in that case the event cannot be grouped and is returned as a plain event.
        if (groupCategory == BffNotificationTimelineGroupCategory.ANALOG) {
            return Optional.ofNullable(details.getServiceLevel())
                    .map(ServiceLevel::getValue)
                    .map(TimelineEventUtility::normalizeChannel)
                    .orElse(null);
        }

        return null;
    }

    /**
     * Extracts the one-based delivery attempt from event details or from the element ID
     *
     * @param event         timeline event
     * @param groupCategory group category of the event
     * @param channel       normalized event channel
     * @return the one-based attempt, or null for flows without attempts or missing data
     */
    public static Integer extractAttempt(
            BffNotificationTimelineEvent event,
            BffNotificationTimelineGroupCategory groupCategory,
            String channel) {

        // Exclude flows for which an attempt is not part of the grouping key
        if (groupCategory == null
                || groupCategory
                == BffNotificationTimelineGroupCategory.COURTESY
                || isSercqSendEvent(groupCategory, channel)
                || isSimpleRegisteredLetter(event.getCategory())) {
            return null;
        }

        BffNotificationDetailTimelineDetails details = event.getDetails();

        // Digital events has retryNumber as attempt. The counters are zero-based
        if (details != null
                && groupCategory
                == BffNotificationTimelineGroupCategory.DIGITAL
                && details.getRetryNumber() != null
                && details.getRetryNumber() >= 0) {

            return details.getRetryNumber() + 1;
        }

        if (details != null
                && groupCategory
                == BffNotificationTimelineGroupCategory.ANALOG
                && details.getSentAttemptMade() != null
                && details.getSentAttemptMade() >= 0) {

            return incrementAttempt(details.getSentAttemptMade());
        }

        // Fall back to the element ID for events without an attempt field
        return extractNumber(ATTEMPT_PATTERN, event.getElementId(), true);
    }

    /**
     * Extracts the one-based attempt of an analog prepare failure from its related prepare request.
     * The attempt encoded in the request ID is zero-based.
     *
     * @param event analog prepare failure event
     * @return the one-based attempt, or null when it cannot be determined
     */
    public static Integer extractPrepareFailureAttempt(BffNotificationTimelineEvent event) {
        BffNotificationDetailTimelineDetails details = event.getDetails();

        if (details == null) {
            return null;
        }

        return extractNumber(ATTEMPT_PATTERN, details.getPrepareRequestId(), true);
    }

    /**
     * Converts a zero-based attempt to the one-based value exposed to the frontend
     */
    private static Integer incrementAttempt(Integer zeroBasedAttempt) {
        try {
            return Math.addExact(zeroBasedAttempt, 1);
        } catch (ArithmeticException exception) {
            return null;
        }
    }

    /**
     * Normalizes a channel for comparisons and group identifiers
     *
     * @param channel event channel
     * @return the trimmed uppercase channel, or null when blank
     */
    public static String normalizeChannel(String channel) {
        return channel == null || channel.isBlank()
                ? null
                : channel.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Checks whether an attempt is required to group the given flow
     *
     * @param category group category
     * @param channel  normalized event channel
     * @return true when the attempt is required
     */
    public static boolean requiresAttempt(
            BffNotificationTimelineGroupCategory category,
            String channel) {

        if (category == BffNotificationTimelineGroupCategory.COURTESY) {
            return false;
        }

        if (TimelineEventUtility.SIMPLE_REGISTERED_LETTER_CHANNEL.equals(channel)) {
            return false;
        }

        return !TimelineEventUtility.isSercqSendEvent(category, channel);
    }

    /**
     * Checks whether the category and channel identify a SERCQ event
     *
     * @param category group category
     * @param channel  event channel
     * @return true for digital SERCQ events
     */
    public static boolean isSercqSendEvent(BffNotificationTimelineGroupCategory category, String channel) {
        return category
                == BffNotificationTimelineGroupCategory.DIGITAL
                && SERCQ_CHANNEL.equalsIgnoreCase(channel);
    }

    /**
     * Returns whether the category belongs to the simple registered letter flow.
     */
    private static boolean isSimpleRegisteredLetter(BffTimelineCategory category) {
        return category == BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER ||
                category == BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS;
    }

    /**
     * Extracts a numeric component from an element ID.
     *
     * @param pattern   pattern identifying the numeric component
     * @param elementId timeline element ID
     * @param increment whether the extracted value must be converted from zero-based to one-based
     * @return the extracted number, or null when missing or invalid
     */
    private static Integer extractNumber(
            Pattern pattern,
            String elementId,
            boolean increment) {

        if (elementId == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(elementId);

        if (!matcher.find()) {
            return null;
        }

        try {
            int value = Integer.parseInt(matcher.group(1));
            return increment ? Math.addExact(value, 1) : value;
        } catch (NumberFormatException | ArithmeticException exception) {
            return null;
        }
    }
}
