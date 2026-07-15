package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push_rework.model.ReworkItem;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.BffTimelineMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Class that contains the utility functions used to transform the data from delivery to front-end
 */
public class NotificationDetailUtility {

    /**
     * Suffix used to identify reworked timeline elements
     */
    private static final String REWORK_SUFFIX = ".REWORK_";

    /**
     * List of the allowed delivery codes for the analog flow
     * They are used to filter out those timeline events that are not included in the list
     */
    final private static List<String> AnalogFlowAllowedCodes = java.util.List.of(
            "CON020",
            "CON080",
            "RECRN001C",
            "RECRN002C",
            "RECRN002F",
            "RECRN003C",
            "RECRN004C",
            "RECRN005C",
            "RECRN006",
            "RECAG001C",
            "RECAG002C",
            "RECAG003C",
            "RECAG003F",
            "RECAG004",
            "PNAG012",
            "RECAG005C",
            "RECAG006C",
            "RECAG007C",
            "RECAG008C",
            "RECRI003C",
            "RECRI004C",
            "RECRI005",
            "RECRN011",
            "PNRN012",
            "RECRN013",
            "RECRN015",
            "RECAG011A",
            "RECAG013",
            "RECAG015",
            "RECRI001",
            "RECRI002",
            "PNALL001",
            "CON993",
            "CON995",
            "CON996",
            "CON997",
            "CON998",
            "RECRN001B",
            "RECRN002B",
            "RECRN002E",
            "RECRN003B",
            "RECRN004B",
            "RECRN005B",
            "RECAG001B",
            "RECAG002B",
            "RECAG003B",
            "RECAG003E",
            "RECAG011B",
            "RECAG005B",
            "RECAG006B",
            "RECAG007B",
            "RECAG008B",
            "RECRI003B",
            "RECRI004B",
            "RECAG010",
            "RECRS010",
            "RECRN010",
            "RECAG012"
    );

    /**
     * List of the allowed timeline categories for the analog flow
     * They are used to filter out those timeline categories that are not included in the list
     */
    final private static List<BffTimelineCategory> TimelineAllowedAnalogCategories =
            Arrays.asList(BffTimelineCategory.SEND_ANALOG_PROGRESS, BffTimelineCategory.SEND_ANALOG_FEEDBACK,
                    BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS);

    /**
     * List of the allowed timeline categories
     * On front-end side we don't show all the categories but only those listed below
     */
    final private static List<BffTimelineCategory> TimelineAllowedCategories =
            Arrays.asList(
                    BffTimelineCategory.SCHEDULE_DIGITAL_WORKFLOW,
                    // PN-6902
                    BffTimelineCategory.ANALOG_FAILURE_WORKFLOW,
                    BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                    BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER,
                    BffTimelineCategory.SEND_ANALOG_DOMICILE,
                    BffTimelineCategory.SEND_DIGITAL_FEEDBACK,
                    BffTimelineCategory.SEND_DIGITAL_PROGRESS,
                    // PN-2068
                    BffTimelineCategory.SEND_COURTESY_MESSAGE,
                    // PN-1647
                    BffTimelineCategory.NOT_HANDLED,
                    BffTimelineCategory.SEND_ANALOG_PROGRESS,
                    BffTimelineCategory.SEND_ANALOG_FEEDBACK,
                    BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                    // PN-7743
                    BffTimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE
            );

    /**
     * Compares two timeline elements and returns an integer representing the order between them
     * This is used to sort the timeline categories into each status
     * The desired order is descending (from earliest to the oldest)
     * For those categories that have the same timestamp, we use the index properties (assuming that delivery returns
     * the categories in ascending order)
     *
     * @param a the first timeline element
     * @param b the second timeline element
     * @return an integer representing the order between the two elements
     */
    public static Integer fromLatestToEarliest(BffNotificationDetailTimeline a, BffNotificationDetailTimeline b) {
        long differenceInTimeline = b.getTimestamp().toInstant().toEpochMilli() - a.getTimestamp().toInstant().toEpochMilli();
        int differenceInIndex = (b.getIndex() != null && a.getIndex() != null) ? b.getIndex() - a.getIndex() : 0;

        if (differenceInTimeline > 0) {
            return 1;
        } else if (differenceInTimeline < 0) {
            return -1;
        } else {
            return Integer.compare(differenceInIndex, 0);
        }
    }

    /**
     * Check if the step is an internal app IO event
     * This is used to filter out those categories that are about sending messages on App IO
     *
     * @param step - The timeline step to check
     * @return true if the step is an internal app IO event, false otherwise
     */
    public static boolean isInternalAppIoEvent(BffNotificationDetailTimeline step) {
        if (step.getCategory().equals(BffTimelineCategory.SEND_COURTESY_MESSAGE)) {
            BffNotificationDetailTimelineDetails details = step.getDetails();
            return details.getDigitalAddress().getType().equals("APPIO")
                    && details.getIoSendMessageResult() != null
                    && !details.getIoSendMessageResult().equals(IoSendMessageResult.SENT_COURTESY);
        }

        return false;
    }

    /**
     * Check if the timeline element must be shown
     * This method uses the TimelineAllowedCategories to check if the timeline category must be shown
     *
     * @param t - The timeline element to check
     * @return true if the timeline element must be shown, false otherwise
     */
    public static boolean timelineElementMustBeShown(BffNotificationDetailTimeline t) {
        if (TimelineAllowedAnalogCategories.contains(t.getCategory())) {
            String deliveryDetailCode = t.getDetails().getDeliveryDetailCode();
            return deliveryDetailCode != null && AnalogFlowAllowedCodes.contains(deliveryDetailCode);
        }

        return TimelineAllowedCategories.contains(t.getCategory());
    }

    /**
     * Se the CANCELLATION_IN_PROGRESS status for the notification.
     * The cancellation flow is async. So we pass through a cancellation request before the cancellation process ends.
     * On delivery side, this means that we have a NOTIFICATION_CANCELLATION_REQUEST timeline category.
     * On front-end side, we translate this timeline category into a new notification status (CANCELLATION_IN_PROGRESS).
     * This status must be added only if the cancellation process isn't already ended (no NOTIFICATION_CANCELLED category into the timeline)
     *
     * @param bffFullNotificationV1 the notification to populate
     */
    public static void insertCancelledStatusInTimeline(BffFullNotificationV1 bffFullNotificationV1) {
        BffNotificationDetailTimeline timelineCancelledElement = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == BffTimelineCategory.NOTIFICATION_CANCELLED)
                .findFirst()
                .orElse(null);

        if (timelineCancelledElement == null) {
            BffNotificationDetailTimeline timelineCancellationRequestElement = bffFullNotificationV1.getTimeline().stream()
                    .filter(el -> el.getCategory() == BffTimelineCategory.NOTIFICATION_CANCELLATION_REQUEST)
                    .findFirst()
                    .orElse(null);

            if (timelineCancellationRequestElement != null) {

                BffNotificationStatusHistory notificationStatusHistoryElement =
                        new BffNotificationStatusHistory(BffNotificationStatus.CANCELLATION_IN_PROGRESS,
                                timelineCancellationRequestElement.getTimestamp(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                null,
                                null,
                                null
                        );

                bffFullNotificationV1.getNotificationStatusHistory().add(notificationStatusHistoryElement);
                bffFullNotificationV1.setNotificationStatus(BffNotificationStatus.CANCELLATION_IN_PROGRESS);
            }
        }
    }

    /**
     * Remove those relatedTimelineElements that aren't in the timeline array
     *
     * @param bffFullNotificationV1 the notification to clean
     */
    public static void cleanRelatedTimelineElements(BffFullNotificationV1 bffFullNotificationV1) {
        for (BffNotificationStatusHistory notificationStatusHistory : bffFullNotificationV1.getNotificationStatusHistory()) {
            List<String> cleanedRelatedTimelineElements = new ArrayList<>();
            for (String relatedTimelineElement : notificationStatusHistory.getRelatedTimelineElements()) {
                bffFullNotificationV1.getTimeline()
                        .stream()
                        .filter(elem -> elem.getElementId().equals(relatedTimelineElement))
                        .findFirst()
                        .ifPresent(timelineElem -> cleanedRelatedTimelineElements.add(relatedTimelineElement));
            }
            notificationStatusHistory.setRelatedTimelineElements(cleanedRelatedTimelineElements);
        }
    }

    /**
     * Move the AAR documents from the timeline to a separate key in the notification model
     *
     * @param bffFullNotificationV1 the notification to populate
     */
    public static void populateOtherDocuments(BffFullNotificationV1 bffFullNotificationV1) {
        List<BffNotificationDetailDocument> otherDocuments = new ArrayList<>();

        List<BffNotificationDetailTimeline> timelineFiltered = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == BffTimelineCategory.AAR_GENERATION)
                .toList();

        if (!timelineFiltered.isEmpty()) {


            for (BffNotificationDetailTimeline timeline : timelineFiltered) {
                final BffDocumentRecipientData recipientData = new BffDocumentRecipientData();

                final Integer recIndex = timeline.getDetails().getRecIndex();
                final List<NotificationRecipientV24> recipients = bffFullNotificationV1.getRecipients();
                if (recIndex != null) {
                    recipientData.setDenomination(recipients.get(recIndex).getDenomination());
                    recipientData.setTaxId(recipients.get(recIndex).getTaxId());
                }

                BffNotificationDetailDocument document = new BffNotificationDetailDocument()
                        .recIndex(recIndex)
                        .documentId(timeline.getDetails().getGeneratedAarUrl())
                        .documentType(BffLegalFactType.AAR.toString())
                        .title(null)
                        .digests(
                                new NotificationAttachmentDigests()
                                        .sha256("")
                        )
                        .ref(
                                new NotificationAttachmentBodyRef()
                                        .key("")
                                        .versionToken("")
                        )
                        .contentType("")
                        .recipient(recipientData);
                otherDocuments.add(document);
            }
        }

        bffFullNotificationV1.setOtherDocuments(otherDocuments);
    }

    /**
     * If timeline contains a NOTIFICATION_RADD_RETRIEVED event, it is set in the notification radd flag.
     * This flag indicates that the notification has been retrieved through the RADD flow
     *
     * @param bffFullNotificationV1 the notification to check
     */
    public static void checkRADDInTimeline(BffFullNotificationV1 bffFullNotificationV1) {
        bffFullNotificationV1.getTimeline()
                .stream()
                .filter(element -> element.getCategory() == BffTimelineCategory.NOTIFICATION_RADD_RETRIEVED)
                .findFirst()
                .ifPresent(bffFullNotificationV1::setRadd);
    }

    /**
     * Populates the steps for each notification status history element.
     * Each status history elements is determined by a series of timeline elements.
     * This method copies the related timeline elements into a new property (steps) into each status history element
     *
     * @param notificationDetail  the notification to populate
     * @param timelineElement     the timeline element to populate
     * @param status              the status to populate
     * @param acceptedStatusItems the accepted status items
     * @return the populated macro step
     */
    public static BffNotificationDetailTimeline populateMacroStep(
            BffFullNotificationV1 notificationDetail,
            String timelineElement,
            BffNotificationStatusHistory status,
            List<String> acceptedStatusItems
    ) {
        final BffNotificationDetailTimeline step = notificationDetail.getTimeline().stream()
                .filter(t -> t.getElementId().equals(timelineElement))
                .findFirst()
                .orElse(null);

        BffNotificationDetailTimeline timelineStep = new BffNotificationDetailTimeline();


        if (step != null) {
            BeanUtils.copyProperties(step, timelineStep);

            // hide accepted status micro steps
            if (status.getStatus().equals(BffNotificationStatus.ACCEPTED)) {
                timelineStep.setHidden(true);
                status.addStepsItem(timelineStep);
                // PN-4484 - hide the internal events related to the courtesy messages sent through app IO
            } else if (NotificationDetailUtility.isInternalAppIoEvent(step)) {
                timelineStep.setHidden(true);
                status.addStepsItem(timelineStep);
                // add legal facts for ANALOG_FAILURE_WORKFLOW steps with linked generatedAarUrl
                // since the AAR for such steps must be shown in the timeline exactly the same way as legalFacts.
                // Cfr. comment in the definition of INotificationDetailTimeline in src/models/NotificationDetail.ts.
            } else if (step.getCategory().equals(BffTimelineCategory.ANALOG_FAILURE_WORKFLOW)
                    && step.getDetails().getGeneratedAarUrl() != null) {
                timelineStep.setLegalFactsIds(java.util.List.of(new BffLegalFactId(
                        step.getDetails().getGeneratedAarUrl(),
                        BffLegalFactType.AAR
                )));
                status.addStepsItem(timelineStep);
                // remove legal facts for those microsteps that are related to the accepted status
            } else if (!acceptedStatusItems.isEmpty() && acceptedStatusItems.contains(step.getElementId())) {
                timelineStep.setLegalFactsIds(new ArrayList<>());
                status.addStepsItem(timelineStep);
                // default case
            } else {
                status.addStepsItem(timelineStep);
            }
        }

        return step;
    }

    /**
     * In addition to the population of the key steps, we apply other transformations.
     * This is because, on front-end side, choices were made to make the timeline more readable to the citizen.
     * So in this method:
     * - we move the timeline elements from the status ACCEPTED to the next one
     * - we move the timeline elements from DELIVERED to DELIVERING, if the digital workflow fails
     * - we enrich the VIEWED status with the information about the user that has opened the notification
     *
     * @param bffFullNotificationV1 the notification to populate
     */
    public static void populateMacroSteps(BffFullNotificationV1 bffFullNotificationV1) {
        ArrayList<String> acceptedStatusItems = new ArrayList<>();
        BffNotificationDeliveryMode deliveryMode = null;
        BffNotificationStatusHistory deliveringStatus = null;
        int lastDeliveredIndexToShift = -1;
        boolean lastDeliveredIndexToShiftIsFixed = false;
        boolean preventShiftFromDeliveredToDelivering = false;

        for (BffNotificationStatusHistory status : bffFullNotificationV1.getNotificationStatusHistory()) {
            if (status.getStatus().equals(BffNotificationStatus.DELIVERING)) {
                deliveringStatus = status;
            }

            if (status.getStatus().equals(BffNotificationStatus.ACCEPTED) && !status.getRelatedTimelineElements().isEmpty()) {
                acceptedStatusItems = new ArrayList<>(status.getRelatedTimelineElements());
            } else if (!acceptedStatusItems.isEmpty()) {
                status.getRelatedTimelineElements().addAll(0, acceptedStatusItems);
            }

            status.setSteps(new ArrayList<>());

            for (int ix = 0; ix < status.getRelatedTimelineElements().size(); ix++) {
                String timelineElement = status.getRelatedTimelineElements().get(ix);
                BffNotificationDetailTimeline step = populateMacroStep(
                        bffFullNotificationV1,
                        timelineElement,
                        status,
                        acceptedStatusItems
                );

                if (step != null) {
                    if (deliveryMode == null) {
                        List<BffNotificationDetailTimeline> steps = new ArrayList<>();
                        steps.add(step);
                        deliveryMode = getDeliveryMode(steps);
                    }

                    if (status.getStatus().equals(BffNotificationStatus.DELIVERED) && !preventShiftFromDeliveredToDelivering) {
                        if ((step.getCategory().equals(BffTimelineCategory.DIGITAL_FAILURE_WORKFLOW)
                                || step.getCategory().equals(BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER)
                                || step.getCategory().equals(BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS))
                                && !lastDeliveredIndexToShiftIsFixed) {
                            lastDeliveredIndexToShift = ix;
                        } else if (step.getCategory().equals(BffTimelineCategory.DIGITAL_SUCCESS_WORKFLOW)) {
                            if (lastDeliveredIndexToShift > -1) {
                                lastDeliveredIndexToShift = ix - 1;
                                lastDeliveredIndexToShiftIsFixed = true;
                            } else {
                                preventShiftFromDeliveredToDelivering = true;
                            }
                        }
                    }
                }
            }

            if (status.getStatus().equals(BffNotificationStatus.DELIVERED)
                    && deliveringStatus != null
                    && deliveringStatus.getSteps() != null
                    && !preventShiftFromDeliveredToDelivering
                    && lastDeliveredIndexToShift > -1) {

                List<BffNotificationDetailTimeline> stepsToShift = new ArrayList<>(status.getSteps().subList(0, lastDeliveredIndexToShift + 1));
                stepsToShift.sort(NotificationDetailUtility::fromLatestToEarliest);
                deliveringStatus.getSteps().addAll(0, stepsToShift);
                status.setSteps(new ArrayList<>(status.getSteps().subList(lastDeliveredIndexToShift + 1, status.getSteps().size())));
                status.setActiveFrom(deliveringStatus.getSteps().get(0).getTimestamp());
            }

            status.getSteps().sort(NotificationDetailUtility::fromLatestToEarliest);

            if (!status.getStatus().equals(BffNotificationStatus.ACCEPTED) && !acceptedStatusItems.isEmpty()) {
                acceptedStatusItems = new ArrayList<>();
            }

            // set deliveryMode for DELIVERED status using deliveryMode determined from steps
            if (status.getStatus().equals(BffNotificationStatus.DELIVERED) && deliveryMode != null) {
                status.setDeliveryMode(deliveryMode);
            }

            // set recipient for VIEWED status
            if (status.getStatus().equals(BffNotificationStatus.VIEWED)) {
                String recipient = getRecipientFromViewedSteps(status.getSteps());
                if (recipient != null) {
                    status.setRecipient(recipient);
                }
            }
        }
    }

    public static void insertReworkedStatus(BffFullNotificationV1 bffFullNotificationV1, List<ReworkItem> reworkItems) {
        List<BffNotificationDetailTimeline> reworkedTimelineElements = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED)
                .toList();

        for (BffNotificationDetailTimeline timelineElement : reworkedTimelineElements) {
            // resolve and expose the correction type on the reworked timeline element
            timelineElement.setRequestType(resolveRequestType(timelineElement, reworkItems));

            BffNotificationStatusHistory reworkedStatusHistory = new BffNotificationStatusHistory();
            reworkedStatusHistory.setStatus(BffNotificationStatus.NOTIFICATION_TIMELINE_REWORKED);
            reworkedStatusHistory.setActiveFrom(timelineElement.getTimestamp());

            bffFullNotificationV1.getNotificationStatusHistory().add(reworkedStatusHistory);
        }
    }

    /**
     * Resolves the correction type ({@link BffReworkRequestType}) for a reworked timeline element,
     * correlating it with the rework items retrieved from pn-delivery-push by matching the
     * {@code REWORK_<idx>} and {@code RECINDEX_<r>} segments shared by the reworkId
     * ({@code REWORK_<idx>.TRY_<tryIdx>}) and the elementId
     * ({@code NOTIFICATION_TIMELINE_REWORKED.IUN_<iun>.RECINDEX_<r>.ATTEMPT_<a>.REWORK_<idx>}).
     * Rework items in {@code ERROR} status are discarded.
     *
     * @param reworkedTimelineElement the reworked timeline element
     * @param reworkItems             the rework items of the notification (may be null/empty)
     * @return the resolved correction type, or {@code null} if it cannot be determined
     */
    private static BffReworkRequestType resolveRequestType(BffNotificationDetailTimeline reworkedTimelineElement, List<ReworkItem> reworkItems) {
        if (reworkItems == null || reworkItems.isEmpty() || reworkedTimelineElement.getElementId() == null) {
            return null;
        }

        return reworkItems.stream()
                // discard rework requests ended in ERROR: they don't produce a reworked element in timeline
                .filter(item -> item.getStatus() != ReworkItem.StatusEnum.ERROR)
                .filter(item -> item.getReworkId() != null && item.getRequestType() != null)
                .filter(item -> {
                    String reworkIdxSegment = item.getReworkId().split("\\.")[0];
                    boolean reworkMatch = reworkedTimelineElement.getElementId().endsWith("." + reworkIdxSegment);
                    boolean recIndexMatch = item.getRecIndex() == null
                            || reworkedTimelineElement.getElementId().contains("." + item.getRecIndex() + ".");
                    return reworkMatch && recIndexMatch;
                })
                .map(item -> BffReworkRequestType.fromValue(item.getRequestType().getValue()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Processes {@code NOTIFICATION_TIMELINE_REWORKED} events and updates the notification status
     * history to reflect invalidated statuses and steps.
     * <p>
     * For each invalidated status (skipping {@code DELIVERING}), moves the related steps from their
     * original status history into a new entry marked {@code NOT_VALID}. Steps correcting an
     * invalidated element (or of category {@code SEND_ANALOG_PROGRESS}) are marked {@code VALID},
     * and their parent status history is marked {@code VALID} as well (unless {@code VIEWED} or
     * {@code DELIVERING}). The new invalidated entries are appended to the status history list.
     * <p>
     * Punctual corrections ({@code INVALIDATE_ELEMENTS}, resolved onto the marker by
     * {@link #insertReworkedStatus}) are an exception: their invalidated events are still marked
     * {@code NOT_VALID}, but no synthetic {@code NOT_VALID} status is created and no status is marked.
     *
     * @param bffFullNotificationV1 the notification to update in place
     */
    public static void setReworkedStatusOnSteps(BffFullNotificationV1 bffFullNotificationV1) {
        List<BffNotificationDetailTimeline> reworkedEvents = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED)
                .toList();

        if (reworkedEvents.isEmpty()) {
            return;
        }

        List<String> invalidatedElementIds = reworkedEvents.stream()
                .flatMap(el -> el.getDetails().getInvalidatedTimelineAndStatusHistory().stream())
                .flatMap(invalidatedElement -> invalidatedElement.getRelatedTimelineElements().stream())
                .map(TimelineElementV28::getElementId)
                .toList();

        boolean firstRefinementNotInvalidated = false;

        /* FIRST STEP
         * create new status history entries for each invalidated statuses in REWORKED events (except DELIVERING and VIEWED), moving the related steps from the original status history to the new one
         */
        List<BffNotificationStatusHistory> newNotValidStatusHistories = new ArrayList<>();

        for (BffNotificationDetailTimeline reworkedEvent : reworkedEvents) {
            // Punctual correction (INVALIDATE_ELEMENTS): only the events are marked NOT_VALID (SECOND STEP),
            // no synthetic NOT_VALID status is created and no status is marked. Skip the FIRST STEP for it.
            if (reworkedEvent.getRequestType() == BffReworkRequestType.INVALIDATE_ELEMENTS) {
                continue;
            }

            for (NotificationStatusHistoryInvalidatedElement invalidatedStatus : reworkedEvent.getDetails().getInvalidatedTimelineAndStatusHistory()) {

                // skip ACCEPTED (PN-20141), DELIVERING and VIEWED status - keep everything together
                if (invalidatedStatus.getStatus() == NotificationStatusV26.ACCEPTED ||
                        invalidatedStatus.getStatus() == NotificationStatusV26.DELIVERING ||
                        invalidatedStatus.getStatus() == NotificationStatusV26.VIEWED) {
                    continue;
                }

                // in a multi-recipient notification, EFFECTIVE_DATE must not be duplicated if the correction does not involve the refinement that perfected the notification
                // to determine this, sort the REFINEMENT timeline events and check whether the first one is not invalidated
                if (invalidatedStatus.getStatus() == NotificationStatusV26.EFFECTIVE_DATE) {
                    List<BffNotificationDetailTimeline> refinementEvents = bffFullNotificationV1.getTimeline().stream()
                            .filter(el -> el.getCategory() == BffTimelineCategory.REFINEMENT)
                            .sorted(Comparator.comparing(BffNotificationDetailTimeline::getTimestamp))
                            .toList();

                    if (!refinementEvents.isEmpty()) {
                        String firstRefinementId = refinementEvents.get(0).getElementId();
                        if (!invalidatedElementIds.contains(firstRefinementId)) {
                            firstRefinementNotInvalidated = true;
                            continue;
                        }
                    }
                }

                BffNotificationStatusHistory newStatusHistory = new BffNotificationStatusHistory();
                newStatusHistory.setStatus(BffNotificationStatus.fromValue(invalidatedStatus.getStatus().getValue()));
                newStatusHistory.setActiveFrom(invalidatedStatus.getActiveFrom());
                newStatusHistory.setReworkedStatus(BffNotificationReworkedStatus.NOT_VALID);
                newStatusHistory.setRelatedTimelineElements(new ArrayList<>());
                newStatusHistory.setSteps(new ArrayList<>());

                List<String> relatedElementIds = invalidatedStatus.getRelatedTimelineElements().stream()
                        .map(TimelineElementV28::getElementId)
                        .toList();

                // populate steps and relatedElementId by finding each step in notificationStatusHistory
                for (String elementId : relatedElementIds) {
                    for (BffNotificationStatusHistory statusHistory : bffFullNotificationV1.getNotificationStatusHistory()) {
                        // move the step from the original status history to the new one
                        BffNotificationDetailTimeline step = statusHistory.getSteps().stream()
                                .filter(t -> t.getElementId().equals(elementId))
                                .findFirst()
                                .orElse(null);
                        if (step != null) {
                            newStatusHistory.getRelatedTimelineElements().add(elementId);
                            newStatusHistory.getSteps().add(step);

                            // clean the step and relatedTimelineElements from the original status history
                            statusHistory.getSteps().remove(step);
                            statusHistory.getRelatedTimelineElements().remove(elementId);
                        }
                    }
                }

                newStatusHistory.getSteps().sort(NotificationDetailUtility::fromLatestToEarliest);

                // set deliveryMode for DELIVERED status
                if (newStatusHistory.getStatus() == BffNotificationStatus.DELIVERED) {
                    BffNotificationDeliveryMode deliveryMode = getDeliveryMode(newStatusHistory.getSteps());
                    if (deliveryMode != null) {
                        newStatusHistory.setDeliveryMode(deliveryMode);
                    }
                }

                newNotValidStatusHistories.add(newStatusHistory);
            }
        }

        bffFullNotificationV1.getNotificationStatusHistory().addAll(newNotValidStatusHistories);

        /* SECOND STEP
         * mark the steps that correct an invalidated element as VALID, and the ones that are invalidated as NOT_VALID. Then, mark the parent status history as VALID as well (except for VIEWED, DELIVERING and EFFECTIVE_DATE when duplication must be avoided)
         */

        // mark all statusHistory steps (including new invalidated ones) with reworkedStatus
        for (BffNotificationStatusHistory statusHistory : bffFullNotificationV1.getNotificationStatusHistory()) {
            if (statusHistory.getSteps() == null || statusHistory.getSteps().isEmpty()) {
                continue;
            }

            boolean hasReworkedSteps = false;
            for (BffNotificationDetailTimeline step : statusHistory.getSteps()) {
                // check if step is invalidated first
                if (invalidatedElementIds.contains(step.getElementId())) {
                    step.setReworkedStatus(BffNotificationReworkedStatus.NOT_VALID);
                } else if (step.getElementId().contains(REWORK_SUFFIX)) {
                    // Check if this step corrects an invalidated element
                    String baseId = step.getElementId().substring(0, step.getElementId().lastIndexOf(REWORK_SUFFIX));
                    boolean correctsInvalidated = invalidatedElementIds.stream()
                            .anyMatch(id -> id.equals(baseId) || id.startsWith(baseId + REWORK_SUFFIX));

                    // PN-18239 -> Reworked elements with SEND_ANALOG_PROGRESS category must be marked as VALID even if they don't have a direct link with an invalidated element.
                    boolean isAnalogProgress = step.getCategory() == BffTimelineCategory.SEND_ANALOG_PROGRESS;

                    if (correctsInvalidated || isAnalogProgress) {
                        step.setReworkedStatus(BffNotificationReworkedStatus.VALID);
                        hasReworkedSteps = true;
                    }
                }
            }

            // if statusHistory contains reworked steps, mark it as VALID (except for ACCEPTED, VIEWED, DELIVERING and EFFECTIVE_DATE when duplication must be avoided)
            if (hasReworkedSteps &&
                    statusHistory.getStatus() != BffNotificationStatus.ACCEPTED &&
                    statusHistory.getStatus() != BffNotificationStatus.VIEWED &&
                    statusHistory.getStatus() != BffNotificationStatus.DELIVERING &&
                    !(statusHistory.getStatus() == BffNotificationStatus.EFFECTIVE_DATE && firstRefinementNotInvalidated)) {
                statusHistory.setReworkedStatus(BffNotificationReworkedStatus.VALID);
            }
        }
    }

    public static void insertInvalidateElementsInTimeline(BffFullNotificationV1 bffFullNotificationV1) {
        List<BffNotificationDetailTimeline> reworkedTimelineElements = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == BffTimelineCategory.NOTIFICATION_TIMELINE_REWORKED)
                .toList();

        // get the related Timeline Element from the notification status history from the event of reworked elements
        for (BffNotificationDetailTimeline timelineElement : reworkedTimelineElements) {
            for (NotificationStatusHistoryInvalidatedElement invalidateElement : timelineElement.getDetails().getInvalidatedTimelineAndStatusHistory()) {
                // add invalidated timeline elements to the main timeline
                for (TimelineElementV28 relatedTimelineElement : invalidateElement.getRelatedTimelineElements()) {
                    BffNotificationDetailTimeline bffRelatedTimelineElement = BffTimelineMapper.modelMapper.mapToBffTimeline(relatedTimelineElement);
                    bffFullNotificationV1.getTimeline().add(bffRelatedTimelineElement);
                }

                // add elementIds to the corresponding notificationStatusHistory in the correct position based on timestamp
                bffFullNotificationV1.getNotificationStatusHistory().stream()
                        .filter(statusHistory -> statusHistory.getStatus().getValue().equals(invalidateElement.getStatus().getValue()))
                        .findFirst()
                        .ifPresent(statusHistory -> {
                            for (TimelineElementV28 relatedTimelineElement : invalidateElement.getRelatedTimelineElements()) {
                                insertElementIdInCorrectPosition(
                                        statusHistory.getRelatedTimelineElements(),
                                        relatedTimelineElement,
                                        bffFullNotificationV1.getTimeline()
                                );
                            }
                        });
            }
        }

        // sort the timeline by timestamp
        bffFullNotificationV1.getTimeline().sort(Comparator.comparing(BffNotificationDetailTimeline::getTimestamp));
    }

    /**
     * Sort the notification status history by activeFrom date
     * From delivery, the statuses of the notification are sorted ascending (from the oldest to the earliest)
     * Front-end wants them ordered descending (from the earliest to the oldest) instead
     * A PARITà DI DATA MOSTRIAMO PRIMA GLI EVENTI REWORKED E POI QUELLI INVALIDATI
     *
     * @param bffFullNotificationV1 the BffFullNotificationV1 to map
     */
    public static void sortNotificationStatusHistory(BffFullNotificationV1 bffFullNotificationV1) {
        bffFullNotificationV1.getNotificationStatusHistory().sort((o1, o2) -> {
            long time1 = o1.getActiveFrom().toInstant().toEpochMilli();
            long time2 = o2.getActiveFrom().toInstant().toEpochMilli();

            if (time1 != time2) {
                return time2 > time1 ? 1 : -1;
            }

            // at equal activeFrom, NOTIFICATION_TIMELINE_REWORKED comes first
            boolean isReworked1 = o1.getStatus() == BffNotificationStatus.NOTIFICATION_TIMELINE_REWORKED;
            boolean isReworked2 = o2.getStatus() == BffNotificationStatus.NOTIFICATION_TIMELINE_REWORKED;

            if (isReworked1 && !isReworked2) {
                return -1;
            }
            if (!isReworked1 && isReworked2) {
                return 1;
            }

            // then VALID comes before NOT_VALID
            BffNotificationReworkedStatus reworkedStatus1 = o1.getReworkedStatus();
            BffNotificationReworkedStatus reworkedStatus2 = o2.getReworkedStatus();

            if (reworkedStatus1 == BffNotificationReworkedStatus.VALID && reworkedStatus2 == BffNotificationReworkedStatus.NOT_VALID) {
                return -1;
            }
            if (reworkedStatus1 == BffNotificationReworkedStatus.NOT_VALID && reworkedStatus2 == BffNotificationReworkedStatus.VALID) {
                return 1;
            }

            return 0;
        });
    }

    private static void insertElementIdInCorrectPosition(
            List<String> relatedTimelineElements,
            TimelineElementV28 elementToInsert,
            List<BffNotificationDetailTimeline> timeline
    ) {
        long timestampToInsert = elementToInsert.getTimestamp().toInstant().toEpochMilli();
        int insertPosition = 0;

        for (int i = 0; i < relatedTimelineElements.size(); i++) {
            String existingElementId = relatedTimelineElements.get(i);
            // find the timestamp of the existing element in the timeline
            long existingTimestamp = timeline.stream()
                    .filter(t -> t.getElementId().equals(existingElementId))
                    .findFirst()
                    .map(t -> t.getTimestamp().toInstant().toEpochMilli())
                    .orElse(0L);

            if (timestampToInsert >= existingTimestamp) {
                insertPosition = i + 1;
            }
        }

        relatedTimelineElements.add(insertPosition, elementToInsert.getElementId());
    }

    /**
     * Determines the deliveryMode based on the steps
     *
     * @param steps the steps to analyze
     * @return the delivery mode or null if not determined
     */
    private static BffNotificationDeliveryMode getDeliveryMode(List<BffNotificationDetailTimeline> steps) {
        if (steps == null) {
            return null;
        }

        for (BffNotificationDetailTimeline step : steps) {
            if (step.getCategory() == BffTimelineCategory.DIGITAL_SUCCESS_WORKFLOW) {
                return BffNotificationDeliveryMode.DIGITAL;
            } else if (step.getCategory() == BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER
                    || step.getCategory() == BffTimelineCategory.ANALOG_SUCCESS_WORKFLOW) {
                return BffNotificationDeliveryMode.ANALOG;
            }
        }
        return null;
    }

    /**
     * Extracts the recipient information from VIEWED steps based on delegate info
     *
     * @param steps the steps to analyze
     * @return the recipient string or null if not found
     */
    private static String getRecipientFromViewedSteps(List<BffNotificationDetailTimeline> steps) {
        if (steps == null) {
            return null;
        }

        List<BffNotificationDetailTimeline> viewedSteps = steps.stream()
                .filter(s -> s.getCategory() == BffTimelineCategory.NOTIFICATION_VIEWED)
                .toList();

        if (!viewedSteps.isEmpty()) {
            BffNotificationDetailTimeline mostOldViewedStep = viewedSteps.get(viewedSteps.size() - 1);

            if (mostOldViewedStep.getDetails() != null) {
                BffNotificationDetailTimelineDetails viewedDetails = mostOldViewedStep.getDetails();
                if (viewedDetails.getDelegateInfo() != null) {
                    return viewedDetails.getDelegateInfo().getDenomination() +
                            " (" + viewedDetails.getDelegateInfo().getTaxId() + ")";
                }
            }
        }
        return null;
    }

    /**
     * Returns the index of the recipient with a taxId valorized.
     * In multi-recipient notifications, only the requester has the taxId valorized;
     * others are anonymized.
     *
     * @param recipients the list of recipients from the notification
     * @return OptionalInt containing the index, or empty if no recipient has a taxId
     */
    public static OptionalInt findRecipientIndex(List<NotificationRecipientV24> recipients) {
        return IntStream.range(0, recipients.size())
                .filter(i -> StringUtils.hasText(recipients.get(i).getTaxId()))
                .findFirst();
    }
}