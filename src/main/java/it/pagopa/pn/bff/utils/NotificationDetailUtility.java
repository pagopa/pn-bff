package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that contains the utility functions used to transform the data from delivery to front-end
 */
public class NotificationDetailUtility {

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
                if(recIndex != null){
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
                    if (deliveryMode == null && step.getCategory().equals(BffTimelineCategory.DIGITAL_SUCCESS_WORKFLOW)) {
                        deliveryMode = BffNotificationDeliveryMode.DIGITAL;
                    } else if (deliveryMode == null && (step.getCategory().equals(BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER)
                            || step.getCategory().equals(BffTimelineCategory.ANALOG_SUCCESS_WORKFLOW))) {
                        deliveryMode = BffNotificationDeliveryMode.ANALOG;
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

            if (status.getStatus().equals(BffNotificationStatus.DELIVERED) && deliveryMode != null) {
                status.setDeliveryMode(deliveryMode);
            }

            if (status.getStatus().equals(BffNotificationStatus.VIEWED)) {
                List<BffNotificationDetailTimeline> viewedSteps = status.getSteps().stream()
                        .filter(s -> s.getCategory().equals(BffTimelineCategory.NOTIFICATION_VIEWED))
                        .toList();

                if (!viewedSteps.isEmpty()) {
                    BffNotificationDetailTimeline mostOldViewedStep = viewedSteps.get(viewedSteps.size() - 1);

                    if (mostOldViewedStep.getDetails() != null) {
                        BffNotificationDetailTimelineDetails viewedDetails = mostOldViewedStep.getDetails();
                        if (viewedDetails.getDelegateInfo() != null) {
                            String recipient = viewedDetails.getDelegateInfo().getDenomination() +
                                    " (" + viewedDetails.getDelegateInfo().getTaxId() + ")";
                            status.setRecipient(recipient);
                        }
                    }
                }
            }
        }
    }
}