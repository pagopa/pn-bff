package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalNotificationStatusHistoryElementV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementCategoryV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalNotificationTimelineItem;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalNotificationTimelineStatusHistoryV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CommunicationOutcomes;
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
            InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED
    );

    /**
     * Computes the communication outcomes by checking the presence of DELIVERED and INFORMAL_NOTIFICATION_VIEWED events
     *
     * @param timeline the notification timeline
     * @return the communication outcomes object
     */
    public static CommunicationOutcomes computeCommunicationOutcomes(
            List<it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementV1> timeline) {
        boolean delivered = false;
        boolean viewed = false;

        for (it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.InformalTimelineElementV1 element
                : CommonUtility.safeList(timeline)) {
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
     * Each status history element is resolved from relatedTimelineElements into the
     * corresponding steps, keeping only the categories visible to the frontend.
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

        for (InformalNotificationStatusHistoryElementV1 sourceStatus : CommonUtility.safeList(source.getNotificationStatusHistory())) {
            BffInformalNotificationTimelineStatusHistoryV1 mappedStatus = mapper.mapStatusHistory(sourceStatus);

            mappedStatus.setSteps(resolveSteps(sourceStatus, source.getTimeline(), mapper));

            mappedStatuses.add(mappedStatus);
        }

        Collections.reverse(mappedStatuses);

        target.setNotificationStatusHistory(mappedStatuses);
    }

    /**
     * Resolves a status history element relatedTimelineElements into the corresponding
     * timeline step
     *
     * @param status   the source status history element
     * @param timeline the notification timeline
     * @param mapper   MapStruct mapper used for event mapping
     * @return the resolved and filtered steps, most recent first
     */
    private static List<BffInformalNotificationTimelineItem> resolveSteps(
            InformalNotificationStatusHistoryElementV1 status,
            List<InformalTimelineElementV1> timeline,
            InformalNotificationTimelineMapper mapper) {

        List<BffInformalNotificationTimelineItem> steps = new ArrayList<>();

        for (String elementId : CommonUtility.safeList(status.getRelatedTimelineElements())) {
            CommonUtility.safeList(timeline).stream()
                    .filter(element -> Objects.equals(elementId, element.getElementId()))
                    .findFirst()
                    .filter(element -> VISIBLE_CATEGORIES.contains(element.getCategory()))
                    .ifPresent(element -> steps.add(mapper.mapTimelineElement(element)));
        }

        Collections.reverse(steps);

        return steps;
    }
}
