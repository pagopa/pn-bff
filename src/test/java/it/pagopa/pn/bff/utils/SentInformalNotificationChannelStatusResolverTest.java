package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffChannelDeliveryStatusV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffChannelStatusV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationChannelType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentInformalNotificationChannelStatusResolverTest {

    private static final OffsetDateTime T1 = OffsetDateTime.parse("2026-09-09T10:00:00Z");
    private static final OffsetDateTime T2 = OffsetDateTime.parse("2026-09-09T11:00:00Z");

    @Test
    void viewedTakesPriorityOverFeedbackForIoChannel() {
        List<InformalTimelineElementV1> timeline = List.of(
                viewedEvent("IO", T1),
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "IO", ResponseStatus.OK, T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.IO, BffChannelStatusV1.VIEWED);
    }

    @Test
    void deliveredFromLatestOkFeedback() {
        List<InformalTimelineElementV1> timeline = List.of(
                dispatchEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE, "PEC", T1),
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC", ResponseStatus.OK, T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.PEC, BffChannelStatusV1.DELIVERED);
    }

    @Test
    void notDeliveredFromLatestKoFeedback() {
        List<InformalTimelineElementV1> timeline = List.of(
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC", ResponseStatus.KO, T1)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.PEC, BffChannelStatusV1.NOT_DELIVERED);
    }

    @Test
    void unavailableFromDigitalMessageSkip() {
        List<InformalTimelineElementV1> timeline = List.of(
                new InformalTimelineElementV1()
                        .category(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_SKIP)
                        .eventTimestamp(T1)
                        .details(new InformalTimelineElementDetailsV1().channel("SMS"))
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.SMS, BffChannelStatusV1.UNAVAILABLE);
    }

    @Test
    void smsCapsAtSentEvenWithPositiveFeedback() {
        List<InformalTimelineElementV1> timeline = List.of(
                dispatchEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE, "SMS", T1),
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "SMS", ResponseStatus.OK, T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.SMS, BffChannelStatusV1.SENT);
    }

    @Test
    void sendingWhenProcessingWithNoChannelEvents() {
        assertStatus(InformalNotificationStatusV1.PROCESSING, List.of(), BffNotificationChannelType.ANALOG, BffChannelStatusV1.SENDING);
    }

    @Test
    void ioNeverReturnsSendingEvenWhenProcessing() {
        // IO does not support SENDING: it must fall back to WAITING_TO_SEND instead
        assertStatus(InformalNotificationStatusV1.PROCESSING, List.of(), BffNotificationChannelType.IO, BffChannelStatusV1.WAITING_TO_SEND);
    }

    @Test
    void onlyTheFirstUnattemptedChannelInTheWorkflowIsSending() {
        List<BffChannelDeliveryStatusV1> result = SentInformalNotificationChannelStatusResolver.populateChannelStatuses(
                InformalNotificationStatusV1.PROCESSING,
                List.of(),
                List.of(BffNotificationChannelType.EMAIL, BffNotificationChannelType.SMS));

        assertEquals(BffChannelStatusV1.SENDING, result.get(0).getStatus());
        assertEquals(BffChannelStatusV1.WAITING_TO_SEND, result.get(1).getStatus());
    }

    @Test
    void waitingToSendWhenAccepted() {
        assertStatus(InformalNotificationStatusV1.ACCEPTED, List.of(), BffNotificationChannelType.PEC, BffChannelStatusV1.WAITING_TO_SEND);
    }

    @Test
    void workflowEndedWhenChannelNeverAttempted() {
        assertStatus(InformalNotificationStatusV1.COMPLETED_UNREACHED, List.of(), BffNotificationChannelType.SMS, BffChannelStatusV1.WORKFLOW_ENDED);
    }

    @Test
    void workflowEndedAlsoWhenNotificationIsUndeliverable() {
        assertStatus(InformalNotificationStatusV1.UNDELIVERABLE, List.of(), BffNotificationChannelType.PEC, BffChannelStatusV1.WORKFLOW_ENDED);
    }

    @Test
    void sendChannelIsFiledWithoutWebView() {
        assertStatus(InformalNotificationStatusV1.ACCEPTED, List.of(), BffNotificationChannelType.SEND, BffChannelStatusV1.FILED);
    }

    @Test
    void sendChannelIsViewedWhenViewedFromWeb() {
        List<InformalTimelineElementV1> timeline = List.of(viewedEvent("WEB", T1));

        assertStatus(InformalNotificationStatusV1.ACCEPTED, timeline, BffNotificationChannelType.SEND, BffChannelStatusV1.VIEWED);
    }

    private void assertStatus(InformalNotificationStatusV1 notificationStatus,
                              List<InformalTimelineElementV1> timeline,
                              BffNotificationChannelType channel,
                              BffChannelStatusV1 expectedStatus) {
        List<BffChannelDeliveryStatusV1> result = SentInformalNotificationChannelStatusResolver.populateChannelStatuses(
                notificationStatus, timeline, List.of(channel));

        assertEquals(1, result.size());
        assertEquals(channel, result.get(0).getChannel());
        assertEquals(expectedStatus, result.get(0).getStatus());
    }

    private InformalTimelineElementV1 dispatchEvent(InformalTimelineElementCategoryV1 category, String channel, OffsetDateTime timestamp) {
        return new InformalTimelineElementV1()
                .category(category)
                .eventTimestamp(timestamp)
                .details(new InformalTimelineElementDetailsV1().channel(channel));
    }

    private InformalTimelineElementV1 feedbackEvent(InformalTimelineElementCategoryV1 category, String channel,
                                                    ResponseStatus responseStatus, OffsetDateTime timestamp) {
        return new InformalTimelineElementV1()
                .category(category)
                .eventTimestamp(timestamp)
                .details(new InformalTimelineElementDetailsV1().channel(channel).responseStatus(responseStatus));
    }

    private InformalTimelineElementV1 viewedEvent(String sourceChannel, OffsetDateTime timestamp) {
        return new InformalTimelineElementV1()
                .category(InformalTimelineElementCategoryV1.INFORMAL_NOTIFICATION_VIEWED)
                .eventTimestamp(timestamp)
                .details(new InformalTimelineElementDetailsV1().sourceChannel(sourceChannel));
    }
}
