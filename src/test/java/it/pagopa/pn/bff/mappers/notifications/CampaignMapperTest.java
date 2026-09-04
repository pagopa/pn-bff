package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.ChannelType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import it.pagopa.pn.bff.mocks.CampaignMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CampaignMapperTest {

    private final CampaignMock campaignMock = new CampaignMock();

    @Test
    void testCampaignMapper() {
        CampaignSearchResponse campaignSearchResponse =
                campaignMock.getCampaignSearchResponseMock();

        BffCampaignSearchResponseV1 bffCampaignSearchResponse =
                CampaignMapper.modelMapper.toBffCampaignSearchResponse(
                        campaignSearchResponse
                );

        assertNotNull(bffCampaignSearchResponse);

        assertEquals(
                campaignSearchResponse.getResultsPage().size(),
                bffCampaignSearchResponse.getResultsPage().size()
        );

        for (int i = 0; i < bffCampaignSearchResponse.getResultsPage().size(); i++) {
            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getCampaignId(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getCampaignId()
            );

            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getSenderId(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getSenderId()
            );

            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getTitle(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getTitle()
            );

            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getCampaignStatus().getValue(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getCampaignStatus().getValue()
            );

            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getStartDate(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getStartDate()
            );

            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getEndDate(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getEndDate()
            );

            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getPfChannels().stream()
                            .map(ChannelType::getValue)
                            .toList(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getPfChannels().stream()
                            .map(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.ChannelType::getValue)
                            .toList()
            );

            assertEquals(
                    campaignSearchResponse.getResultsPage().get(i).getPgChannels().stream()
                            .map(ChannelType::getValue)
                            .toList(),
                    bffCampaignSearchResponse.getResultsPage().get(i).getPgChannels().stream()
                            .map(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.ChannelType::getValue)
                            .toList()
            );
        }

        assertEquals(
                campaignSearchResponse.getMoreResult(),
                bffCampaignSearchResponse.getMoreResult()
        );

        assertEquals(
                campaignSearchResponse.getNextPagesKey(),
                bffCampaignSearchResponse.getNextPagesKey()
        );

        BffCampaignSearchResponseV1 nullResponse =
                CampaignMapper.modelMapper.toBffCampaignSearchResponse(null);

        assertNull(nullResponse);
    }
}