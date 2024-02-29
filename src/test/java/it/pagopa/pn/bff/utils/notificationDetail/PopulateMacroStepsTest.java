package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailTimeline;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatusHistory;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailMock;
import it.pagopa.pn.bff.utils.helpers.ArrayHelpers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class PopulateMacroStepsTest {
    NotificationDetailMock notificationDetailMock = new NotificationDetailMock();

    @Test
    void checkFillingOfMacroSteps() {
        FullReceivedNotificationV23 notificationDTO = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), notificationDTO);
        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTO);

        boolean previousStepIsAccepted = false;
        ArrayList<String> acceptedItems = new ArrayList<>();

        for (NotificationStatusHistory status : ArrayHelpers.reverseArray(calculatedParsedNotification.getNotificationStatusHistory())) {
            if (String.valueOf(status.getStatus()).equals(String.valueOf(NotificationStatus.ACCEPTED))) {
                previousStepIsAccepted = true;
                acceptedItems = notificationDTO.getNotificationStatusHistory()
                        .stream()
                        .filter((statusHistory) -> statusHistory.getStatus()
                                .equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.ACCEPTED)
                        ).findFirst()
                        .orElseThrow()
                        .getRelatedTimelineElements()
                        .stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                for (NotificationDetailTimeline step : status.getSteps()) {
                    Assertions.assertTrue(step.getHidden());
                    Assertions.assertTrue(step.getLegalFactsIds().isEmpty());
                }
                continue;
            }

            if (previousStepIsAccepted) {
                ArrayList<String> currentItems = notificationDTO.getNotificationStatusHistory()
                        .stream()
                        .filter((statusHistory) -> String.valueOf(statusHistory.getStatus())
                                .equals(String.valueOf(status.getStatus()))
                        )
                        .findFirst()
                        .orElseThrow()
                        .getRelatedTimelineElements()
                        .stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                previousStepIsAccepted = false;

                Assertions.assertEquals(
                        status.getRelatedTimelineElements(),
                        Arrays.stream(ArrayUtils.addAll(acceptedItems.toArray(), currentItems.toArray()))
                                .toList()
                );
            }

            OffsetDateTime previousStepTimestamp = null;
            for (NotificationDetailTimeline step : status.getSteps()) {
                NotificationDetailTimeline timelineStep = calculatedParsedNotification.getTimeline()
                        .stream()
                        .filter((timelineElem) -> timelineElem.getElementId().equals(step.getElementId()))
                        .findFirst()
                        .orElse(null);
                assert step.equals(timelineStep);

                if (previousStepTimestamp != null) {
                    Assertions.assertTrue(step.getTimestamp().isBefore(previousStepTimestamp));
                }

                previousStepTimestamp = step.getTimestamp();
            }
        }
    }

    @Test
    void hideAppIOEvent() {
        TimelineElementV23 sendCourtesy = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_COURTESY_MESSAGE,
                new TimelineElementDetailsV23()
                        .recIndex(0)
                        .digitalAddress(new DigitalAddress().type("APPIO").address(""))
                        .ioSendMessageResult(IoSendMessageResult.SENT_OPTIN)
        );

        FullReceivedNotificationV23 ioNotification = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), ioNotification);
        ioNotification.getTimeline().add(sendCourtesy);

        NotificationStatusHistoryElement acceptedStatus = ioNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.ACCEPTED))
                .findFirst()
                .orElseThrow();

        acceptedStatus.getRelatedTimelineElements().add(sendCourtesy.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(ioNotification);

        NotificationDetailTimeline ioStep;

        for (NotificationStatusHistory status : calculatedParsedNotification.getNotificationStatusHistory()) {
            ioStep = status.getSteps()
                    .stream()
                    .filter((st) -> st.getElementId().equals(sendCourtesy.getElementId()))
                    .findFirst()
                    .orElse(null);

            Assertions.assertTrue(ioStep == null || ioStep.getHidden());
        }
    }

    @Test
    void shiftStepsFromDeliveredToDelivering() {
        FullReceivedNotificationV23 notificationDTO = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), notificationDTO);
        TimelineElementV23 digitalFailure = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.DIGITAL_FAILURE_WORKFLOW,
                new TimelineElementDetailsV23()
                        .recIndex(0)
        );
        int digitalSuccessIndex = -1;
        for (int i = 0; i < notificationDTO.getTimeline().size(); i++) {
            if (String.valueOf(notificationDTO.getTimeline().get(i).getCategory())
                    .equals(String.valueOf(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW))) {
                digitalSuccessIndex = i;
                break;
            }
        }


        TimelineElementV23 prepareLetter = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.PREPARE_SIMPLE_REGISTERED_LETTER,
                new TimelineElementDetailsV23()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new PhysicalAddress()
                                .address("Via Rosas 1829")
                                .zip("98036")
                                .municipality("Graniti")
                        )
        );

        TimelineElementV23 sendLetter = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER,
                new TimelineElementDetailsV23()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new PhysicalAddress()
                                .address("Via Rosas 1829")
                                .zip("98036")
                                .municipality("Graniti")
                        )
        );

        NotificationStatusHistoryElement prevDeliveredStatus = notificationDTO.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        int digitalSuccessElIndex = -1;
        for (int i = 0; i < prevDeliveredStatus.getRelatedTimelineElements().size(); i++) {
            if (String.valueOf(prevDeliveredStatus.getRelatedTimelineElements().get(i))
                    .equals(String.valueOf(notificationDTO.getTimeline().get(digitalSuccessIndex).getElementId()))) {
                digitalSuccessElIndex = i;
                break;
            }
        }

        prevDeliveredStatus.getRelatedTimelineElements().set(digitalSuccessElIndex, digitalFailure.getElementId());

        prevDeliveredStatus.getRelatedTimelineElements().add(digitalSuccessElIndex + 1, prepareLetter.getElementId());
        prevDeliveredStatus.getRelatedTimelineElements().add(digitalSuccessElIndex + 2, sendLetter.getElementId());

        int deliveredCount = prevDeliveredStatus.getRelatedTimelineElements().size();

        notificationDTO.getTimeline().set(digitalSuccessIndex, digitalFailure);
        notificationDTO.getTimeline().add(digitalSuccessIndex + 1, prepareLetter);
        notificationDTO.getTimeline().add(digitalSuccessIndex + 2, sendLetter);

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTO);

        NotificationStatusHistory deliveredStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        NotificationStatusHistory deliveringStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(NotificationStatus.DELIVERING)))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(deliveredStatus.getSteps().size(), deliveredCount - digitalSuccessElIndex - 3);

        NotificationDetailTimeline prepareLetterEl = deliveringStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(prepareLetter.getElementId()))
                .findFirst()
                .orElseThrow();

        NotificationDetailTimeline sendLetterEl = deliveringStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(sendLetter.getElementId()))
                .findFirst()
                .orElseThrow();

        NotificationDetailTimeline digitalFailureEl = deliveringStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(digitalFailure.getElementId()))
                .findFirst()
                .orElseThrow();

        Assertions.assertNotNull(prepareLetterEl);
        Assertions.assertNotNull(sendLetterEl);
        Assertions.assertNotNull(digitalFailureEl);
    }

}
