package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        NotificationDetailTimeline step = null;

        for (NotificationDetailTimeline t : notificationDetail.getTimeline()) {
            if (t.getElementId().equals(timelineElement)) {
                step = t;
                break;
            }
        }

        if (step != null) {
            // hide accepted status micro steps
            if (status.getStatus() == NotificationStatus.ACCEPTED) {
                status.getSteps().add(step.hidden(true));
                // PN-4484 - hide the internal events related to the courtesy messages sent through app IO
            } else if (NotificationDetailUtility.isInternalAppIoEvent(step)) {
                status.getSteps().add(step.hidden(true));
                // add legal facts for ANALOG_FAILURE_WORKFLOW steps with linked generatedAarUrl
                // since the AAR for such steps must be shown in the timeline exactly the same way as legalFacts.
                // Cfr. comment in the definition of INotificationDetailTimeline in src/models/NotificationDetail.ts.
            } else if (step.getCategory() == TimelineCategory.ANALOG_FAILURE_WORKFLOW
                    && (step.getDetails()).getGeneratedAarUrl() != null) {
                status.getSteps().add(step.legalFactsIds(List.of(new LegalFactId(
                        (step.getDetails()).getGeneratedAarUrl(),
                        LegalFactType.AAR
                ))));
                // remove legal facts for those microsteps that are related to the accepted status
            } else if (acceptedStatusItems.contains(step.getElementId())) {
                status.getSteps().add(step.legalFactsIds(Collections.emptyList()));
                // default case
            } else {
                status.getSteps().add(step);
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
        List<String> acceptedStatusItems = new ArrayList<>();
        NotificationDeliveryMode deliveryMode = null;
        NotificationStatusHistory deliveringStatus = null;
        int lastDeliveredIndexToShift = -1;
        boolean lastDeliveredIndexToShiftIsFixed = false;
        boolean preventShiftFromDeliveredToDelivering = false;

        for (NotificationStatusHistory status : bffFullNotificationV1.getNotificationStatusHistory()) {
            if (NotificationStatus.DELIVERING.equals(status.getStatus())) {
                deliveringStatus = status;
            }

            if (NotificationStatus.ACCEPTED.equals(status.getStatus()) && !status.getRelatedTimelineElements().isEmpty()) {
                acceptedStatusItems = status.getRelatedTimelineElements();
            } else if (!acceptedStatusItems.isEmpty()) {
                status.getRelatedTimelineElements().addAll(0, acceptedStatusItems);
            }

            status.setSteps(new ArrayList<>());

            int ix = 0;
            for (String timelineElement : status.getRelatedTimelineElements()) {
                NotificationDetailTimeline step = NotificationMacroStepPopulator.populateMacroStep(bffFullNotificationV1, timelineElement, status, acceptedStatusItems);
                if (step != null) {
                    if (deliveryMode == null && TimelineCategory.DIGITAL_SUCCESS_WORKFLOW.equals(step.getCategory())) {
                        deliveryMode = NotificationDeliveryMode.DIGITAL;
                    } else if (deliveryMode == null && (TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER.equals(step.getCategory())
                            || TimelineCategory.ANALOG_SUCCESS_WORKFLOW.equals(step.getCategory()))) {
                        deliveryMode = NotificationDeliveryMode.ANALOG;
                    }

                    if (NotificationStatus.DELIVERED.equals(status.getStatus()) && !preventShiftFromDeliveredToDelivering) {
                        if ((TimelineCategory.DIGITAL_FAILURE_WORKFLOW.equals(step.getCategory())
                                || TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER.equals(step.getCategory())
                                || TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.equals(step.getCategory()))
                                && !lastDeliveredIndexToShiftIsFixed) {
                            lastDeliveredIndexToShift = ix;
                        } else if (TimelineCategory.DIGITAL_SUCCESS_WORKFLOW.equals(step.getCategory())) {
                            if (lastDeliveredIndexToShift > -1) {
                                lastDeliveredIndexToShift = ix - 1;
                                lastDeliveredIndexToShiftIsFixed = true;
                            } else {
                                preventShiftFromDeliveredToDelivering = true;
                            }
                        }
                    }
                }
                ix++;
            }

            if (NotificationStatus.DELIVERED.equals(status.getStatus())
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

            if (!NotificationStatus.ACCEPTED.equals(status.getStatus()) && !acceptedStatusItems.isEmpty()) {
                acceptedStatusItems.clear();
            }

            if (NotificationStatus.DELIVERED.equals(status.getStatus()) && deliveryMode != null) {
                status.setDeliveryMode(deliveryMode);
            }

            if (NotificationStatus.VIEWED.equals(status.getStatus())) {
                List<NotificationDetailTimeline> viewedSteps = status.getSteps().stream()
                        .filter(s -> TimelineCategory.NOTIFICATION_VIEWED.equals(s.getCategory()))
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
