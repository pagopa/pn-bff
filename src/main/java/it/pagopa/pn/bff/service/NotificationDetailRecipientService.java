package it.pagopa.pn.bff.service;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notification.NotificationReceivedMapper;
import it.pagopa.pn.bff.mappers.notification.NotificationStatusMapper;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
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
public class NotificationDetailRecipientService {

    private final PnDeliveryClientRecipientImpl pnDeliveryClient;

    /**
     * Get the detail of a notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @return the detail of the notification with a specific IUN
     */
    public Mono<BffFullNotificationV1> getNotificationDetail(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                             String xPagopaPnCxId, String iun, List<String> xPagopaPnCxGroups,
                                                             String mandateId) {
        log.info("Get notification detail for iun {} and mandateId: {}", iun, mandateId);
        Mono<FullReceivedNotificationV23> notificationDetail = pnDeliveryClient.getReceivedNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return notificationDetail.map(NotificationDetailMapper.modelMapper::mapReceivedNotificationDetail);
    }

    /**
     * Search received notifications for a recipient user.
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Receiver Type
     * @param xPagopaPnCxId   Receiver id
     * @param iunMatch        Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param mandateId       mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @param senderId        Sender id
     * @param status          Notification status
     * @param startDate       Start date
     * @param endDate         End date
     * @param subjectRegExp   Regular expression for the subject
     * @param size            Number of notifications to retrieve
     * @param nextPagesKey    Key to retrieve the next page
     * @return the list of notifications
     */
    public Mono<BffNotificationsResponseV1> searchReceivedNotification(String xPagopaPnUid,
                                                                       CxTypeAuthFleet xPagopaPnCxType,
                                                                       String xPagopaPnCxId,
                                                                       String iunMatch,
                                                                       List<String> xPagopaPnCxGroups,
                                                                       String mandateId,
                                                                       String senderId,
                                                                       NotificationStatus status,
                                                                       OffsetDateTime startDate,
                                                                       OffsetDateTime endDate,
                                                                       String subjectRegExp,
                                                                       Integer size,
                                                                       String nextPagesKey) {
        log.info("Search received notifications for user {}", xPagopaPnUid);
        return pnDeliveryClient
                .searchReceivedNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iunMatch,
                xPagopaPnCxGroups,
                mandateId,
                senderId,
                NotificationStatusMapper.notificationStatusMapper.convertDeliveryRecipientNotificationStatus(status),
                startDate,
                endDate,
                subjectRegExp,
                size,
                nextPagesKey)
                .map(NotificationReceivedMapper.modelMapper::toBffNotificationsResponseV1)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    /**
     * Search received delegated notifications for a recipient user.
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Receiver Type
     * @param xPagopaPnCxId   Receiver id
     * @param iunMatch        Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param senderId        Sender id
     * @param recipientId     Recipient id
     * @param group           Group
     * @param status          Notification status
     * @param startDate       Start date
     * @param endDate         End date
     * @param size            Number of notifications to retrieve
     * @param nextPagesKey    Key to retrieve the next page
     * @return the list of notifications
     */
    public Mono<BffNotificationsResponseV1> searchReceivedDelegatedNotification(String xPagopaPnUid,
                                                                                CxTypeAuthFleet xPagopaPnCxType,
                                                                                String xPagopaPnCxId,
                                                                                String iunMatch,
                                                                                List<String> xPagopaPnCxGroups,
                                                                                String senderId,
                                                                                String recipientId,
                                                                                String group,
                                                                                NotificationStatus status,
                                                                                OffsetDateTime startDate,
                                                                                OffsetDateTime endDate,
                                                                                Integer size,
                                                                                String nextPagesKey) {
        log.info("Search received delegated notifications for user {}", xPagopaPnUid);
        return pnDeliveryClient
                .searchReceivedDelegatedNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iunMatch,
                xPagopaPnCxGroups,
                senderId,
                recipientId,
                group,
                NotificationStatusMapper.notificationStatusMapper.convertDeliveryRecipientNotificationStatus(status),
                startDate,
                endDate,
                size,
                nextPagesKey)
                .map(NotificationReceivedMapper.modelMapper::toBffNotificationsResponseV1)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}