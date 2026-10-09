package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.CommunicationOutcomes;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationSearchRow;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationStatusV1;

import java.time.OffsetDateTime;
import java.util.List;

public class InformalNotificationSearchMock {

    public static final String RECIPIENT_ID = "RECIPIENT_ID";
    public static final String IUN_MATCH = "AAAA-BBBB-CCCC-000000-A-B";
    public static final int SIZE = 10;
    public static final String START_DATE = "2014-04-30T00:00:00.000Z";
    public static final String END_DATE = "2024-04-30T00:00:00.000Z";
    public static final String NEXT_PAGES_KEY = "NEXT_PAGES_KEY";
    public static final it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalNotificationStatusV1 STATUS =
            it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalNotificationStatusV1.ACCEPTED;

    public InformalNotificationSearchResponse getInformalNotificationSearchResponseMock() {
        InformalNotificationSearchRow rowOne = new InformalNotificationSearchRow()
                .iun("IUN1")
                .paProtocolNumber("Protocol Number One")
                .sender("Sender One")
                .sentAt(OffsetDateTime.parse("2024-04-29T13:54:42.563421537Z"))
                .subject("Subject One")
                .recipients(List.of("Person 1", "Person 2"))
                .group("Group One")
                .campaignId(CampaignMock.CAMPAIGN_ID)
                .notificationStatus(InformalNotificationStatusV1.ACCEPTED)
                .communicationOutcomes(new CommunicationOutcomes().viewed(true).delivered(true));

        InformalNotificationSearchRow rowTwo = new InformalNotificationSearchRow()
                .iun("IUN2")
                .paProtocolNumber("Protocol Number Two")
                .sender("Sender Two")
                .sentAt(OffsetDateTime.parse("2024-04-29T13:54:42.563421537Z"))
                .subject("Subject Two")
                .recipients(List.of("Person 3", "Person 4"))
                .group("Group Two")
                .campaignId(CampaignMock.CAMPAIGN_ID)
                .notificationStatus(InformalNotificationStatusV1.PROCESSING)
                .communicationOutcomes(new CommunicationOutcomes().viewed(false).delivered(true));

        return new InformalNotificationSearchResponse()
                .resultsPage(List.of(rowOne, rowTwo))
                .moreResult(false)
                .nextPagesKey(List.of(NEXT_PAGES_KEY));
    }
}
