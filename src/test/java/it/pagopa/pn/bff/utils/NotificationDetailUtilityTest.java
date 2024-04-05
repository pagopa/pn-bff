package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementCategoryV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.utils.helpers.ArrayHelpers;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class NotificationDetailUtilityTest {
    NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();

    @Test
    void populateOtherDocumentsTest() {
        FullSentNotificationV23 notificationDTOMultiRecipient = notificationDetailPaMock.getNotificationMultiRecipientMock();
        FullSentNotificationV23 notificationDTO = notificationDetailPaMock.getOneRecipientNotification();

        FullSentNotificationV23 noAARNotification = new FullSentNotificationV23();
        BeanUtils.copyProperties(notificationDTOMultiRecipient, noAARNotification);
        noAARNotification.setTimeline(noAARNotification.getTimeline().stream().filter(
                t -> t.getCategory() != TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toList()));

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(noAARNotification);

        Assertions.assertTrue(calculatedParsedNotification.getOtherDocuments().isEmpty());

        ArrayList<TimelineElementV23> AARTimelineElements = notificationDTO.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);

        Assertions.assertEquals(1, calculatedParsedNotification.getOtherDocuments().size());

        Assertions.assertEquals(new NotificationDetailDocument()
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
        ArrayList<TimelineElementV23> AARTimelineElementsMultiRecipient = notificationDTOMultiRecipient.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTOMultiRecipient);

        Assertions.assertEquals(calculatedParsedNotification.getOtherDocuments().size(), AARTimelineElementsMultiRecipient.size());

        int index = 0;
        for (TimelineElementV23 element : AARTimelineElementsMultiRecipient) {
            Assertions.assertEquals(new NotificationDetailDocument()
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
        NotificationDetailTimeline raddFromTimeline = notificationDetailPaMock.notificationToFERADD().getRadd();

        Assertions.assertEquals(new NotificationDetailTimeline()
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
                        .hidden(true),
                raddFromTimeline
        );
    }

    @Test
    void insertCancelledStatusInTimeline() {
        FullSentNotificationV23 cancellationInProgressNotification = new FullSentNotificationV23();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), cancellationInProgressNotification);

        cancellationInProgressNotification.getTimeline().add(
                notificationDetailPaMock.getTimelineElem(
                        TimelineElementCategoryV23.NOTIFICATION_CANCELLATION_REQUEST,
                        null
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper
                .mapSentNotificationDetail(cancellationInProgressNotification);

        NotificationStatusHistory cancellationInProgressStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(NotificationStatus.CANCELLATION_IN_PROGRESS))
                        .findFirst()
                        .orElse(null);

        Assertions.assertNotNull(cancellationInProgressStatusHistory);

        Assertions.assertEquals(NotificationStatus.CANCELLATION_IN_PROGRESS, calculatedParsedNotification.getNotificationStatus());
    }

    @Test
    void setTimelineHidden() {
        TimelineElementV23 sendAnalogProgress = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23().recIndex(0)
        );

        TimelineElementV23 sendAnalogFeedback = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("RECAG003C")
        );

        TimelineElementV23 sendAnalogRegisteredLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("NTINCLCD")
        );

        FullSentNotificationV23 analogNotification = new FullSentNotificationV23();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), analogNotification);

        analogNotification.getTimeline().addAll(
                List.of(
                        sendAnalogProgress,
                        sendAnalogFeedback,
                        sendAnalogRegisteredLetter
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(analogNotification);

        NotificationDetailTimeline sendAnalogProgressElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.TimelineCategory.SEND_ANALOG_PROGRESS))
                .findFirst()
                .orElseThrow();

        Assertions.assertTrue(sendAnalogProgressElem.getHidden());

        NotificationDetailTimeline sendAnalogFeedbackElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.TimelineCategory.SEND_ANALOG_FEEDBACK))
                .findFirst()
                .orElseThrow();

        Assertions.assertFalse(sendAnalogFeedbackElem.getHidden());

        NotificationDetailTimeline sendAnalogRegisteredLetterElem = calculatedParsedNotification.getTimeline().stream()
                .filter(t -> t.getCategory().equals(it.pagopa.pn.bff.generated.openapi.server.v1.dto.TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS))
                .findFirst()
                .orElseThrow();
        Assertions.assertTrue(sendAnalogRegisteredLetterElem.getHidden());
    }

    @Test
    void populateLegalFactsOfAnalogFailureStep() {
        TimelineElementV23 analogFailure = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.ANALOG_FAILURE_WORKFLOW,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .generatedAarUrl("https://www.aar.com")
        );

        FullSentNotificationV23 analogFailureNotification = new FullSentNotificationV23();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), analogFailureNotification);
        analogFailureNotification.getTimeline().add(analogFailure);

        NotificationStatusHistoryElement deliveredStatus = analogFailureNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) ->
                        status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.NotificationStatus.DELIVERED)
                )
                .findFirst()
                .orElseThrow();

        deliveredStatus.getRelatedTimelineElements().add(analogFailure.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(analogFailureNotification);

        NotificationStatusHistory bffDeliveredStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        NotificationDetailTimeline analogFailureStep = bffDeliveredStatus.getSteps()
                .stream()
                .filter((step) -> step.getElementId().equals(analogFailure.getElementId()))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(
                analogFailureStep.getLegalFactsIds(),
                Collections.singletonList(
                        new LegalFactId()
                                .key("https://www.aar.com")
                                .category(LegalFactType.AAR)
                )
        );
    }

    @Test
    void deliveryModeDigital() {
        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(
                notificationDetailPaMock.getOneRecipientNotification()
        );

        NotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(deliveredStep.getDeliveryMode(), NotificationDeliveryMode.DIGITAL);
    }

    @Test
    void deliveryModeAnalog() {
        FullSentNotificationV23 notificationDTO = new FullSentNotificationV23();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);

        TimelineElementV23 digitalSuccess = new TimelineElementV23();

        digitalSuccess = notificationDTO.getTimeline()
                .stream()
                .filter((timelineElem) ->
                        String.valueOf(timelineElem.getCategory())
                                .equals(String.valueOf(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)))
                .findFirst()
                .orElseThrow();

        digitalSuccess.setCategory(TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER);

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);
        NotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(deliveredStep.getDeliveryMode(), NotificationDeliveryMode.ANALOG);
    }

    @Test
    void deliveryModeNotAssigned() {
        FullSentNotificationV23 notificationDTO = new FullSentNotificationV23();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        notificationDTO.setTimeline(
                notificationDTO.getTimeline()
                        .stream()
                        .filter((timelineElem) ->
                                !String.valueOf(timelineElem.getCategory())
                                        .equals(String.valueOf(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)))
                        .toList()
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);
        NotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        Assertions.assertNull(deliveredStep.getDeliveryMode());
    }

    @Test
    void checkFillingOfMacroSteps() {
        FullSentNotificationV23 notificationDTO = new FullSentNotificationV23();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);

        boolean previousStepIsAccepted = false;
        ArrayList<String> acceptedItems = new ArrayList<>();

        for (NotificationStatusHistory status : ArrayHelpers.reverseArray(calculatedParsedNotification.getNotificationStatusHistory())) {
            if (String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.ACCEPTED))) {
                previousStepIsAccepted = true;
                acceptedItems = notificationDTO.getNotificationStatusHistory()
                        .stream()
                        .filter((statusHistory) -> statusHistory.getStatus()
                                .equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.NotificationStatus.ACCEPTED)
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
                Assertions.assertEquals(timelineStep, step);

                if (previousStepTimestamp != null) {
                    Assertions.assertTrue(step.getTimestamp().isBefore(previousStepTimestamp));
                }

                previousStepTimestamp = step.getTimestamp();
            }
        }
    }

    @Test
    void hideAppIOEvent() {
        TimelineElementV23 sendCourtesy = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_COURTESY_MESSAGE,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .digitalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.DigitalAddress().type("APPIO").address(""))
                        .ioSendMessageResult(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.IoSendMessageResult.SENT_OPTIN)
        );

        FullSentNotificationV23 ioNotification = new FullSentNotificationV23();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), ioNotification);
        ioNotification.getTimeline().add(sendCourtesy);

        NotificationStatusHistoryElement acceptedStatus = ioNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.NotificationStatus.ACCEPTED))
                .findFirst()
                .orElseThrow();

        acceptedStatus.getRelatedTimelineElements().add(sendCourtesy.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(ioNotification);

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
        FullSentNotificationV23 notificationDTO = new FullSentNotificationV23();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        TimelineElementV23 digitalFailure = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.DIGITAL_FAILURE_WORKFLOW,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23()
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


        TimelineElementV23 prepareLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.PREPARE_SIMPLE_REGISTERED_LETTER,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.PhysicalAddress()
                                .address("Via Rosas 1829")
                                .zip("98036")
                                .municipality("Graniti")
                        )
        );

        TimelineElementV23 sendLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.PhysicalAddress()
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

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO);

        NotificationStatusHistory deliveredStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        NotificationStatusHistory deliveringStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERING)))
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

    @Test
    void viewedByRecipient() {
        FullSentNotificationV23 viewedNotification = new FullSentNotificationV23();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), viewedNotification);

        TimelineElementV23 viewedTimelineElement = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.NOTIFICATION_VIEWED,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
        );
        // add viewed timeline element
        viewedNotification.getTimeline().add(viewedTimelineElement);
        // add viewed status
        NotificationStatusHistoryElement viewedStatus = new NotificationStatusHistoryElement();
        viewedStatus.setStatus(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.NotificationStatus.VIEWED);
        viewedStatus.setActiveFrom(viewedTimelineElement.getTimestamp());
        List<String> relatedViewedElement = new ArrayList<>();
        relatedViewedElement.add(viewedTimelineElement.getElementId());
        viewedStatus.setRelatedTimelineElements(relatedViewedElement);

        viewedNotification.addNotificationStatusHistoryItem(viewedStatus);

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper
                .mapSentNotificationDetail(viewedNotification);

        NotificationStatusHistory viewedStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(NotificationStatus.VIEWED))
                        .findFirst()
                        .orElse(null);

        Assertions.assertNotNull(viewedStatusHistory);
        Assertions.assertEquals(viewedStatusHistory.getSteps().size(), 1);
        Assertions.assertNull(viewedStatusHistory.getRecipient());
    }

    @Test
    void viewedByDelegate() {
        FullSentNotificationV23 viewedNotification = new FullSentNotificationV23();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), viewedNotification);

        it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.DelegateInfo delegate =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.DelegateInfo()
                        .taxId("GLLGLL64B15G702I")
                        .denomination("Galileo Galilei")
                        .delegateType(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.RecipientType.PF);

        TimelineElementV23 viewedTimelineElement = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV23.NOTIFICATION_VIEWED,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .delegateInfo(delegate)
        );
        // add viewed timeline element
        viewedNotification.getTimeline().add(viewedTimelineElement);
        // add viewed status
        NotificationStatusHistoryElement viewedStatus = new NotificationStatusHistoryElement();
        viewedStatus.setStatus(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.NotificationStatus.VIEWED);
        viewedStatus.setActiveFrom(viewedTimelineElement.getTimestamp());
        List<String> relatedViewedElement = new ArrayList<>();
        relatedViewedElement.add(viewedTimelineElement.getElementId());
        viewedStatus.setRelatedTimelineElements(relatedViewedElement);

        viewedNotification.addNotificationStatusHistoryItem(viewedStatus);

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper
                .mapSentNotificationDetail(viewedNotification);

        NotificationStatusHistory viewedStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(NotificationStatus.VIEWED))
                        .findFirst()
                        .orElse(null);

        Assertions.assertNotNull(viewedStatusHistory);
        Assertions.assertEquals(viewedStatusHistory.getSteps().size(), 1);
        Assertions.assertEquals(viewedStatusHistory.getRecipient(), delegate.getDenomination() + " (" + delegate.getTaxId() + ')');
    }
}