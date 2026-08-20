package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV29;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatusHistoryElementV26;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementCategoryV28;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationSentDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.utils.helpers.ArrayHelpers;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.bff.utils.NotificationDetailUtility.findRecipientIndex;
import static org.junit.jupiter.api.Assertions.*;

class NotificationDetailUtilityTest {
    NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();

    @Test
    void cleanRelatedTimelineElementsTest() {
        FullSentNotificationV29 notificationDTO = notificationDetailPaMock.getOneRecipientNotification();
        // copy status history and timeline
        BffFullNotificationV1 bffNotificationDTO = new BffFullNotificationV1();
        List<BffNotificationDetailTimeline> timeline = new ArrayList<>();
        for (TimelineElementV28 timelineElement : notificationDTO.getTimeline()) {
            BffNotificationDetailTimeline bffTimelineElem = new BffNotificationDetailTimeline();
            BeanUtils.copyProperties(timelineElement, bffTimelineElem);
            timeline.add(bffTimelineElem);
        }
        List<BffNotificationStatusHistory> notificationHistory = new ArrayList<>();
        for (NotificationStatusHistoryElementV26 historyElement : notificationDTO.getNotificationStatusHistory()) {
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
            NotificationStatusHistoryElementV26 notificationStatusHistory = notificationDTO.getNotificationStatusHistory().get(i);
            BffNotificationStatusHistory bffNotificationStatusHistory = bffNotificationDTO.getNotificationStatusHistory().get(i);
            for (int j = 0; j < notificationStatusHistory.getRelatedTimelineElements().size(); j++) {
                String relatedTimelineElement = notificationStatusHistory.getRelatedTimelineElements().get(j);
                TimelineElementV28 timelineElement = notificationDTO.getTimeline()
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
        FullSentNotificationV29 notificationDTOMultiRecipient = notificationDetailPaMock.getNotificationMultiRecipientMock();
        FullSentNotificationV29 notificationDTO = notificationDetailPaMock.getOneRecipientNotification();

        FullSentNotificationV29 noAARNotification = new FullSentNotificationV29();
        BeanUtils.copyProperties(notificationDTOMultiRecipient, noAARNotification);
        noAARNotification.setTimeline(noAARNotification.getTimeline().stream().filter(
                t -> t.getCategory() != TimelineElementCategoryV28.AAR_GENERATION
        ).collect(Collectors.toList()));

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(noAARNotification, null);

        assertTrue(calculatedParsedNotification.getOtherDocuments().isEmpty());

        ArrayList<TimelineElementV28> AARTimelineElements = notificationDTO.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV28.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO, null);

        assertEquals(1, calculatedParsedNotification.getOtherDocuments().size());

        assertEquals(new BffNotificationDetailDocument()
                        .recIndex(0)
                        .documentId(AARTimelineElements.get(0).getDetails().getGeneratedAarUrl())
                        .documentType("AAR")
                        .title(null)
                        .digests(new NotificationAttachmentDigests().sha256(""))
                        .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                        .contentType("")
                        .recipient(new BffDocumentRecipientData()
                                .denomination(notificationDTO.getRecipients().get(0).getDenomination())
                                .taxId(notificationDTO.getRecipients().get(0).getTaxId())
                        ),
                calculatedParsedNotification.getOtherDocuments().get(0)
        );

        // MULTI RECIPIENT
        ArrayList<TimelineElementV28> AARTimelineElementsMultiRecipient = notificationDTOMultiRecipient.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV28.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTOMultiRecipient, null);

        assertEquals(calculatedParsedNotification.getOtherDocuments().size(), AARTimelineElementsMultiRecipient.size());

        int index = 0;
        for (TimelineElementV28 element : AARTimelineElementsMultiRecipient) {
            assertEquals(new BffNotificationDetailDocument()
                            .recIndex(element.getDetails().getRecIndex())
                            .documentId(element.getDetails().getGeneratedAarUrl())
                            .documentType("AAR")
                            .title(null)
                            .digests(new NotificationAttachmentDigests().sha256(""))
                            .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                            .contentType("")
                            .recipient(new BffDocumentRecipientData()
                                    .denomination(notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getDenomination())
                                    .taxId(notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getTaxId())
                            ),
                    calculatedParsedNotification.getOtherDocuments().get(index)
            );
            index++;
        }
    }

    @Test
    void setAarDocumentAvailability_available() {
        Clock clock = Clock.fixed(Instant.parse("2036-01-01T09:59:59Z"), ZoneOffset.UTC);

        BffFullNotificationV1 notification = buildNotificationForAarAvailability(
                List.of(buildEffectiveDate("2026-01-01T10:00:00Z", null))
        );

        NotificationDetailUtility.setAarDocumentAvailability(notification, clock);

        assertTrue(notification.getAarDocumentAvailable());
    }

    @Test
    void setAarDocumentAvailability_notAvailable() {
        Clock clock = Clock.fixed(Instant.parse("2036-01-01T10:00:00Z"), ZoneOffset.UTC);

        BffFullNotificationV1 notification = buildNotificationForAarAvailability(
                List.of(buildEffectiveDate("2026-01-01T10:00:00Z", null))
        );

        NotificationDetailUtility.setAarDocumentAvailability(notification, clock);

        assertFalse(notification.getAarDocumentAvailable());
    }

    @Test
    void setAarDocumentAvailability_usesValidEffectiveDateAfterRework() {
        Clock clock = Clock.fixed(Instant.parse("2036-06-22T10:00:00Z"), ZoneOffset.UTC);

        BffNotificationStatusHistory oldEffectiveDate = buildEffectiveDate(
                "2025-01-01T10:00:00Z", BffNotificationReworkedStatus.NOT_VALID
        );

        BffNotificationStatusHistory newEffectiveDate = buildEffectiveDate(
                "2026-07-01T10:00:00Z", BffNotificationReworkedStatus.VALID
        );

        BffFullNotificationV1 notification = buildNotificationForAarAvailability(
                List.of(oldEffectiveDate, newEffectiveDate)
        );

        NotificationDetailUtility.setAarDocumentAvailability(notification, clock);

        assertTrue(notification.getAarDocumentAvailable());
    }

    @Test
    void setAarDocumentAvailability_availableWhenEffectiveDateIsMissing() {
        Clock clock = Clock.fixed(Instant.parse("2050-01-01T10:00:00Z"), ZoneOffset.UTC);

        BffFullNotificationV1 notification = buildNotificationForAarAvailability(List.of());

        NotificationDetailUtility.setAarDocumentAvailability(notification, clock);

        assertTrue(notification.getAarDocumentAvailable());
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
        FullSentNotificationV29 cancellationInProgressNotification = new FullSentNotificationV29();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), cancellationInProgressNotification);

        cancellationInProgressNotification.getTimeline().add(
                notificationDetailPaMock.getTimelineElem(
                        TimelineElementCategoryV28.NOTIFICATION_CANCELLATION_REQUEST,
                        null
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper
                .mapSentNotificationDetail(cancellationInProgressNotification, null);

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
        TimelineElementV28 sendAnalogProgress = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.SEND_ANALOG_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28().recIndex(0)
        );

        TimelineElementV28 sendAnalogFeedback = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.SEND_ANALOG_FEEDBACK,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28().recIndex(0).deliveryDetailCode("RECAG003C")
        );

        TimelineElementV28 sendAnalogRegisteredLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28().recIndex(0).deliveryDetailCode("NTINCLCD")
        );

        FullSentNotificationV29 analogNotification = new FullSentNotificationV29();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), analogNotification);

        analogNotification.getTimeline().addAll(
                List.of(
                        sendAnalogProgress,
                        sendAnalogFeedback,
                        sendAnalogRegisteredLetter
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(analogNotification, null);

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
        TimelineElementV28 analogFailure = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.ANALOG_FAILURE_WORKFLOW,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28()
                        .recIndex(0)
                        .generatedAarUrl("https://www.aar.com")
        );

        FullSentNotificationV29 analogFailureNotification = new FullSentNotificationV29();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), analogFailureNotification);
        analogFailureNotification.getTimeline().add(analogFailure);

        NotificationStatusHistoryElementV26 deliveredStatus = analogFailureNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) ->
                        status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatusV26.DELIVERED)
                )
                .findFirst()
                .orElseThrow();

        deliveredStatus.getRelatedTimelineElements().add(analogFailure.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(analogFailureNotification, null);

        BffNotificationStatusHistory bffDeliveredStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.DELIVERED)))
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
                notificationDetailPaMock.getOneRecipientNotification(),
                null
        );

        BffNotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.DELIVERED)))
                .findFirst()
                .orElseThrow();

        assertEquals(deliveredStep.getDeliveryMode(), BffNotificationDeliveryMode.DIGITAL);
    }

    @Test
    void deliveryModeAnalog() {
        FullSentNotificationV29 notificationDTO = new FullSentNotificationV29();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);

        TimelineElementV28 digitalSuccess = new TimelineElementV28();

        digitalSuccess = notificationDTO.getTimeline()
                .stream()
                .filter((timelineElem) ->
                        String.valueOf(timelineElem.getCategory())
                                .equals(String.valueOf(TimelineElementCategoryV28.DIGITAL_SUCCESS_WORKFLOW)))
                .findFirst()
                .orElseThrow();

        digitalSuccess.setCategory(TimelineElementCategoryV28.SEND_SIMPLE_REGISTERED_LETTER);

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO, null);
        BffNotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.DELIVERED)))
                .findFirst()
                .orElseThrow();

        assertEquals(deliveredStep.getDeliveryMode(), BffNotificationDeliveryMode.ANALOG);
    }

    @Test
    void deliveryModeNotAssigned() {
        FullSentNotificationV29 notificationDTO = new FullSentNotificationV29();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        notificationDTO.setTimeline(
                notificationDTO.getTimeline()
                        .stream()
                        .filter((timelineElem) ->
                                !String.valueOf(timelineElem.getCategory())
                                        .equals(String.valueOf(TimelineElementCategoryV28.DIGITAL_SUCCESS_WORKFLOW)))
                        .toList()
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO, null);
        BffNotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.DELIVERED)))
                .findFirst()
                .orElseThrow();

        assertNull(deliveredStep.getDeliveryMode());
    }

    @Test
    void checkFillingOfMacroSteps() {
        FullSentNotificationV29 notificationDTO = new FullSentNotificationV29();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO, null);

        boolean previousStepIsAccepted = false;
        ArrayList<String> acceptedItems = new ArrayList<>();

        // clean timeline related items
        for (NotificationStatusHistoryElementV26 notificationStatusHistory : notificationDTO.getNotificationStatusHistory()) {
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
            if (String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.ACCEPTED))) {
                previousStepIsAccepted = true;
                acceptedItems = notificationDTO.getNotificationStatusHistory()
                        .stream()
                        .filter((statusHistory) -> statusHistory.getStatus()
                                .equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatusV26.ACCEPTED)
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
        TimelineElementV28 sendCourtesy = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.SEND_COURTESY_MESSAGE,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28()
                        .recIndex(0)
                        .digitalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.DigitalAddress().type("APPIO").address(""))
                        .ioSendMessageResult(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.IoSendMessageResult.SENT_OPTIN)
        );

        FullSentNotificationV29 ioNotification = new FullSentNotificationV29();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), ioNotification);
        ioNotification.getTimeline().add(sendCourtesy);

        NotificationStatusHistoryElementV26 acceptedStatus = ioNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatusV26.ACCEPTED))
                .findFirst()
                .orElseThrow();

        acceptedStatus.getRelatedTimelineElements().add(sendCourtesy.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(ioNotification, null);

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
        FullSentNotificationV29 notificationDTO = new FullSentNotificationV29();
        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), notificationDTO);
        TimelineElementV28 digitalFailure = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.DIGITAL_FAILURE_WORKFLOW,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28()
                        .recIndex(0)
        );
        int digitalSuccessIndex = -1;
        for (int i = 0; i < notificationDTO.getTimeline().size(); i++) {
            if (String.valueOf(notificationDTO.getTimeline().get(i).getCategory())
                    .equals(String.valueOf(TimelineElementCategoryV28.DIGITAL_SUCCESS_WORKFLOW))) {
                digitalSuccessIndex = i;
                break;
            }
        }


        TimelineElementV28 prepareLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.PREPARE_SIMPLE_REGISTERED_LETTER,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PhysicalAddress()
                                .address("Via Rosas 1829")
                                .zip("98036")
                                .municipality("Graniti")
                        )
        );

        TimelineElementV28 sendLetter = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.SEND_SIMPLE_REGISTERED_LETTER,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28()
                        .recIndex(0)
                        .productType("RN_RS")
                        .physicalAddress(new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PhysicalAddress()
                                .address("Via Rosas 1829")
                                .zip("98036")
                                .municipality("Graniti")
                        )
        );

        NotificationStatusHistoryElementV26 prevDeliveredStatus = notificationDTO.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.DELIVERED)))
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

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTO, null);

        BffNotificationStatusHistory deliveredStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.DELIVERED)))
                .findFirst()
                .orElseThrow();

        BffNotificationStatusHistory deliveringStatus = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26.DELIVERING)))
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
        FullSentNotificationV29 viewedNotification = new FullSentNotificationV29();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), viewedNotification);

        TimelineElementV28 viewedTimelineElement = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.NOTIFICATION_VIEWED,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28()
                        .recIndex(0)
        );
        // add viewed timeline element
        viewedNotification.getTimeline().add(viewedTimelineElement);
        // add viewed status
        NotificationStatusHistoryElementV26 viewedStatus = new NotificationStatusHistoryElementV26();
        viewedStatus.setStatus(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatusV26.VIEWED);
        viewedStatus.setActiveFrom(viewedTimelineElement.getTimestamp());
        List<String> relatedViewedElement = new ArrayList<>();
        relatedViewedElement.add(viewedTimelineElement.getElementId());
        viewedStatus.setRelatedTimelineElements(relatedViewedElement);

        viewedNotification.addNotificationStatusHistoryItem(viewedStatus);

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper
                .mapSentNotificationDetail(viewedNotification, null);

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
        FullSentNotificationV29 viewedNotification = new FullSentNotificationV29();

        BeanUtils.copyProperties(notificationDetailPaMock.getOneRecipientNotification(), viewedNotification);

        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.DelegateInfo delegate =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.DelegateInfo()
                        .taxId("GLLGLL64B15G702I")
                        .denomination("Galileo Galilei")
                        .delegateType(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.RecipientType.PF);

        TimelineElementV28 viewedTimelineElement = notificationDetailPaMock.getTimelineElem(
                TimelineElementCategoryV28.NOTIFICATION_VIEWED,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementDetailsV28()
                        .recIndex(0)
                        .delegateInfo(delegate)
        );
        // add viewed timeline element
        viewedNotification.getTimeline().add(viewedTimelineElement);
        // add viewed status
        NotificationStatusHistoryElementV26 viewedStatus = new NotificationStatusHistoryElementV26();
        viewedStatus.setStatus(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationStatusV26.VIEWED);
        viewedStatus.setActiveFrom(viewedTimelineElement.getTimestamp());
        List<String> relatedViewedElement = new ArrayList<>();
        relatedViewedElement.add(viewedTimelineElement.getElementId());
        viewedStatus.setRelatedTimelineElements(relatedViewedElement);

        viewedNotification.addNotificationStatusHistoryItem(viewedStatus);

        BffFullNotificationV1 calculatedParsedNotification = NotificationSentDetailMapper.modelMapper
                .mapSentNotificationDetail(viewedNotification, null);

        BffNotificationStatusHistory viewedStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(BffNotificationStatus.VIEWED))
                        .findFirst()
                        .orElse(null);

        assertNotNull(viewedStatusHistory);
        assertEquals(viewedStatusHistory.getSteps().size(), 1);
        assertEquals(viewedStatusHistory.getRecipient(), delegate.getDenomination() + " (" + delegate.getTaxId() + ')');
    }


    @Test
    void shouldFindRecIndexOnSingleRecipient() {
        List<NotificationRecipientV24> recipients = List.of(
                new NotificationRecipientV24().recipientType(NotificationRecipientV24.RecipientTypeEnum.PF).taxId("123456789")
        );

        assertEquals(OptionalInt.of(0), findRecipientIndex(recipients));
    }

    @Test
    void shouldFindRecIndexOnMultiRecipients() {
        List<NotificationRecipientV24> recipients = List.of(
                new NotificationRecipientV24().recipientType(NotificationRecipientV24.RecipientTypeEnum.PF),
                new NotificationRecipientV24().recipientType(NotificationRecipientV24.RecipientTypeEnum.PF),
                new NotificationRecipientV24().recipientType(NotificationRecipientV24.RecipientTypeEnum.PF).taxId("123456789")
        );

        assertEquals(OptionalInt.of(2), findRecipientIndex(recipients));
    }

    @Test
    void shouldReturnEmptyWhenNoRecipientHasTaxId() {
        List<NotificationRecipientV24> recipients = List.of(
                new NotificationRecipientV24().recipientType(NotificationRecipientV24.RecipientTypeEnum.PF),
                new NotificationRecipientV24().recipientType(NotificationRecipientV24.RecipientTypeEnum.PF)
        );

        assertEquals(OptionalInt.empty(), findRecipientIndex(recipients));
    }

    private BffFullNotificationV1 buildNotificationForAarAvailability(
            List<BffNotificationStatusHistory> notificationStatusHistory
    ) {
        BffFullNotificationV1 notification = new BffFullNotificationV1();
        notification.setNotificationStatusHistory(new ArrayList<>(notificationStatusHistory));

        return notification;
    }

    private BffNotificationStatusHistory buildEffectiveDate(
            String activeFrom,
            BffNotificationReworkedStatus reworkedStatus
    ) {
        BffNotificationStatusHistory effectiveDate = new BffNotificationStatusHistory();

        effectiveDate.setStatus(BffNotificationStatus.EFFECTIVE_DATE);
        effectiveDate.setActiveFrom(OffsetDateTime.parse(activeFrom));
        effectiveDate.setReworkedStatus(reworkedStatus);

        return effectiveDate;
    }
}
