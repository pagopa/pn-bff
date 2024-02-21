package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationDetailUtility {

    public static Integer fromLatestToEarliest(NotificationDetailTimeline a, NotificationDetailTimeline b) {
        long differenceInTimeline = (b.getTimestamp() != null && a.getTimestamp() != null) ? b.getTimestamp().compareTo(a.getTimestamp()) : 0;
        int differenceInIndex = (b.getIndex() != null && a.getIndex() != null) ? b.getIndex() - a.getIndex() : 0;

        if (differenceInTimeline > 0) {
            return 1;
        } else if (differenceInTimeline < 0) {
            return -1;
        } else {
            return Integer.compare(differenceInIndex, 0);
        }
    }

    public static boolean isInternalAppIoEvent(NotificationDetailTimeline step) {
        if (step.getCategory() == TimelineCategory.SEND_COURTESY_MESSAGE) {
            NotificationDetailTimelineDetails details = step.getDetails();
            return details.getDigitalAddress().getType() == DigitalDomicileType.APPIO
                    && details.getIoSendMessageResult() != null
                    && !details.getIoSendMessageResult().equals(AppIoCourtesyMessageEventType.SENT_COURTESY);
        } else {
            return false;
        }
    }

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
            } else if (isInternalAppIoEvent(step)) {
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

    public static boolean timelineElementMustBeShown(NotificationDetailTimeline t) {
        final List<TimelineCategory> AnalogFlowAllowedCodes =
                Arrays.asList(TimelineCategory.SEND_ANALOG_PROGRESS, TimelineCategory.SEND_ANALOG_FEEDBACK,
                        TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS);

        final List<TimelineCategory> TimelineAllowedStatus =
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

        if (AnalogFlowAllowedCodes.contains(t.getCategory())) {
            String deliveryDetailCode = t.getDetails().getDeliveryDetailCode();
            return deliveryDetailCode != null && AnalogFlowAllowedCodes.contains(deliveryDetailCode);
        }

        return TimelineAllowedStatus.contains(t.getCategory());
    }

}
