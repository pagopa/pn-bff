package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV29;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push_rework.model.ReworkItem;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV28;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Shared helper to retrieve the rework items of a notification (used to resolve the correction type
 * on the {@code NOTIFICATION_TIMELINE_REWORKED} reworkedTimelineElements). It is used by both the
 * recipient and the sender flows: the notification and timeline models come from two different
 * generated clients ({@code delivery_recipient} / {@code delivery_b2b_pa}) and share no common
 * supertype, hence the two overloads.
 */
@Component
@RequiredArgsConstructor
public class ReworkItemsService {

    private final PnDeliveryPushClientImpl pnDeliveryPushClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Retrieves the rework items of a received (recipient side) notification.
     *
     * @param notification the received notification detail
     * @return the rework items (empty if there is no correction)
     */
    public Mono<List<ReworkItem>> getReworkItems(FullReceivedNotificationV28 notification) {
        boolean hasRework = notification.getTimeline() != null && notification.getTimeline().stream()
                .anyMatch(el -> el.getCategory() == it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model
                        .TimelineElementCategoryV28.NOTIFICATION_TIMELINE_REWORKED);

        return getReworkItems(hasRework, notification.getIun());
    }

    /**
     * Retrieves the rework items of a sent (sender side) notification.
     *
     * @param notification the sent notification detail
     * @return the rework items (empty if there is no correction)
     */
    public Mono<List<ReworkItem>> getReworkItems(FullSentNotificationV29 notification) {
        boolean hasRework = notification.getTimeline() != null && notification.getTimeline().stream()
                .anyMatch(el -> el.getCategory() == it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model
                        .TimelineElementCategoryV28.NOTIFICATION_TIMELINE_REWORKED);

        return getReworkItems(hasRework, notification.getIun());
    }

    /**
     * Common part: invokes the rework API only when {@code hasRework} is {@code true}.
     */
    private Mono<List<ReworkItem>> getReworkItems(boolean hasRework, String iun) {
        if (!hasRework) {
            return Mono.just(List.of());
        }

        return pnDeliveryPushClient.getRework(iun)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                .map(response -> response.getItems() == null ? List.<ReworkItem>of() : response.getItems());
    }
}