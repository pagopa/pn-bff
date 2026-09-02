package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.SenderInformalNotificationsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignDetailResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import it.pagopa.pn.bff.service.InformalNotificationSenderService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
     * @param campaignId - The ID of the campaign
     * @param senderId   - The ID of the sender
     * @return the details of the requested campaign
     */
    @Override
    public Mono<ResponseEntity<BffCampaignDetailResponseV1>> getCampaignDetailV1(
            String campaignId,
            UUID senderId,
            final ServerWebExchange exchange) {

        Mono<BffCampaignDetailResponseV1> campaignDetail = informalNotificationSenderService.getCampaignDetail(
                campaignId,
                senderId
        );

        return campaignDetail.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }
}