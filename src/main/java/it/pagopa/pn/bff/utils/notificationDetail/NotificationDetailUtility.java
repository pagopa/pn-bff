package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NotificationDetailUtility {
    public static OffsetDateTime parseOffsetDateTime(String date) {
        return date != null ? OffsetDateTime.parse(date) : null;
    }

    public static Integer fromLatestToEarliest(NotificationDetailTimeline a, NotificationDetailTimeline b) {
        long differenceInTimeline = (b.getTimestamp() != null && a.getTimestamp() != null) ? b.getTimestamp().compareTo(a.getTimestamp()) : 0;
        int differenceInIndex = (b.getIndex() != null && a.getIndex() != null) ? b.getIndex() - a.getIndex() : 0;

        if (differenceInTimeline > 0) {
            return 1;
        } else if (differenceInTimeline < 0) {
            return -1;
        } else {
            return Integer.compare(differenceInIndex, 0);
        }
    }

    public static boolean isInternalAppIoEvent(NotificationDetailTimeline step) {
        if (step.getCategory() == TimelineCategory.SEND_COURTESY_MESSAGE) {
            NotificationDetailTimelineDetails details = step.getDetails();
            return details.getDigitalAddress().getType().equals("APPIO")
                    && details.getIoSendMessageResult() != null
                    && !details.getIoSendMessageResult().equals(IoSendMessageResult.SENT_COURTESY);
        } else {
            return false;
        }
    }

    public static boolean timelineElementMustBeShown(NotificationDetailTimeline t) {
        final List<TimelineCategory> AnalogFlowAllowedCodes =
                Arrays.asList(TimelineCategory.SEND_ANALOG_PROGRESS, TimelineCategory.SEND_ANALOG_FEEDBACK,
                        TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS);

        final List<TimelineCategory> TimelineAllowedStatus =
                Arrays.asList(
                        TimelineCategory.SCHEDULE_DIGITAL_WORKFLOW,
                        // PN-6902
                        TimelineCategory.ANALOG_FAILURE_WORKFLOW,
                        TimelineCategory.SEND_DIGITAL_DOMICILE,
                        TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER,
                        TimelineCategory.SEND_ANALOG_DOMICILE,
                        TimelineCategory.SEND_DIGITAL_FEEDBACK,
                        TimelineCategory.SEND_DIGITAL_PROGRESS,
                        // PN-2068
                        TimelineCategory.SEND_COURTESY_MESSAGE,
                        // PN-1647
                        TimelineCategory.NOT_HANDLED,
                        TimelineCategory.SEND_ANALOG_PROGRESS,
                        TimelineCategory.SEND_ANALOG_FEEDBACK,
                        TimelineCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS,
                        // PN-7743
                        TimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE
                );

        if (AnalogFlowAllowedCodes.contains(t.getCategory())) {
            String deliveryDetailCode = t.getDetails().getDeliveryDetailCode();
            return deliveryDetailCode != null && AnalogFlowAllowedCodes.contains(deliveryDetailCode);
        }

        return TimelineAllowedStatus.contains(t.getCategory());
    }

    public static void insertCancelledStatusInTimeline(BffFullNotificationV1 bffFullNotificationV1) {
        Optional<NotificationDetailTimeline> timelineCancelledElement = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == TimelineCategory.NOTIFICATION_CANCELLED)
                .findFirst();

        if (timelineCancelledElement.isEmpty()) {
            Optional<NotificationDetailTimeline> timelineCancellationRequestElement = bffFullNotificationV1.getTimeline().stream()
                    .filter(el -> el.getCategory() == TimelineCategory.NOTIFICATION_CANCELLATION_REQUEST)
                    .findFirst();

            if (timelineCancellationRequestElement.isPresent()) {
                OffsetDateTime timestamp = timelineCancellationRequestElement.get().getTimestamp();

                NotificationStatusHistory notificationStatusHistoryElement =
                        new NotificationStatusHistory(NotificationStatus.CANCELLATION_IN_PROGRESS, timestamp, new ArrayList<>(), new ArrayList<>(), null, null);

                bffFullNotificationV1.getNotificationStatusHistory().add(notificationStatusHistoryElement);
                bffFullNotificationV1.setNotificationStatus(NotificationStatus.CANCELLATION_IN_PROGRESS);
            }
        }
    }

    public static void populateOtherDocuments(BffFullNotificationV1 bffFullNotificationV1) {
        List<NotificationDetailDocument> otherDocuments = new ArrayList<>();

        List<NotificationDetailTimeline> timelineFiltered = bffFullNotificationV1.getTimeline().stream()
                .filter(el -> el.getCategory() == TimelineCategory.AAR_GENERATION)
                .toList();

        if (!timelineFiltered.isEmpty()) {
            final boolean isMultiRecipient = timelineFiltered.size() > 1;

            otherDocuments = timelineFiltered.stream()
                    .map(t -> {
                        final Integer recIndex = t.getDetails().getRecIndex();
                        final List<NotificationRecipientV23> recipients = bffFullNotificationV1.getRecipients();
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
}
