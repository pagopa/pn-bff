package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignDetail;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notifications.CampaignMapper;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationSentMapper;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationStatusMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InformalNotificationSenderService {

    private final PnDeliveryClientPAImpl pnDeliveryClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get the campaigns list for a Public Administration.
     *
     * @param xPagopaPnCxId Public Administration id
     * @param size          Page size
     * @param nextPagesKey  Next page key
     * @return the paginated campaigns list
     */
    public Mono<BffCampaignSearchResponseV1> getListCampaigns(
            String xPagopaPnCxId,
            Integer size,
            String nextPagesKey
    ) {
        log.info("Get campaigns list - senderId: {}", xPagopaPnCxId);

        Mono<CampaignSearchResponse> campaigns = pnDeliveryClient.listCampaigns(
                UUID.fromString(xPagopaPnCxId),
                size,
                nextPagesKey
        ).onErrorMap(
                WebClientResponseException.class,
                pnBffExceptionUtility::wrapException
        );

        return campaigns.map(
                CampaignMapper.modelMapper::toBffCampaignSearchResponse
        );
    }

    /**
     * Get the detail of a campaign
     *
     * @param campaignId    - The ID of the campaign
     * @param xPagopaPnCxId - Public Administration id
     * @return the details of the requested campaign
     */
    public Mono<BffCampaignDetailResponseV1> getCampaignDetail(
            String campaignId,
            String xPagopaPnCxId
    ) {
        log.info("Get campaign detail with ID: {}", campaignId);

        Mono<CampaignDetail> campaignDetail = pnDeliveryClient
                .getCampaignDetail(campaignId, UUID.fromString(xPagopaPnCxId))
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return campaignDetail.map(CampaignMapper.modelMapper::mapCampaignDetail);
    }

    /**
     * Search the sender's informal notifications of a campaign
     *
     * @param xPagopaPnUid      user id
     * @param xPagopaPnCxType   auth fleet cx type
     * @param xPagopaPnCxId     Public Administration id
     * @param campaignId        the ID of the campaign
     * @param startDate         search range start date
     * @param endDate           search range end date
     * @param xPagopaPnCxGroups user groups
     * @param recipientId       recipient id to filter by
     * @param iunMatch          IUN to filter by
     * @param status            informal notification status to filter by
     * @param viewed            viewed outcome to filter by
     * @param delivered         delivered outcome to filter by
     * @param size              page size
     * @param nextPagesKey      next page key
     * @return the paginated list of the campaign's sent informal notifications
     */
    public Mono<BffInformalSenderNotificationSearchResponse> searchInformalSentNotifications(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String campaignId,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            List<String> xPagopaPnCxGroups,
            String recipientId,
            String iunMatch,
            InformalNotificationStatusV1 status,
            Boolean viewed,
            Boolean delivered,
            Integer size,
            String nextPagesKey
    ) {
        log.info("Search Sent Informal Notifications: campaignId: {}, IUN: {}", campaignId, iunMatch);

        Mono<InformalNotificationSearchResponse> sentNotifications = pnDeliveryClient.searchInformalSentNotifications(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryInformalPAWebCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                campaignId,
                startDate,
                endDate,
                xPagopaPnCxGroups,
                recipientId,
                iunMatch,
                InformalNotificationStatusMapper.informalNotificationStatusMapper.convertDeliveryInformalPAWebNotificationStatus(status),
                viewed,
                delivered,
                size,
                nextPagesKey
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return sentNotifications.map(CampaignMapper.modelMapper::toBffInformalSenderNotificationSearchResponse);
    }

    /**
     * Retrieve the informal sent notification detail
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Informal Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the detail of the informal notification
     */
    public Mono<BffFullSentInformalNotificationV1> getSentInformalNotification(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String iun,
            List<String> xPagopaPnCxGroups
    ) {
        log.info("Get sent informal notification detail - senderId: {} - iun: {}", xPagopaPnCxId, iun);

        Mono<FullSentInformalNotificationV1> informalNotification = pnDeliveryClient.getSentInformalNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryInformalPAB2BCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return informalNotification.flatMap(notification ->
                pnDeliveryClient.getCampaignDetail(notification.getCampaignId(), UUID.fromString(xPagopaPnCxId))
                        .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                        .map(campaignDetail -> InformalNotificationSentMapper.modelMapper.mapSentInformalNotificationDetail(
                                notification,
                                extractCampaignChannels(campaignDetail)
                        ))
        );
    }

    /**
     * Create a list with all the channels of a campaign. It also adds SEND since it isn't returned from pn-delivery
     *
     * @param campaignDetail - The detail of the campaign
     * @return A list with all the channels of the campaign, including SEND.
     */
    private List<BffNotificationChannelType> extractCampaignChannels(CampaignDetail campaignDetail) {
        List<BffNotificationChannelType> channels = CampaignMapper.modelMapper.mapCampaignDetail(campaignDetail)
                .getChannels().stream()
                .map(channel -> BffNotificationChannelType.valueOf(channel.name()))
                .collect(Collectors.toCollection(ArrayList::new));
        channels.add(BffNotificationChannelType.SEND);
        return channels;
    }
}