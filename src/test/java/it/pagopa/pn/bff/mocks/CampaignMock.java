package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CampaignMock {

    public static final String SENDER_ID =
            "5b994d4a-0fa8-47ac-9c7b-354f1d44a1ce";
    public static final String CAMPAIGN_ID = "campaign-1";

    public CampaignSearchResponse getCampaignSearchResponseMock() {
        CampaignSummary campaignSummary = new CampaignSummary()
                .campaignId(CAMPAIGN_ID)
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

    public CampaignDetail getCampaignDetailMock() {
        return new CampaignDetail()
                .campaignId(CAMPAIGN_ID)
                .senderId(UUID.fromString(SENDER_ID))
                .title("First campaign")
                .descriptionScope("Description of the campaign")
                .startDate(OffsetDateTime.parse("2026-02-01T00:00:00Z"))
                .endDate(OffsetDateTime.parse("2026-12-31T23:59:59Z"))
                .campaignStatus(CampaignStatus.IN_PROGRESS)
                .senderContact("test@test.it")
                .serviceId("service-id")
                .sensitiveContent(false)
                .stopOnViewed(false)
                .taxonomyCode("taxonomy-code")
                .workflow(List.of(
                        new WorkflowEntity()
                                .channel(ChannelType.IO)
                                .recipientType(Set.of(RecipientTypeInt.PF))
                                .timeout("PT24H")
                                .desiredFeedback(Set.of(DesiredFeedbackType.READ))
                                .includeAttachment(false),
                        new WorkflowEntity()
                                .channel(ChannelType.EMAIL)
                                .recipientType(Set.of(RecipientTypeInt.PF, RecipientTypeInt.PG))
                                .timeout("PT48H")
                                .desiredFeedback(Set.of(DesiredFeedbackType.RECEIVED))
                                .includeAttachment(true)
                ))
                .serviceName("SERVICE_NAME");
    }
}