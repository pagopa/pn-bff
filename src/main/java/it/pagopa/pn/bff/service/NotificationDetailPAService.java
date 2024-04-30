package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notification.NotificationStatusMapper;
import it.pagopa.pn.bff.mappers.notification.SentNotificationMapper;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDetailPAService {

    private final PnDeliveryClientPAImpl pnDeliveryClientPA;

    /**
     * Get the detail of a notification. This is for a Public Administration user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the detail of the notification with a specific IUN
     */
    public Mono<BffFullNotificationV1> getSentNotificationDetail(String xPagopaPnUid,
                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                 String xPagopaPnCxId, String iun,
                                                                 List<String> xPagopaPnCxGroups
    ) {
        log.info("Get notification detail for iun {}", iun);
        Mono<FullSentNotificationV23> notificationDetail = pnDeliveryClientPA.getSentNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return notificationDetail.map(NotificationDetailMapper.modelMapper::mapSentNotificationDetail);
    }
    /**
     * Search the notifications sent by a Public Administration
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param iun               Notification IUN
     * @param senderId          Sender Identifier
     * @param status            Notification Status
     * @param subjectRegExp     Regular Expression for the subject
     * @param startDate         Start Date
     * @param endDate           End Date
     * @param page              Page number
     * @param pageSize          Page size
     * @return the list of notifications sent by a Public Administration
     */
    public Mono<BffNotificationsResponseV1> searchSentNotifications(String xPagopaPnUid,
                                                                     CxTypeAuthFleet xPagopaPnCxType,
                                                                     String xPagopaPnCxId,
                                                                     List<String> xPagopaPnCxGroups,
                                                                     String iun,
                                                                     String senderId,
                                                                     NotificationStatus status,
                                                                     String subjectRegExp,
                                                                     OffsetDateTime startDate,
                                                                     OffsetDateTime endDate,
                                                                     int page,
                                                                     String pageSize) {
        log.info("Search xPagopaPnUid {}", xPagopaPnUid);
        log.info("Search xPagopaPnCxType {}", xPagopaPnCxType);
        log.info("Search xPagopaPnCxId {}", xPagopaPnCxId);
        log.info("Search notification for iun {}", iun);
        log.info("Search notification for senderId {}", senderId);
        log.info("Search notification for status {}", status);
        log.info("Search notification for subjectRegExp {}", subjectRegExp);
        log.info("Search notification for startDate {}", startDate);
        log.info("Search notification for endDate {}", endDate);
        log.info("Search notification for page {}", page);
        log.info("Search notification for pageSize {}", pageSize);
        return pnDeliveryClientPA
                .searchSentNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryWebPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                startDate,
                endDate,
                xPagopaPnCxGroups,
                senderId,
                NotificationStatusMapper.notificationStatusMapper.convertDeliveryWebPANotificationStatus(status),
                subjectRegExp,
                iun,
                page,
                pageSize)
                .map(SentNotificationMapper.modelMapper::toBffNotificationsResponseV1)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}