package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NotificationMacroStepPopulator {

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
                timelineStep.setLegalFactsIds(List.of(new LegalFactId(
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
                NotificationDetailTimeline step = NotificationMacroStepPopulator.populateMacroStep(
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
