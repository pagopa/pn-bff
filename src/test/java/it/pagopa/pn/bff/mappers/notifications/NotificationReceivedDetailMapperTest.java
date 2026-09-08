package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV28;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.utils.NotificationDetailUtility;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

class NotificationReceivedDetailMapperTest {

    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();

    @Test
    void testMapNotificationNull() {
        BffFullNotificationV1 mapNotificationNull = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(null, null);
        assertNull(mapNotificationNull);
    }

    @Test
    void testReceivedNotificationDetailMapper() {
        FullReceivedNotificationV28 notification = new FullReceivedNotificationV28();
        notification.setIun("id-test-123");

        BffFullNotificationV1 result = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification, null);

        assertNotNull(result);
        assertNotNull(result.getIun());
    }

    @Test
    void testFiledAtFromRequestAcceptedTimelineElement() {
        FullReceivedNotificationV28 notification = notificationDetailRecipientMock.getNotificationMultiRecipientMock();

        BffFullNotificationV1 result = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification, null);

        assertNotNull(result);
        assertEquals(OffsetDateTime.parse("2023-08-25T09:34:58.041398918Z"), result.getFiledAt());
    }

    @Test
    void testFiledAtNullWhenNoRequestAcceptedElement() {
        FullReceivedNotificationV28 notification = new FullReceivedNotificationV28();
        notification.setIun("test-iun");
        notification.setTimeline(new ArrayList<>(List.of(new TimelineElementV28())));
        notification.setNotificationStatusHistory(new ArrayList<>());

        BffFullNotificationV1 result = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification, null);

        assertNotNull(result);
        assertNull(result.getFiledAt());
    }

    @Test
    void mapReceivedNotificationDetail_invokesAllAfterMappingMethodsInOrder() {
        FullReceivedNotificationV28 notification = new FullReceivedNotificationV28();
        notification.setIun("test-iun");
        notification.setTimeline(new ArrayList<>(List.of(new TimelineElementV28())));
        notification.setNotificationStatusHistory(new ArrayList<>());

        try (MockedStatic<NotificationDetailUtility> mockUtil = Mockito.mockStatic(NotificationDetailUtility.class)) {
            // Stub the only non-void method
            mockUtil.when(() -> NotificationDetailUtility.timelineElementMustBeShown(any())).thenReturn(false);

            // Act
            NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notification, null);

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
            mockUtil.verify(() -> NotificationDetailUtility.setAarDocumentAvailability(any()), times(1));
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
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.setAarDocumentAvailability(any()));
            inOrder.verify(mockUtil, () -> NotificationDetailUtility.sortNotificationStatusHistory(any()));
        }
    }
}

