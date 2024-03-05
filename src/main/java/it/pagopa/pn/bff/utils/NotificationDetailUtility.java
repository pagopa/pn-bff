package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationDetailUtility {

    final private static List<String> AnalogFlowAllowedCodes = java.util.List.of(
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
            "RECRI004B"
    );

    final private static List<TimelineCategory> AllowedCategories =
            Arrays.asList(TimelineCategory.SEND_ANALOG_PROGRESS, TimelineCategory.SEND_ANALOG_FEEDBACK,
                    TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS);

    final private static List<TimelineCategory> TimelineAllowedStatus =
            Arrays.asList(
                    TimelineCategory.SCHEDULE_DIGITAL_WORKFLOW,
                    // PN-6902
                    TimelineCategory.ANALOG_FAILURE_WORKFLOW,
                    TimelineCategory.SEND_DIGITAL_DOMICILE,
                    TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER,
                    TimelineCategory.SEND_ANALOG_DOMICILE,
                    TimelineCategory.SEND_DIGITAL_FEEDBACK,
                    TimelineCategory.SEND_DIGITAL_PROGRESS,
                    // PN-2068
                    TimelineCategory.SEND_COURTESY_MESSAGE,
                    // PN-1647
                    TimelineCategory.NOT_HANDLED,
                    TimelineCategory.SEND_ANALOG_PROGRESS,
                    TimelineCategory.SEND_ANALOG_FEEDBACK,
                    TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                    // PN-7743
                    TimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE
            );

    /**
     * Compares two timeline elements and returns an integer representing the order between them
     *
     * @param a the first timeline element
     * @param b the second timeline element
     * @return an integer representing the order between the two elements
     */
    public static Integer fromLatestToEarliest(NotificationDetailTimeline a, NotificationDetailTimeline b) {
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
     *
     * @param step - The timeline step to check
     * @return true if the step is an internal app IO event, false otherwise
     */
    public static boolean isInternalAppIoEvent(NotificationDetailTimeline step) {
        if (step.getCategory().equals(TimelineCategory.SEND_COURTESY_MESSAGE)) {
            NotificationDetailTimelineDetails details = step.getDetails();
            return details.getDigitalAddress().getType().equals("APPIO")
                    && details.getIoSendMessageResult() != null
                    && !details.getIoSendMessageResult().equals(IoSendMessageResult.SENT_COURTESY);
        }

        return false;
    }

    /**
     * Check if the timeline element must be shown
     *
     * @param t - The timeline element to check
     * @return true if the timeline element must be shown, false otherwise
     */
    public static boolean timelineElementMustBeShown(NotificationDetailTimeline t) {
        if (AllowedCategories.contains(t.getCategory())) {
            String deliveryDetailCode = t.getDetails().getDeliveryDetailCode();
            return deliveryDetailCode != null && AnalogFlowAllowedCodes.contains(deliveryDetailCode);
        }

        return TimelineAllowedStatus.contains(t.getCategory());
    }

    /**
     * Inserts the cancelled status in the timeline
     *
     * @param bffFullNotificationV1 the notification to populate
     */
    public static void insertCancelledStatusInTimeline(BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailTimeline timelineCancelledElement = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == TimelineCategory.NOTIFICATION_CANCELLED)
                .findFirst()
                .orElse(null);

        if (timelineCancelledElement == null) {
            NotificationDetailTimeline timelineCancellationRequestElement = bffFullNotificationV1.getTimeline().stream()
                    .filter(el -> el.getCategory() == TimelineCategory.NOTIFICATION_CANCELLATION_REQUEST)
                    .findFirst()
                    .orElse(null);

            if (timelineCancellationRequestElement != null) {

                NotificationStatusHistory notificationStatusHistoryElement =
                        new NotificationStatusHistory(NotificationStatus.CANCELLATION_IN_PROGRESS,
                                timelineCancellationRequestElement.getTimestamp(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                null,
                                null
                        );

                bffFullNotificationV1.getNotificationStatusHistory().add(notificationStatusHistoryElement);
                bffFullNotificationV1.setNotificationStatus(NotificationStatus.CANCELLATION_IN_PROGRESS);
            }
        }
    }

    /**
     * Populates the other documents for the notification
     *
     * @param bffFullNotificationV1 the notification to populate
     */
    public static void populateOtherDocuments(BffFullNotificationV1 bffFullNotificationV1) {
        List<NotificationDetailDocument> otherDocuments = new ArrayList<>();

        List<NotificationDetailTimeline> timelineFiltered = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == TimelineCategory.AAR_GENERATION)
                .toList();

        if (!timelineFiltered.isEmpty()) {
            final boolean isMultiRecipient = timelineFiltered.size() > 1;

            for (NotificationDetailTimeline timeline : timelineFiltered) {
                final Integer recIndex = timeline.getDetails().getRecIndex();
                final List<NotificationRecipientV23> recipients = bffFullNotificationV1.getRecipients();
                final String recipientData = isMultiRecipient && recIndex != null
                        ? " - " + recipients.get(recIndex).getDenomination() + "(" + recipients.get(recIndex).getTaxId() + ")"
                        : "";
                final String title = "Avviso di avvenuta ricezione" + recipientData;

                NotificationDetailDocument document = new NotificationDetailDocument()
                        .recIndex(recIndex)
                        .documentId(timeline.getDetails().getGeneratedAarUrl())
                        .documentType(LegalFactType.AAR.toString())
                        .title(title)
                        .digests(
                                new NotificationAttachmentDigests()
                                        .sha256("")
                        )
                        .ref(
                                new NotificationAttachmentBodyRef()
                                        .key("")
                                        .versionToken("")
                        )
                        .contentType("");
                otherDocuments.add(document);
            }
        }

        bffFullNotificationV1.setOtherDocuments(otherDocuments);
    }

    /**
     * If timeline contains a NOTIFICATION_RADD_RETRIEVED event, it is set in the notification radd property
     *
     * @param bffFullNotificationV1 the notification to check
     */
    public static void checkRADDInTimeline(BffFullNotificationV1 bffFullNotificationV1) {
        bffFullNotificationV1.getTimeline()
                .stream()
                .filter(element -> element.getCategory() == TimelineCategory.NOTIFICATION_RADD_RETRIEVED)
                .findFirst()
                .ifPresent(bffFullNotificationV1::setRadd);
    }

    /**
     * Populates the macro step for the notification status history
     *
     * @param notificationDetail  the notification to populate
     * @param timelineElement     the timeline element to populate
     * @param status              the status to populate
     * @param acceptedStatusItems the accepted status items
     * @return the populated macro step
     */
    public static NotificationDetailTimeline populateMacroStep(
            BffFullNotificationV1 notificationDetail,
            String timelineElement,
            NotificationStatusHistory status,
            List<String> acceptedStatusItems
    ) {
        final NotificationDetailTimeline step = notificationDetail.getTimeline().stream()
                .filter(t -> t.getElementId().equals(timelineElement))
                .findFirst()
                .orElse(null);

        NotificationDetailTimeline timelineStep = new NotificationDetailTimeline();


        if (step != null) {
            BeanUtils.copyProperties(step, timelineStep);

            // hide accepted status micro steps
            if (status.getStatus().equals(NotificationStatus.ACCEPTED)) {
                timelineStep.setHidden(true);
                status.addStepsItem(timelineStep);
                // PN-4484 - hide the internal events related to the courtesy messages sent through app IO
            } else if (NotificationDetailUtility.isInternalAppIoEvent(step)) {
                timelineStep.setHidden(true);
                status.addStepsItem(timelineStep);
                // add legal facts for ANALOG_FAILURE_WORKFLOW steps with linked generatedAarUrl
                // since the AAR for such steps must be shown in the timeline exactly the same way as legalFacts.
                // Cfr. comment in the definition of INotificationDetailTimeline in src/models/NotificationDetail.ts.
            } else if (step.getCategory().equals(TimelineCategory.ANALOG_FAILURE_WORKFLOW)
                    && step.getDetails().getGeneratedAarUrl() != null) {
                timelineStep.setLegalFactsIds(java.util.List.of(new LegalFactId(
                        step.getDetails().getGeneratedAarUrl(),
                        LegalFactType.AAR
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
     * Populates the macro steps for the notification status history
     *
     * @param bffFullNotificationV1 the notification to populate
     */
    public static void populateMacroSteps(BffFullNotificationV1 bffFullNotificationV1) {
        ArrayList<String> acceptedStatusItems = new ArrayList<>();
        NotificationDeliveryMode deliveryMode = null;
        NotificationStatusHistory deliveringStatus = null;
        int lastDeliveredIndexToShift = -1;
        boolean lastDeliveredIndexToShiftIsFixed = false;
        boolean preventShiftFromDeliveredToDelivering = false;

        for (NotificationStatusHistory status : bffFullNotificationV1.getNotificationStatusHistory()) {
            if (status.getStatus().equals(NotificationStatus.DELIVERING)) {
                deliveringStatus = status;
            }

            if (status.getStatus().equals(NotificationStatus.ACCEPTED) && !status.getRelatedTimelineElements().isEmpty()) {
                acceptedStatusItems = new ArrayList<>(status.getRelatedTimelineElements());
            } else if (!acceptedStatusItems.isEmpty()) {
                status.getRelatedTimelineElements().addAll(0, acceptedStatusItems);
            }

            status.setSteps(new ArrayList<>());

            for (int ix = 0; ix < status.getRelatedTimelineElements().size(); ix++) {
                String timelineElement = status.getRelatedTimelineElements().get(ix);
                NotificationDetailTimeline step = populateMacroStep(
                        bffFullNotificationV1,
                        timelineElement,
                        status,
                        acceptedStatusItems
                );

                if (step != null) {
                    if (deliveryMode == null && step.getCategory().equals(TimelineCategory.DIGITAL_SUCCESS_WORKFLOW)) {
                        deliveryMode = NotificationDeliveryMode.DIGITAL;
                    } else if (deliveryMode == null && (step.getCategory().equals(TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER)
                            || step.getCategory().equals(TimelineCategory.ANALOG_SUCCESS_WORKFLOW))) {
                        deliveryMode = NotificationDeliveryMode.ANALOG;
                    }

                    if (status.getStatus().equals(NotificationStatus.DELIVERED) && !preventShiftFromDeliveredToDelivering) {
                        if ((step.getCategory().equals(TimelineCategory.DIGITAL_FAILURE_WORKFLOW)
                                || step.getCategory().equals(TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER)
                                || step.getCategory().equals(TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS))
                                && !lastDeliveredIndexToShiftIsFixed) {
                            lastDeliveredIndexToShift = ix;
                        } else if (step.getCategory().equals(TimelineCategory.DIGITAL_SUCCESS_WORKFLOW)) {
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

            if (status.getStatus().equals(NotificationStatus.DELIVERED)
                    && deliveringStatus != null
                    && deliveringStatus.getSteps() != null
                    && !preventShiftFromDeliveredToDelivering
                    && lastDeliveredIndexToShift > -1) {

                List<NotificationDetailTimeline> stepsToShift = new ArrayList<>(status.getSteps().subList(0, lastDeliveredIndexToShift + 1));
                stepsToShift.sort(NotificationDetailUtility::fromLatestToEarliest);
                deliveringStatus.getSteps().addAll(0, stepsToShift);
                status.setSteps(new ArrayList<>(status.getSteps().subList(lastDeliveredIndexToShift + 1, status.getSteps().size())));
                status.setActiveFrom(deliveringStatus.getSteps().get(0).getTimestamp());
            }

            status.getSteps().sort(NotificationDetailUtility::fromLatestToEarliest);

            if (!status.getStatus().equals(NotificationStatus.ACCEPTED) && !acceptedStatusItems.isEmpty()) {
                acceptedStatusItems = new ArrayList<>();
            }

            if (status.getStatus().equals(NotificationStatus.DELIVERED) && deliveryMode != null) {
                status.setDeliveryMode(deliveryMode);
            }

            if (status.getStatus().equals(NotificationStatus.VIEWED)) {
                List<NotificationDetailTimeline> viewedSteps = status.getSteps().stream()
                        .filter(s -> s.getCategory().equals(TimelineCategory.NOTIFICATION_VIEWED))
                        .toList();

                if (!viewedSteps.isEmpty()) {
                    NotificationDetailTimeline mostOldViewedStep = viewedSteps.get(viewedSteps.size() - 1);

                    if (mostOldViewedStep.getDetails() != null) {
                        NotificationDetailTimelineDetails viewedDetails = mostOldViewedStep.getDetails();
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
