package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSummary;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.ChannelType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class CampaignMock {

    public static final String SENDER_ID =
            "5b994d4a-0fa8-47ac-9c7b-354f1d44a1ce";

    public CampaignSearchResponse getCampaignSearchResponseMock() {
        CampaignSummary campaignSummary = new CampaignSummary()
                .campaignId("campaign-1")
                .senderId(UUID.fromString(SENDER_ID))
                .title("First campaign")
                .pfChannels(List.of(ChannelType.IO, ChannelType.SMS))
                .pgChannels(List.of(ChannelType.PEC, ChannelType.ANALOG))
                .campaignStatus(CampaignStatus.IN_PROGRESS)
                .startDate(OffsetDateTime.parse("2026-02-01T00:00:00Z"))
                .endDate(OffsetDateTime.parse("2026-12-31T23:59:59Z"));

        return new CampaignSearchResponse()
                .resultsPage(List.of(campaignSummary))
                .moreResult(true)
                .nextPagesKey(List.of("next-page-key"));
    }
}