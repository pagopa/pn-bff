package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.SenderInformalNotificationsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.service.InformalNotificationSenderService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@CustomLog
@RestController
public class InformalNotificationSenderController implements SenderInformalNotificationsApi {

    private final InformalNotificationSenderService informalNotificationSenderService;

    public InformalNotificationSenderController(
            InformalNotificationSenderService informalNotificationSenderService
    ) {
        this.informalNotificationSenderService = informalNotificationSenderService;
    }

    @Override
    public Mono<ResponseEntity<BffCampaignSearchResponseV1>> getListCampaignsV1(
            String xPagopaPnCxId,
            Integer size,
            String nextPagesKey,
            final ServerWebExchange exchange
    ) {
        Mono<BffCampaignSearchResponseV1> serviceResponse =
                informalNotificationSenderService.getListCampaigns(
                        xPagopaPnCxId,
                        size,
                        nextPagesKey
                );

        return serviceResponse.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }

    /**
     * Get the detail of a campaign
     *
     * @param campaignId    - The ID of the campaign
     * @param xPagopaPnCxId - Public Administration id
     * @return the details of the requested campaign
     */
    @Override
    public Mono<ResponseEntity<BffCampaignDetailResponseV1>> getCampaignDetailV1(
            String campaignId,
            String xPagopaPnCxId,
            final ServerWebExchange exchange) {

        Mono<BffCampaignDetailResponseV1> campaignDetail = informalNotificationSenderService.getCampaignDetail(
                campaignId,
                xPagopaPnCxId
        );

        return campaignDetail.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }

    /**
     * Get the list of informal notifications of a campaign
     *
     * @param xPagopaPnUid      user id
     * @param xPagopaPnCxType   auth fleet cx type
     * @param xPagopaPnCxId     Public Administration id
     * @param campaignId        The ID of the campaign
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
    @Override
    public Mono<ResponseEntity<BffInformalSenderNotificationSearchResponse>> searchInformalSentNotificationV1(
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
            String nextPagesKey,
            final ServerWebExchange exchange
    ) {
        Mono<BffInformalSenderNotificationSearchResponse> serviceResponse =
                informalNotificationSenderService.searchInformalSentNotifications(
                        xPagopaPnUid,
                        xPagopaPnCxType,
                        xPagopaPnCxId,
                        campaignId,
                        startDate,
                        endDate,
                        xPagopaPnCxGroups,
                        recipientId,
                        iunMatch,
                        status,
                        viewed,
                        delivered,
                        size,
                        nextPagesKey
                );

        return serviceResponse.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }

    /**
     * GET /bff/v1/notifications/informal/sent/{iun}: Informal Sent Notification detail
     * Get the detail of an informal notification. This is for a Public Administration user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Informal Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the detail of the informal notification with a specific IUN
     */
    @Override
    public Mono<ResponseEntity<BffFullSentInformalNotificationV1>> getSentInformalNotificationV1(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String iun,
            List<String> xPagopaPnCxGroups,
            final ServerWebExchange exchange
    ) {
        Mono<BffFullSentInformalNotificationV1> serviceResponse =
                informalNotificationSenderService.getSentInformalNotification(
                        xPagopaPnUid,
                        xPagopaPnCxType,
                        xPagopaPnCxId,
                        iun,
                        xPagopaPnCxGroups
                );

        return serviceResponse.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }

}