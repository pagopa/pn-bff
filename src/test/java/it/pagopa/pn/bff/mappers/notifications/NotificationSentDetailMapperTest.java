package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV28;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import it.pagopa.pn.bff.utils.NotificationDetailUtility;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class NotificationSentDetailMapperTest {

    @Test
    void testMapNotificationNull() {
        FullSentNotificationV28 notification = new FullSentNotificationV28();
        BffFullNotificationV1 actualMapSentNotificationDetailResult = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);
        assertNotNull(actualMapSentNotificationDetailResult);

        BffFullNotificationV1 mapSentNotificationNull = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(null);
        assertNull(mapSentNotificationNull);
    }

    @Test
    void testSentNotificationDetailMapper() {
        FullSentNotificationV28 notification = new FullSentNotificationV28();
        notification.setSenderPaId("sent-id-123");

        BffFullNotificationV1 result = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);

        assertNotNull(result);
        assertNotNull(result.getSenderPaId()); // <--- Aggiungiamo questo per coerenza con l'altro test
    }

    @Test
    void mapSentNotificationDetail_invokesAllAfterMappingMethodsInOrder() {
        FullSentNotificationV28 notification = new FullSentNotificationV28();
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

            NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);
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