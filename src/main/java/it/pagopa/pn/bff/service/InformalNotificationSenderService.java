package it.pagopa.pn.bff.service;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import it.pagopa.pn.bff.mappers.notifications.CampaignMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
     * @param size Page size
     * @param nextPagesKey Next page key
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
}