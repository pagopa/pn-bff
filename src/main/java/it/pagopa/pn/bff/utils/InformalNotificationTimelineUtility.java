package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementCategoryV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CommunicationOutcomes;

import java.util.List;

public class InformalNotificationTimelineUtility {

    /**
     * Computes the communication outcomes
     *
     * @param timeline the notification timeline
     * @return the computed communication outcomes
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
}
