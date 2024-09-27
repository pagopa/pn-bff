package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV24;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementCategoryV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementV24;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationSentDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.utils.helpers.ArrayHelpers;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDetailUtilityTest {
    NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();

    @Test
    void cleanRelatedTimelineElementsTest() {
        FullSentNotificationV24 notificationDTO = notificationDetailPaMock.getOneRecipientNotification();
        // copy status history and timeline
        BffFullNotificationV1 bffNotificationDTO = new BffFullNotificationV1();
        List<BffNotificationDetailTimeline> timeline = new ArrayList<>();
        for (TimelineElementV24 timelineElement : notificationDTO.getTimeline()) {
            BffNotificationDetailTimeline bffTimelineElem = new BffNotificationDetailTimeline();
            BeanUtils.copyProperties(timelineElement, bffTimelineElem);
            timeline.add(bffTimelineElem);
        }
        List<BffNotificationStatusHistory> notificationHistory = new ArrayList<>();
        for (NotificationStatusHistoryElement historyElement : notificationDTO.getNotificationStatusHistory()) {
            BffNotificationStatusHistory bffHistoryElement = new BffNotificationStatusHistory();
            BeanUtils.copyProperties(historyElement, bffHistoryElement);
            notificationHistory.add(bffHistoryElement);
        }
        bffNotificationDTO.setTimeline(timeline);
        bffNotificationDTO.setNotificationStatusHistory(notificationHistory);
        // clean related timeline elements
        NotificationDetailUtility.cleanRelatedTimelineElements(bffNotificationDTO);
        // check the result
        for (int i = 0; i < notificationDTO.getNotificationStatusHistory().size(); i++) {
            NotificationStatusHistoryElement notificationStatusHistory = notificationDTO.getNotificationStatusHistory().get(i);
            BffNotificationStatusHistory bffNotificationStatusHistory = bffNotificationDTO.getNotificationStatusHistory().get(i);
            for (int j = 0; j < notificationStatusHistory.getRelatedTimelineElements().size(); j++) {
                String relatedTimelineElement = notificationStatusHistory.getRelatedTimelineElements().get(j);
                TimelineElementV24 timelineElement = notificationDTO.getTimeline()
                        .stream()
                        .filter(el -> el.getElementId().equals(relatedTimelineElement))
                        .findFirst()
                        .orElse(null);
                if (timelineElement != null) {
                    assertTrue(bffNotificationStatusHistory.getRelatedTimelineElements().contains(relatedTimelineElement));
                } else {
                    assertFalse(bffNotificationStatusHistory.getRelatedTimelineElements().contains(relatedTimelineElement));
                }
            }
        }
    }

    @Test
    void populateOtherDocumentsTest() {
        FullSentNotificationV24 notificationDTOMultiRecipient = notificationDetailPaMock.getNotificationMultiRecipientMock();
        FullSentNotificationV24 notificationDTO = notificationDetailPaMock.getOneRecipientNotification();

        FullSentNotificationV24 noAARNotification = new FullSentNotificationV24();
        BeanUtils.copyProperties(notificationDTOMultiRecipient, noAARNotification);
        noAARNotification.setTimeline(noAARNotification.getTimeline().stream().filter(
                t -> t.getCategory() != TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toList()));

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(noAARNotification);

        assertTrue(calculatedParsedNotification.getOtherDocuments().isEmpty());

        ArrayList<TimelineElementV24> AARTimelineElements = notificationDTO.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);

        assertEquals(1, calculatedParsedNotification.getOtherDocuments().size());

        assertEquals(new BffNotificationDetailDocument()
                        .recIndex(0)
                        .documentId(AARTimelineElements.get(0).getDetails().getGeneratedAarUrl())
                        .documentType("AAR")
                        .title("Avviso di avvenuta ricezione")
                        .digests(new NotificationAttachmentDigests().sha256(""))
                        .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                        .contentType(""),
                calculatedParsedNotification.getOtherDocuments().get(0)
        );

        // MULTI RECIPIENT
        ArrayList<TimelineElementV24> AARTimelineElementsMultiRecipient = notificationDTOMultiRecipient.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTOMultiRecipient);

        assertEquals(calculatedParsedNotification.getOtherDocuments().size(), AARTimelineElementsMultiRecipient.size());

        int index = 0;
        for (TimelineElementV24 element : AARTimelineElementsMultiRecipient) {
            assertEquals(new BffNotificationDetailDocument()
                            .recIndex(element.getDetails().getRecIndex())
                            .documentId(element.getDetails().getGeneratedAarUrl())
                            .documentType("AAR")
                            .title("Avviso di avvenuta ricezione - " +
                                    notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getDenomination() +
                                    "(" + notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getTaxId() + ")"
                            )
                            .digests(new NotificationAttachmentDigests().sha256(""))
                            .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                            .contentType(""),
                    calculatedParsedNotification.getOtherDocuments().get(index)
            );
            index++;
        }
    }

    @Test
    void checkRADDInTimeline() {
        BffNotificationDetailTimeline raddFromTimeline = notificationDetailPaMock.notificationToFERADD().getRadd();

        assertEquals(new BffNotificationDetailTimeline()
                        .elementId("NOTIFICATION_RADD_RETRIEVED_mock")
                        .timestamp(OffsetDateTime.parse("2022-06-21T11:44:28Z"))
                        .legalFactsIds(List.of())
                        .category(BffTimelineCategory.NOTIFICATION_RADD_RETRIEVED)
                        .details(
                                new BffNotificationDetailTimelineDetails()
                                        .recIndex(1)
                                        .eventTimestamp(OffsetDateTime.parse("2022-06-21T11:44:28Z"))
                                        .raddType("ALT")
                                        .raddTransactionId("6")
                        )
                        .index(0)
                        .hidden(true),
                raddFromTimeline
        );
    }

    @Test
    void insertCancelledStatusInTimeline() {
        FullSentNotificationV24 cancellationInProgressNotification = new FullSentNotificationV24();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), cancellationInProgressNotification);

        cancellationInProgressNotification.getTimeline().add(
                notificationDetailPaMock.getTimelineElem(
                        TimelineElementCategoryV23.NOTIFICATION_CANCELLATION_REQUEST,
                        null
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper
                .mapSentNotificationDetail(cancellationInProgressNotification);

        BffNotificationStatusHistory cancellationInProgressStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(BffNotificationStatus.CANCELLATION_IN_PROGRESS))
                        .findFirst()
                        .orElse(null);

        assertNotNull(cancellationInProgressStatusHistory);

        assertEquals(BffNotificationStatus.CANCELLATION_IN_PROGRESS, calculatedParsedNotification.getNotificationStatus());
    }

    @Test
    void setTimelineHidden() {
        TimelineElementV24 sendAnalogProgress = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23().recIndex(0)
        );

        TimelineElementV24 sendAnalogFeedback = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("RECAG003C")
        );

        TimelineElementV24 sendAnalogRegisteredLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("NTINCLCD")
        );

        FullSentNotificationV24 analogNotification = new FullSentNotificationV24();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), analogNotification);

        analogNotification.getTimeline().addAll(
                List.of(
                        sendAnalogProgress,
                        sendAnalogFeedback,
                        sendAnalogRegisteredLetter
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(analogNotification);

        BffNotificationDetailTimeline sendAnalogProgressElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffTimelineCategory.SEND_ANALOG_PROGRESS))
                .findFirst()
                .orElseThrow();

        assertTrue(sendAnalogProgressElem.getHidden());

        BffNotificationDetailTimeline sendAnalogFeedbackElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffTimelineCategory.SEND_ANALOG_FEEDBACK))
                .findFirst()
                .orElseThrow();

        assertFalse(sendAnalogFeedbackElem.getHidden());

        BffNotificationDetailTimeline sendAnalogRegisteredLetterElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffTimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS))
                .findFirst()
                .orElseThrow();
        assertTrue(sendAnalogRegisteredLetterElem.getHidden());
    }

    @Test
    void populateLegalFactsOfAnalogFailureStep() {
        TimelineElementV24 analogFailure = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.ANALOG_FAILURE_WORKFLOW,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .generatedAarUrl("https://www.aar.com")
        );

        FullSentNotificationV24 analogFailureNotification = new FullSentNotificationV24();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), analogFailureNotification);
        analogFailureNotification.getTimeline().add(analogFailure);

        NotificationStatusHistoryElement deliveredStatus = analogFailureNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) ->
                        status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatus.DELIVERED)
                )
                .findFirst()
                .orElseThrow();

        deliveredStatus.getRelatedTimelineElements().add(analogFailure.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(analogFailureNotification);

        BffNotificationStatusHistory bffDeliveredStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        BffNotificationDetailTimeline analogFailureStep = bffDeliveredStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(analogFailure.getElementId()))
                .findFirst()
                .orElseThrow();

        assertEquals(
                analogFailureStep.getLegalFactsIds(),
                Collections.singletonList(
                        new BffLegalFactId()
                                .key("https://www.aar.com")
                                .category(BffLegalFactType.AAR)
                )
        );
    }

    @Test
    void deliveryModeDigital() {
        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(
                notificationDetailPaMock.getOneRecipientNotification()
        );

        BffNotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        assertEquals(deliveredStep.getDeliveryMode(), BffNotificationDeliveryMode.DIGITAL);
    }

    @Test
    void deliveryModeAnalog() {
        FullSentNotificationV24 notificationDTO = new FullSentNotificationV24();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);

        TimelineElementV24 digitalSuccess = new TimelineElementV24();

        digitalSuccess = notificationDTO.getTimeline()
                .stream()
                .filter((timelineElem) ->
                        String.valueOf(timelineElem.getCategory())
                                .equals(String.valueOf(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)))
                .findFirst()
                .orElseThrow();

        digitalSuccess.setCategory(TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER);

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);
        BffNotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        assertEquals(deliveredStep.getDeliveryMode(), BffNotificationDeliveryMode.ANALOG);
    }

    @Test
    void deliveryModeNotAssigned() {
        FullSentNotificationV24 notificationDTO = new FullSentNotificationV24();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        notificationDTO.setTimeline(
                notificationDTO.getTimeline()
                        .stream()
                        .filter((timelineElem) ->
                                !String.valueOf(timelineElem.getCategory())
                                        .equals(String.valueOf(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)))
                        .toList()
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);
        BffNotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        assertNull(deliveredStep.getDeliveryMode());
    }

    @Test
    void checkFillingOfMacroSteps() {
        FullSentNotificationV24 notificationDTO = new FullSentNotificationV24();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);

        boolean previousStepIsAccepted = false;
        ArrayList<String> acceptedItems = new ArrayList<>();

        // clean timeline related items
        for (NotificationStatusHistoryElement notificationStatusHistory : notificationDTO.getNotificationStatusHistory()) {
            List<String> cleanedRelatedTimelineElements = new ArrayList<>();
            for (String relatedTimelineElement : notificationStatusHistory.getRelatedTimelineElements()) {
                notificationDTO.getTimeline()
                        .stream()
                        .filter(elem -> elem.getElementId().equals(relatedTimelineElement))
                        .findFirst()
                        .ifPresent(timelineElem -> cleanedRelatedTimelineElements.add(relatedTimelineElement));
            }
            notificationStatusHistory.setRelatedTimelineElements(cleanedRelatedTimelineElements);
        }

        for (BffNotificationStatusHistory status : ArrayHelpers.reverseArray(calculatedParsedNotification.getNotificationStatusHistory())) {
            if (String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.ACCEPTED))) {
                previousStepIsAccepted = true;
                acceptedItems = notificationDTO.getNotificationStatusHistory()
                        .stream()
                        .filter((statusHistory) -> statusHistory.getStatus()
                                .equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatus.ACCEPTED)
                        ).findFirst()
                        .orElseThrow()
                        .getRelatedTimelineElements()
                        .stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                for (BffNotificationDetailTimeline step : status.getSteps()) {
                    assertTrue(step.getHidden());
                    assertTrue(step.getLegalFactsIds().isEmpty());
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

                assertEquals(
                        status.getRelatedTimelineElements(),
                        Arrays.stream(ArrayUtils.addAll(acceptedItems.toArray(), currentItems.toArray()))
                                .toList()
                );
            }

            OffsetDateTime previousStepTimestamp = null;
            for (BffNotificationDetailTimeline step : status.getSteps()) {
                BffNotificationDetailTimeline timelineStep = calculatedParsedNotification.getTimeline()
                        .stream()
                        .filter((timelineElem) -> timelineElem.getElementId().equals(step.getElementId()))
                        .findFirst()
                        .orElse(null);
                assertEquals(timelineStep, step);

                if (previousStepTimestamp != null) {
                    assertTrue(step.getTimestamp().isBefore(previousStepTimestamp));
                }

                previousStepTimestamp = step.getTimestamp();
            }
        }
    }

    @Test
    void hideAppIOEvent() {
        TimelineElementV24 sendCourtesy = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_COURTESY_MESSAGE,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .digitalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.DigitalAddress().type("APPIO").address(""))
                        .ioSendMessageResult(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.IoSendMessageResult.SENT_OPTIN)
        );

        FullSentNotificationV24 ioNotification = new FullSentNotificationV24();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), ioNotification);
        ioNotification.getTimeline().add(sendCourtesy);

        NotificationStatusHistoryElement acceptedStatus = ioNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatus.ACCEPTED))
                .findFirst()
                .orElseThrow();

        acceptedStatus.getRelatedTimelineElements().add(sendCourtesy.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(ioNotification);

        BffNotificationDetailTimeline ioStep;

        for (BffNotificationStatusHistory status : calculatedParsedNotification.getNotificationStatusHistory()) {
            ioStep = status.getSteps()
                    .stream()
                    .filter((st) -> st.getElementId().equals(sendCourtesy.getElementId()))
                    .findFirst()
                    .orElse(null);

            assertTrue(ioStep == null || ioStep.getHidden());
        }
    }

    @Test
    void shiftStepsFromDeliveredToDelivering() {
        FullSentNotificationV24 notificationDTO = new FullSentNotificationV24();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        TimelineElementV24 digitalFailure = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.DIGITAL_FAILURE_WORKFLOW,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23()
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


        TimelineElementV24 prepareLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.PREPARE_SIMPLE_REGISTERED_LETTER,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PhysicalAddress()
                                .address("Via Rosas 1829")
                                .zip("98036")
                                .municipality("Graniti")
                        )
        );

        TimelineElementV24 sendLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PhysicalAddress()
                                .address("Via Rosas 1829")
                                .zip("98036")
                                .municipality("Graniti")
                        )
        );

        NotificationStatusHistoryElement prevDeliveredStatus = notificationDTO.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
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

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);

        BffNotificationStatusHistory deliveredStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        BffNotificationStatusHistory deliveringStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERING)))
                .findFirst()
                .orElseThrow();

        assertEquals(deliveredStatus.getSteps().size(), deliveredCount - digitalSuccessElIndex - 3);

        BffNotificationDetailTimeline prepareLetterEl = deliveringStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(prepareLetter.getElementId()))
                .findFirst()
                .orElseThrow();

        BffNotificationDetailTimeline sendLetterEl = deliveringStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(sendLetter.getElementId()))
                .findFirst()
                .orElseThrow();

        BffNotificationDetailTimeline digitalFailureEl = deliveringStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(digitalFailure.getElementId()))
                .findFirst()
                .orElseThrow();

        assertNotNull(prepareLetterEl);
        assertNotNull(sendLetterEl);
        assertNotNull(digitalFailureEl);
    }

    @Test
    void viewedByRecipient() {
        FullSentNotificationV24 viewedNotification = new FullSentNotificationV24();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), viewedNotification);

        TimelineElementV24 viewedTimelineElement = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.NOTIFICATION_VIEWED,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
        );
        // add viewed timeline element
        viewedNotification.getTimeline().add(viewedTimelineElement);
        // add viewed status
        NotificationStatusHistoryElement viewedStatus = new NotificationStatusHistoryElement();
        viewedStatus.setStatus(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatus.VIEWED);
        viewedStatus.setActiveFrom(viewedTimelineElement.getTimestamp());
        List<String> relatedViewedElement = new ArrayList<>();
        relatedViewedElement.add(viewedTimelineElement.getElementId());
        viewedStatus.setRelatedTimelineElements(relatedViewedElement);

        viewedNotification.addNotificationStatusHistoryItem(viewedStatus);

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper
                .mapSentNotificationDetail(viewedNotification);

        BffNotificationStatusHistory viewedStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(BffNotificationStatus.VIEWED))
                        .findFirst()
                        .orElse(null);

        assertNotNull(viewedStatusHistory);
        assertEquals(viewedStatusHistory.getSteps().size(), 1);
        assertNull(viewedStatusHistory.getRecipient());
    }

    @Test
    void viewedByDelegate() {
        FullSentNotificationV24 viewedNotification = new FullSentNotificationV24();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), viewedNotification);

        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.DelegateInfo delegate =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.DelegateInfo()
                        .taxId("GLLGLL64B15G702I")
                        .denomination("Galileo Galilei")
                        .delegateType(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.RecipientType.PF);

        TimelineElementV24 viewedTimelineElement = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.NOTIFICATION_VIEWED,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .delegateInfo(delegate)
        );
        // add viewed timeline element
        viewedNotification.getTimeline().add(viewedTimelineElement);
        // add viewed status
        NotificationStatusHistoryElement viewedStatus = new NotificationStatusHistoryElement();
        viewedStatus.setStatus(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatus.VIEWED);
        viewedStatus.setActiveFrom(viewedTimelineElement.getTimestamp());
        List<String> relatedViewedElement = new ArrayList<>();
        relatedViewedElement.add(viewedTimelineElement.getElementId());
        viewedStatus.setRelatedTimelineElements(relatedViewedElement);

        viewedNotification.addNotificationStatusHistoryItem(viewedStatus);

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper
                .mapSentNotificationDetail(viewedNotification);

        BffNotificationStatusHistory viewedStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(BffNotificationStatus.VIEWED))
                        .findFirst()
                        .orElse(null);

        assertNotNull(viewedStatusHistory);
        assertEquals(viewedStatusHistory.getSteps().size(), 1);
        assertEquals(viewedStatusHistory.getRecipient(), delegate.getDenomination() + " (" + delegate.getTaxId() + ')');
    }
}