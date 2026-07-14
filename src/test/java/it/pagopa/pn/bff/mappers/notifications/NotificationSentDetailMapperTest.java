package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV29;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import it.pagopa.pn.bff.utils.NotificationDetailUtility;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

class NotificationSentDetailMapperTest {

    @Test
    void testMapNotificationNull() {
        FullSentNotificationV29 notification = new FullSentNotificationV29();
        BffFullNotificationV1 actualMapSentNotificationDetailResult = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);
        assertNotNull(actualMapSentNotificationDetailResult);

        BffFullNotificationV1 mapSentNotificationNull = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(null);
        assertNull(mapSentNotificationNull);
    }

    @Test
    void testSentNotificationDetailMapper() {
        FullSentNotificationV29 notification = new FullSentNotificationV29();
        notification.setSenderPaId("sent-id-123");

        BffFullNotificationV1 result = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);

        assertNotNull(result);
        assertNotNull(result.getSenderPaId());
    }

    @Test
    void mapSentNotificationDetail_invokesAllAfterMappingMethodsInOrder() {
        FullSentNotificationV29 notification = new FullSentNotificationV29();
        notification.setIun("test-iun");
        notification.setTimeline(new ArrayList<>(List.of(new TimelineElementV28())));
        notification.setNotificationStatusHistory(new ArrayList<>());

        try (MockedStatic<NotificationDetailUtility> mockUtil = Mockito.mockStatic(NotificationDetailUtility.class)) {
            // Stub the only non-void method
            mockUtil.when(() -> NotificationDetailUtility.timelineElementMustBeShown(any())).thenReturn(false);

            // Act
            NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notification);

            // Assert each method is called exactly once
            mockUtil.verify(() -> NotificationDetailUtility.insertInvalidateElementsInTimeline(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.insertReworkedStatus(any(), any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.cleanRelatedTimelineElements(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.populateOtherDocuments(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.checkRADDInTimeline(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.insertCancelledStatusInTimeline(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.timelineElementMustBeShown(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.populateMacroSteps(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.setReworkedStatusOnSteps(any()), times(1));
            mockUtil.verify(() -> NotificationDetailUtility.sortNotificationStatusHistory(any()), times(1));

            // Assert methods are called in the exact order
            // (inverting any two consecutive inOrder.verify calls would make this test fail)
            InOrder inOrder = inOrder(NotificationDetailUtility.class);
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.insertInvalidateElementsInTimeline(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.insertReworkedStatus(any(), any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.cleanRelatedTimelineElements(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.populateOtherDocuments(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.checkRADDInTimeline(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.insertCancelledStatusInTimeline(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.timelineElementMustBeShown(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.populateMacroSteps(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.setReworkedStatusOnSteps(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.sortNotificationStatusHistory(any()));
        }
    }
}