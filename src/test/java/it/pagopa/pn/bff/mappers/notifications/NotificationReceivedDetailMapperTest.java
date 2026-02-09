package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV27;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementCategoryV28;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import org.junit.jupiter.api.Test;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class NotificationReceivedDetailMapperTest {

    @Test
    void shouldMapNotificationWithTimelineLogic() {
        FullReceivedNotificationV27 notification = new FullReceivedNotificationV27();
        notification.setIun("IUN-TEST-2024");

        TimelineElementV28 timelineElement = new TimelineElementV28();
        timelineElement.setCategory(TimelineElementCategoryV28.REQUEST_ACCEPTED);
        notification.setTimeline(Collections.singletonList(timelineElement));

        BffFullNotificationV1 result = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification);

        assertNotNull(result, "The result should not be null");
        assertEquals("IUN-TEST-2024", result.getIun());

        assertFalse(result.getTimeline().isEmpty(), "Timeline should not be empty");
        assertEquals(0, result.getTimeline().get(0).getIndex(), "The index of the first timeline element should be 0");
        assertNotNull(result.getTimeline().get(0).getHidden(), "The hidden property should not be null");
    }


    @Test
    void shouldReturnNullWhenInputIsNull() {
        BffFullNotificationV1 result = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(null);
        assertNull(result);
    }
}