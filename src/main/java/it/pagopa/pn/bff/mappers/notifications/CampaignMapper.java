package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the CampaignSearchResponse
 * to the BffCampaignSearchResponseV1
 */
@Mapper
public interface CampaignMapper {
    CampaignMapper modelMapper = Mappers.getMapper(CampaignMapper.class);

    /**
     * Maps a CampaignSearchResponse to a BffCampaignSearchResponseV1
     *
     * @param campaignSearchResponse the CampaignSearchResponse to map
     * @return the mapped BffCampaignSearchResponseV1
     */
    BffCampaignSearchResponseV1 toBffCampaignSearchResponse(
            CampaignSearchResponse campaignSearchResponse
    );
}