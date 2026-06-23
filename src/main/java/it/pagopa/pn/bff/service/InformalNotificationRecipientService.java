package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationReceivedMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InformalNotificationRecipientService {

    private final PnDeliveryClientRecipientImpl pnDeliveryClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get the detail of an informal notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iun               Informal Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @return the detail of the informal notification with a specific IUN
     */
    public Mono<BffFullInformalNotificationV1> getInformalNotificationDetail(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails) {
        log.info("Get informal notification detail - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<FullReceivedInformalNotificationV1> informalNotification = pnDeliveryClient.getReceivedInformalNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return informalNotification.map(InformalNotificationReceivedMapper.modelMapper::mapReceivedInformalNotificationDetail);
    }
}
