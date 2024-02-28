package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailTimeline;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailTimelineDetails;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TimelineCategory;
import it.pagopa.pn.bff.mocks.NotificationDetailMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
class CheckRaddInTimelineTest {

    @Test
    void checkRADDInTimeline() {
        NotificationDetailTimeline raddFromTimeline = NotificationDetailMock.notificationToFERADD().getRadd();

        assert raddFromTimeline.equals(
                new NotificationDetailTimeline()
                        .elementId("NOTIFICATION_RADD_RETRIEVED_mock")
                        .timestamp(OffsetDateTime.parse("2022-06-21T11:44:28Z"))
                        .legalFactsIds(List.of())
                        .category(TimelineCategory.NOTIFICATION_RADD_RETRIEVED)
                        .details(
                                new NotificationDetailTimelineDetails()
                                        .recIndex(1)
                                        .eventTimestamp(OffsetDateTime.parse("2022-06-21T11:44:28Z"))
                                        .raddType("ALT")
                                        .raddTransactionId("6")
                        )
                        .index(0)
                        .hidden(true)
        );
    }
}