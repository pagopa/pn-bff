package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementCategoryV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementDetailsV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailTimeline;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class SetTimelineHiddenTest {

    @Test
    void setTimelineHidden() {
        TimelineElementV23 sendAnalogProgress = NotificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_PROGRESS,
                new TimelineElementDetailsV23().recIndex(0)
        );

        TimelineElementV23 sendAnalogFeedback = NotificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK,
                new TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("RECAG003C")
        );

        TimelineElementV23 sendAnalogRegisteredLetter = NotificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                new TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("NTINCLCD")
        );

        FullReceivedNotificationV23 analogNotification = new FullReceivedNotificationV23();

        BeanUtils.copyProperties(NotificationDetailMock.getOneRecipientNotification(), analogNotification);

        analogNotification.getTimeline().addAll(
                List.of(
                        sendAnalogProgress,
                        sendAnalogFeedback,
                        sendAnalogRegisteredLetter
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(analogNotification);

        NotificationDetailTimeline sendAnalogProgressElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.TimelineCategory.SEND_ANALOG_PROGRESS))
                .findFirst()
                .orElseThrow();
        assert sendAnalogProgressElem.getHidden().equals(true);

        NotificationDetailTimeline sendAnalogFeedbackElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.TimelineCategory.SEND_ANALOG_FEEDBACK))
                .findFirst()
                .orElseThrow();
        assert sendAnalogFeedbackElem.getHidden().equals(false);

        NotificationDetailTimeline sendAnalogRegisteredLetterElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS))
                .findFirst()
                .orElseThrow();
        assert sendAnalogRegisteredLetterElem.getHidden().equals(true);
    }

}
