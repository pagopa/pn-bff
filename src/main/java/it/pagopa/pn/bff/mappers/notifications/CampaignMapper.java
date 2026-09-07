package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignDetail;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.WorkflowEntity;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignDetailResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.ChannelType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the Campaigns API models to the BFF API models
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

    /**
     * Maps a CampaignDetail to a BffCampaignDetailResponseV1
     *
     * @param campaignDetail the CampaignDetail to map
     * @return the mapped BffCampaignDetailResponseV1
     */
    @Mapping(source = "descriptionScope", target = "description")
    @Mapping(source = "workflow", target = "channels")
    BffCampaignDetailResponseV1 mapCampaignDetail(CampaignDetail campaignDetail);

    /**
     * Maps a WorkflowEntity to the ChannelType used in that workflow step
     *
     * @param workflowEntity the WorkflowEntity to map
     * @return the mapped ChannelType
     */
    default ChannelType mapWorkflowEntityToChannelType(WorkflowEntity workflowEntity) {
        return ChannelType.fromValue(workflowEntity.getChannel().getValue());
    }
}