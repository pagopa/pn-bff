package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusHistoryElement;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementCategoryV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDetailUtilityTest {
    NotificationDetailMock notificationDetailMock = new NotificationDetailMock();

    @Test
    void populateOtherDocumentsTest() {
        FullReceivedNotificationV23 notificationDTOMultiRecipient = notificationDetailMock.notificationMultiRecipientMock();
        FullReceivedNotificationV23 notificationDTO = notificationDetailMock.getOneRecipientNotification();

        FullReceivedNotificationV23 noAARNotification = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDTOMultiRecipient, noAARNotification);
        noAARNotification.setTimeline(noAARNotification.getTimeline().stream().filter(
                t -> t.getCategory() != TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toList()));

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(noAARNotification);

        assert calculatedParsedNotification.getOtherDocuments().isEmpty();

        ArrayList<TimelineElementV23> AARTimelineElements = notificationDTO.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTO);

        assert calculatedParsedNotification.getOtherDocuments().size() == 1;

        assert calculatedParsedNotification.getOtherDocuments().get(0).equals(
                new NotificationDetailDocument()
                        .recIndex(0)
                        .documentId(AARTimelineElements.get(0).getDetails().getGeneratedAarUrl())
                        .documentType("AAR")
                        .title("Avviso di avvenuta ricezione")
                        .digests(new NotificationAttachmentDigests().sha256(""))
                        .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                        .contentType("")
        );

        // MULTI RECIPIENT
        ArrayList<TimelineElementV23> AARTimelineElementsMultiRecipient = notificationDTOMultiRecipient.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTOMultiRecipient);

        assert calculatedParsedNotification.getOtherDocuments().size() == AARTimelineElementsMultiRecipient.size();

        int index = 0;
        for (TimelineElementV23 element : AARTimelineElementsMultiRecipient) {
            assert calculatedParsedNotification.getOtherDocuments().get(index).equals(
                    new NotificationDetailDocument()
                            .recIndex(element.getDetails().getRecIndex())
                            .documentId(element.getDetails().getGeneratedAarUrl())
                            .documentType("AAR")
                            .title("Avviso di avvenuta ricezione - " +
                                    notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getDenomination() +
                                    "(" + notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getTaxId() + ")"
                            )
                            .digests(new NotificationAttachmentDigests().sha256(""))
                            .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                            .contentType("")
            );
            index++;
        }
    }

    @Test
    void checkRADDInTimeline() {
        NotificationDetailTimeline raddFromTimeline = notificationDetailMock.notificationToFERADD().getRadd();

        assert raddFromTimeline.equals(
                new NotificationDetailTimeline()
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
                        .hidden(true)
        );
    }

    @Test
    void insertCancelledStatusInTimeline() {
        FullReceivedNotificationV23 cancellationInProgressNotification = new FullReceivedNotificationV23();

        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), cancellationInProgressNotification);

        cancellationInProgressNotification.getTimeline().add(
                notificationDetailMock.getTimelineElem(
                        TimelineElementCategoryV23.NOTIFICATION_CANCELLATION_REQUEST,
                        null
                )
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper
                .mapNotificationDetail(cancellationInProgressNotification);

        NotificationStatusHistory cancellationInProgressStatusHistory =
                calculatedParsedNotification.getNotificationStatusHistory().stream()
                        .filter(status -> status.getStatus().equals(NotificationStatus.CANCELLATION_IN_PROGRESS))
                        .findFirst()
                        .orElse(null);

        assert cancellationInProgressStatusHistory != null;

        assert calculatedParsedNotification.getNotificationStatus().equals(NotificationStatus.CANCELLATION_IN_PROGRESS);
    }

    @Test
    void setTimelineHidden() {
        TimelineElementV23 sendAnalogProgress = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementDetailsV23().recIndex(0)
        );

        TimelineElementV23 sendAnalogFeedback = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("RECAG003C")
        );

        TimelineElementV23 sendAnalogRegisteredLetter = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementDetailsV23().recIndex(0).deliveryDetailCode("NTINCLCD")
        );

        FullReceivedNotificationV23 analogNotification = new FullReceivedNotificationV23();

        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), analogNotification);

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

    @Test
    void populateLegalFactsOfAnalogFailureStep() {
        TimelineElementV23 analogFailure = notificationDetailMock.getTimelineElem(
                TimelineElementCategoryV23.ANALOG_FAILURE_WORKFLOW,
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementDetailsV23()
                        .recIndex(0)
                        .generatedAarUrl("https://www.aar.com")
        );

        FullReceivedNotificationV23 analogFailureNotification = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), analogFailureNotification);
        analogFailureNotification.getTimeline().add(analogFailure);

        NotificationStatusHistoryElement deliveredStatus = analogFailureNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) ->
                        status.getStatus().equals(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)
                )
                .findFirst()
                .orElseThrow();

        deliveredStatus.getRelatedTimelineElements().add(analogFailure.getElementId());

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(analogFailureNotification);

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
        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(
                notificationDetailMock.getOneRecipientNotification()
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
        FullReceivedNotificationV23 notificationDTO = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), notificationDTO);

        TimelineElementV23 digitalSuccess = new TimelineElementV23();

        digitalSuccess = notificationDTO.getTimeline()
                .stream()
                .filter((timelineElem) ->
                        String.valueOf(timelineElem.getCategory())
                                .equals(String.valueOf(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)))
                .findFirst()
                .orElseThrow();

        digitalSuccess.setCategory(TimelineElementCategoryV23.SEND_SIMPLE_REGISTERED_LETTER);

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTO);
        NotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        Assertions.assertEquals(deliveredStep.getDeliveryMode(), NotificationDeliveryMode.ANALOG);
    }

    @Test
    void deliveryModeNotAssigned() {
        FullReceivedNotificationV23 notificationDTO = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDetailMock.getOneRecipientNotification(), notificationDTO);
        notificationDTO.setTimeline(
                notificationDTO.getTimeline()
                        .stream()
                        .filter((timelineElem) ->
                                !String.valueOf(timelineElem.getCategory())
                                        .equals(String.valueOf(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)))
                        .toList()
        );

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTO);
        NotificationStatusHistory deliveredStep = calculatedParsedNotification.getNotificationStatusHistory()
                .stream()
                .filter((status) -> String.valueOf(status.getStatus()).equals(String.valueOf(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus.DELIVERED)))
                .findFirst()
                .orElseThrow();

        Assertions.assertNull(deliveredStep.getDeliveryMode());
    }

}
