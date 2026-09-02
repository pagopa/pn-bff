package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTimelineMapperTest {

    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();

    private BffFullNotificationV1 mockNotificationDetail() {
        return NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(
                notificationDetailPaMock.getNotificationMultiRecipientMock(), null);
    }

    @Test
    void mapNotificationTimelineNull() {
        assertNull(NotificationTimelineMapper.modelMapper.mapNotificationTimeline(null));
    }

    @Test
    void mapNotificationTimelineData() {
        BffFullNotificationV1 detail = mockNotificationDetail();

        BffNotificationTimelineResponse timeline = NotificationTimelineMapper.modelMapper.mapNotificationTimeline(detail);

        assertEquals(detail.getIun(), timeline.getIun());
        assertEquals(detail.getSubject(), timeline.getSubject());
        assertEquals(detail.getRecipients(), timeline.getRecipients());
        assertEquals(detail.getNotificationStatusHistory().size(), timeline.getNotificationStatusHistory().size());

        for (int i = 0; i < timeline.getNotificationStatusHistory().size(); i++) {
            BffNotificationStatusHistory source = detail.getNotificationStatusHistory().get(i);
            BffNotificationTimelineStatusHistory mapped = timeline.getNotificationStatusHistory().get(i);

            assertEquals(source.getStatus(), mapped.getStatus());
            assertEquals(source.getActiveFrom(), mapped.getActiveFrom());
            assertEquals(source.getRecipient(), mapped.getViewedByMandate());
            assertNotNull(mapped.getSteps());
        }
    }

    @Test
    void mapNotificationTimelineCancellationInTimelineFalseWhenNoCancellationEvent() {
        BffFullNotificationV1 detail = new BffFullNotificationV1()
                .timeline(List.of(new BffNotificationDetailTimeline().category(BffTimelineCategory.REQUEST_ACCEPTED)));

        BffNotificationTimelineResponse timeline = NotificationTimelineMapper.modelMapper.mapNotificationTimeline(detail);

        assertFalse(timeline.getIsCancelled());
    }

    @Test
    void mapNotificationTimelineCancellationInTimelineTrueOnCancellationRequest() {
        BffFullNotificationV1 detail = new BffFullNotificationV1()
                .timeline(List.of(new BffNotificationDetailTimeline().category(BffTimelineCategory.NOTIFICATION_CANCELLATION_REQUEST)));

        BffNotificationTimelineResponse timeline = NotificationTimelineMapper.modelMapper.mapNotificationTimeline(detail);

        assertTrue(timeline.getIsCancelled());
    }

    @Test
    void mapNotificationTimelineCancellationInTimelineTrueOnNotificationCancelled() {
        BffFullNotificationV1 detail = new BffFullNotificationV1()
                .timeline(List.of(new BffNotificationDetailTimeline().category(BffTimelineCategory.NOTIFICATION_CANCELLED)));

        BffNotificationTimelineResponse timeline = NotificationTimelineMapper.modelMapper.mapNotificationTimeline(detail);

        assertTrue(timeline.getIsCancelled());
    }

    @Test
    void mapStatusHistoryViewedByMandate() {
        BffNotificationStatusHistory source = new BffNotificationStatusHistory()
                .status(BffNotificationStatus.VIEWED)
                .activeFrom(OffsetDateTime.parse("2023-08-25T10:00:00Z"))
                .recipient("TSTUTN00A07A001G")
                .steps(List.of(new BffNotificationDetailTimeline().elementId("NOTIFICATION_VIEWED")));

        BffNotificationTimelineStatusHistory mapped = NotificationTimelineMapper.modelMapper.mapStatusHistory(source);

        assertEquals(source.getStatus(), mapped.getStatus());
        assertEquals(source.getActiveFrom(), mapped.getActiveFrom());
        assertEquals("TSTUTN00A07A001G", mapped.getViewedByMandate());
        // the source steps are not copied: they are populated by NotificationTimelineUtility
        assertTrue(mapped.getSteps().isEmpty());
    }

    @Test
    void mapTimelineElementHiddenFlag() {
        BffNotificationDetailTimeline hidden = new BffNotificationDetailTimeline()
                .elementId("SEND_DIGITAL.IUN_HEUJ-UEPA-HGXT-202401-N-1.RECINDEX_0")
                .timestamp(OffsetDateTime.parse("2023-08-25T10:00:00Z"))
                .category(BffTimelineCategory.SEND_DIGITAL_DOMICILE)
                .hidden(true);

        BffNotificationTimelineEvent mapped = NotificationTimelineMapper.modelMapper.mapTimelineElement(hidden);

        assertEquals(hidden.getElementId(), mapped.getElementId());
        assertEquals(hidden.getTimestamp(), mapped.getTimestamp());
        assertEquals(hidden.getCategory(), mapped.getCategory());
        assertTrue(mapped.getIsHidden());

        // a timeline element without the hidden flag is exposed as visible
        assertFalse(NotificationTimelineMapper.modelMapper
                .mapTimelineElement(new BffNotificationDetailTimeline()).getIsHidden());
    }
}
