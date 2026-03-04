package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV27;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import it.pagopa.pn.bff.utils.NotificationDetailUtility;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class NotificationReceivedDetailMapperTest {

    @Test
    void testMapNotificationNull() {
        BffFullNotificationV1 mapNotificationNull = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(null);
        assertNull(mapNotificationNull);
    }

    @Test
    void testReceivedNotificationDetailMapper() {
        FullReceivedNotificationV27 notification = new FullReceivedNotificationV27();
        notification.setIun("id-test-123");

        BffFullNotificationV1 result = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification);

        assertNotNull(result);
        assertNotNull(result.getIun());
    }

    @Test
    void mapReceivedNotificationDetail_invokesAllAfterMappingMethodsInOrder() {
        FullReceivedNotificationV27 notification = new FullReceivedNotificationV27();
        notification.setIun("test-iun");
        notification.setTimeline(new ArrayList<>(List.of(new TimelineElementV28())));
        notification.setNotificationStatusHistory(new ArrayList<>());

        List<String> callOrder = new ArrayList<>();

        try (MockedStatic<NotificationDetailUtility> mockUtil = Mockito.mockStatic(NotificationDetailUtility.class)) {
            mockUtil.when(() -> NotificationDetailUtility.insertInvalidateElementsInTimeline(any()))
                    .thenAnswer(inv -> { callOrder.add("insertInvalidateElementsInTimeline"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.insertReworkedStatus(any()))
                    .thenAnswer(inv -> { callOrder.add("insertReworkedStatus"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.cleanRelatedTimelineElements(any()))
                    .thenAnswer(inv -> { callOrder.add("cleanRelatedTimelineElements"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.populateOtherDocuments(any()))
                    .thenAnswer(inv -> { callOrder.add("populateOtherDocuments"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.checkRADDInTimeline(any()))
                    .thenAnswer(inv -> { callOrder.add("checkRADDInTimeline"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.insertCancelledStatusInTimeline(any()))
                    .thenAnswer(inv -> { callOrder.add("insertCancelledStatusInTimeline"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.timelineElementMustBeShown(any()))
                    .thenAnswer(inv -> { callOrder.add("setTimelineIndexAndHidden"); return false; });
            mockUtil.when(() -> NotificationDetailUtility.populateMacroSteps(any()))
                    .thenAnswer(inv -> { callOrder.add("populateMacroSteps"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.setReworkedStatusOnSteps(any()))
                    .thenAnswer(inv -> { callOrder.add("setReworkedStatusOnSteps"); return null; });
            mockUtil.when(() -> NotificationDetailUtility.sortNotificationStatusHistory(any()))
                    .thenAnswer(inv -> { callOrder.add("sortNotificationStatusHistory"); return null; });

            NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification);
        }

        assertEquals(List.of(
                "insertInvalidateElementsInTimeline",
                "insertReworkedStatus",
                "cleanRelatedTimelineElements",
                "populateOtherDocuments",
                "checkRADDInTimeline",
                "insertCancelledStatusInTimeline",
                "setTimelineIndexAndHidden",
                "populateMacroSteps",
                "setReworkedStatusOnSteps",
                "sortNotificationStatusHistory"
        ), callOrder);
    }
}

