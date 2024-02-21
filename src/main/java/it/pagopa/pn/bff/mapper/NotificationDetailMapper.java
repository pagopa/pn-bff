package it.pagopa.pn.bff.mapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mapper.NotificationElementMapper.RecipientNotificationTimelineMapper;
import it.pagopa.pn.bff.mapper.NotificationElementMapper.SenderNotificationTimelineMapper;
import it.pagopa.pn.bff.utils.notificationDetail.NotificationDetailUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(uses = {DateMapper.class, RecipientNotificationTimelineMapper.class, SenderNotificationTimelineMapper.class})
public interface NotificationDetailMapper {

    NotificationDetailMapper modelMapper = Mappers.getMapper(NotificationDetailMapper.class);

    BffFullNotificationV1 mapNotificationDetail(FullReceivedNotificationV23 notification);

    BffFullNotificationV1 mapSentNotificationDetail(FullSentNotificationV23 notification);


    @AfterMapping
    default void insertCancelledStatusInTimeline(FullReceivedNotificationV23 notification, @MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        Optional<NotificationDetailTimeline> timelineCancelledElement = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == TimelineCategory.NOTIFICATION_CANCELLED)
                .findFirst();

        if (timelineCancelledElement.isEmpty()) {
            Optional<NotificationDetailTimeline> timelineCancellationRequestElement = bffFullNotificationV1.getTimeline().stream()
                    .filter(el -> el.getCategory() == TimelineCategory.NOTIFICATION_CANCELLATION_REQUEST)
                    .findFirst();

            if (timelineCancellationRequestElement.isPresent()) {
                OffsetDateTime timestamp = OffsetDateTime.parse(timelineCancellationRequestElement.get().getTimestamp());

                NotificationStatusHistory notificationStatusHistoryElement =
                        new NotificationStatusHistory(NotificationStatus.CANCELLATION_IN_PROGRESS, timestamp, new ArrayList<>(), new ArrayList<>(), null, null);

                bffFullNotificationV1.getNotificationStatusHistory().add(notificationStatusHistoryElement);
                bffFullNotificationV1.setNotificationStatus(NotificationStatus.CANCELLATION_IN_PROGRESS);
            }
        }
    }

    @AfterMapping
    default void setTimelineIndex(FullReceivedNotificationV23 notification, @MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        for (int i = 0; i < bffFullNotificationV1.getTimeline().size(); i++) {
            bffFullNotificationV1.getTimeline().get(i).setIndex(i);
        }
    }

    @AfterMapping
    default void populateOtherDocuments(FullReceivedNotificationV23 notification, @MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        List<NotificationDetailDocument> otherDocuments = new ArrayList<>();

        List<NotificationDetailTimeline> timelineFiltered = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == TimelineCategory.AAR_GENERATION)
                .toList();

        if (!timelineFiltered.isEmpty()) {
            final boolean isMultiRecipient = timelineFiltered.size() > 1;

            otherDocuments = timelineFiltered.stream()
                    .map(t -> {
                        final Integer recIndex = t.getDetails().getRecIndex();
                        final List<NotificationDetailRecipient> recipients = bffFullNotificationV1.getRecipients();
                        final String recipientData = isMultiRecipient && recIndex != null
                                ? " - " + recipients.get(recIndex).getDenomination() + "(" + recipients.get(recIndex).getTaxId() + ")"
                                : "";
                        final String title = "Avviso di avvenuta ricezione" + recipientData;

                        return new NotificationDetailDocument()
                                .recIndex(recIndex)
                                .documentId(t.getDetails().getGeneratedAarUrl())
                                .documentType(LegalFactType.AAR.toString())
                                .title(title)
                                .digests(
                                        new NotificationAttachmentDigests()
                                                .sha256("")
                                )
                                .ref(
                                        new NotificationAttachmentBodyRef()
                                                .key("")
                                                .versionToken("")
                                )
                                .contentType("");
                    }).toList();
        }

        bffFullNotificationV1.setOtherDocuments(otherDocuments);
    }

    @AfterMapping
    default void sortNotificationStatusHistory(FullReceivedNotificationV23 notification, @MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        bffFullNotificationV1.getNotificationStatusHistory().sort((o1, o2) -> o2.getActiveFrom().compareTo(o1.getActiveFrom()));
    }

    public default OffsetDateTime parseOffsetDateTime(String date) {
        return date != null ? OffsetDateTime.parse(date) : null;
    }

    @AfterMapping
    default void populateMacroStep(FullReceivedNotificationV23 notification, @MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        List<String> acceptedStatusItems = new ArrayList<>();
        NotificationDeliveryMode deliveryMode = null;
        NotificationStatusHistory deliveringStatus = null;
        int lastDeliveredIndexToShift = -1;
        boolean lastDeliveredIndexToShiftIsFixed = false;
        boolean preventShiftFromDeliveredToDelivering = false;

        for (NotificationStatusHistory status : bffFullNotificationV1.getNotificationStatusHistory()) {
            if (NotificationStatus.DELIVERING.equals(status.getStatus())) {
                deliveringStatus = status;
            }

            if (NotificationStatus.ACCEPTED.equals(status.getStatus()) && !status.getRelatedTimelineElements().isEmpty()) {
                acceptedStatusItems = status.getRelatedTimelineElements();
            } else if (!acceptedStatusItems.isEmpty()) {
                status.getRelatedTimelineElements().addAll(0, acceptedStatusItems);
            }

            status.setSteps(new ArrayList<>());

            int ix = 0;
            for (String timelineElement : status.getRelatedTimelineElements()) {
                NotificationDetailTimeline step = NotificationDetailUtility.populateMacroStep(bffFullNotificationV1, timelineElement, status, acceptedStatusItems);
                if (step != null) {
                    if (deliveryMode == null && TimelineCategory.DIGITAL_SUCCESS_WORKFLOW.equals(step.getCategory())) {
                        deliveryMode = NotificationDeliveryMode.DIGITAL;
                    } else if (deliveryMode == null && (TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER.equals(step.getCategory())
                            || TimelineCategory.ANALOG_SUCCESS_WORKFLOW.equals(step.getCategory()))) {
                        deliveryMode = NotificationDeliveryMode.ANALOG;
                    }

                    if (NotificationStatus.DELIVERED.equals(status.getStatus()) && !preventShiftFromDeliveredToDelivering) {
                        if ((TimelineCategory.DIGITAL_FAILURE_WORKFLOW.equals(step.getCategory())
                                || TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER.equals(step.getCategory())
                                || TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.equals(step.getCategory()))
                                && !lastDeliveredIndexToShiftIsFixed) {
                            lastDeliveredIndexToShift = ix;
                        } else if (TimelineCategory.DIGITAL_SUCCESS_WORKFLOW.equals(step.getCategory())) {
                            if (lastDeliveredIndexToShift > -1) {
                                lastDeliveredIndexToShift = ix - 1;
                                lastDeliveredIndexToShiftIsFixed = true;
                            } else {
                                preventShiftFromDeliveredToDelivering = true;
                            }
                        }
                    }
                }
                ix++;
            }

            if (NotificationStatus.DELIVERED.equals(status.getStatus())
                    && deliveringStatus != null
                    && deliveringStatus.getSteps() != null
                    && !preventShiftFromDeliveredToDelivering
                    && lastDeliveredIndexToShift > -1) {
                List<NotificationDetailTimeline> stepsToShift = new ArrayList<>(status.getSteps().subList(0, lastDeliveredIndexToShift + 1));
                stepsToShift.sort(NotificationDetailUtility::fromLatestToEarliest);
                deliveringStatus.getSteps().addAll(0, stepsToShift);
                status.setSteps(new ArrayList<>(status.getSteps().subList(lastDeliveredIndexToShift + 1, status.getSteps().size())));
                status.setActiveFrom(parseOffsetDateTime(deliveringStatus.getSteps().get(0).getTimestamp()));
            }

            status.getSteps().sort(NotificationDetailUtility::fromLatestToEarliest);

            if (!NotificationStatus.ACCEPTED.equals(status.getStatus()) && !acceptedStatusItems.isEmpty()) {
                acceptedStatusItems.clear();
            }

            if (NotificationStatus.DELIVERED.equals(status.getStatus()) && deliveryMode != null) {
                status.setDeliveryMode(deliveryMode);
            }

            if (NotificationStatus.VIEWED.equals(status.getStatus())) {
                List<NotificationDetailTimeline> viewedSteps = status.getSteps().stream()
                        .filter(s -> TimelineCategory.NOTIFICATION_VIEWED.equals(s.getCategory()))
                        .toList();

                if (!viewedSteps.isEmpty()) {
                    NotificationDetailTimeline mostOldViewedStep = viewedSteps.get(viewedSteps.size() - 1);

                    if (mostOldViewedStep.getDetails() != null) {
                        NotificationDetailTimelineDetails viewedDetails = mostOldViewedStep.getDetails();
                        if (viewedDetails.getDelegateInfo() != null) {
                            String recipient = viewedDetails.getDelegateInfo().getDenomination() +
                                    " (" + viewedDetails.getDelegateInfo().getTaxId() + ")";
                            status.setRecipient(recipient);
                        }
                    }
                }
            }
        }
    }
}