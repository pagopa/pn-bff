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
    void deliveredFromDedicatedDeliveredEvent() {
        List<InformalTimelineElementV1> timeline = List.of(
                dispatchEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE, "PEC", T1),
                deliveredEvent("PEC", T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.PEC, BffChannelStatusV1.DELIVERED);
    }

    @Test
    void deliveredForAnalogChannel() {
        List<InformalTimelineElementV1> timeline = List.of(
                new InformalTimelineElementV1().category(InformalTimelineElementCategoryV1.SEND_ANALOG_MESSAGE).eventTimestamp(T1),
                deliveredEvent("ANALOG", T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.ANALOG, BffChannelStatusV1.DELIVERED);
    }

    @Test
    void notDeliveredFromKoFeedback() {
        List<InformalTimelineElementV1> timeline = List.of(
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC", ResponseStatus.KO, T1)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.PEC, BffChannelStatusV1.NOT_DELIVERED);
    }

    @Test
    void sentWhenLatestFeedbackIsOk() {
        List<InformalTimelineElementV1> timeline = List.of(
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC", ResponseStatus.KO, T1),
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC", ResponseStatus.OK, T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.PEC, BffChannelStatusV1.SENT);
    }

    @Test
    void sentWhenFeedbackIsOkButNoDeliveredEventYet() {
        List<InformalTimelineElementV1> timeline = List.of(
                dispatchEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE, "PEC", T1),
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "PEC", ResponseStatus.OK, T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.PEC, BffChannelStatusV1.SENT);
    }

    @Test
    void sentWhenDispatchedWithoutFeedback() {
        List<InformalTimelineElementV1> timeline = List.of(
                dispatchEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE, "PEC", T1)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.PEC, BffChannelStatusV1.SENT);
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
    void smsCapsAtSentEvenWithDeliveredEvent() {
        List<InformalTimelineElementV1> timeline = List.of(
                dispatchEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE, "SMS", T1),
                feedbackEvent(InformalTimelineElementCategoryV1.SEND_DIGITAL_MESSAGE_FEEDBACK, "SMS", ResponseStatus.OK, T2),
                deliveredEvent("SMS", T2)
        );

        assertStatus(InformalNotificationStatusV1.PROCESSING, timeline, BffNotificationChannelType.SMS, BffChannelStatusV1.SENT);
    }

    @Test
    void sendingWhenProcessingWithNoChannelEvents() {
        assertStatus(InformalNotificationStatusV1.PROCESSING, List.of(), BffNotificationChannelType.ANALOG, BffChannelStatusV1.WAITING_TO_SEND);
    }

    @Test
    void ioReturnsWaitingToSendWhenProcessing() {
        assertStatus(InformalNotificationStatusV1.PROCESSING, List.of(), BffNotificationChannelType.IO, BffChannelStatusV1.WAITING_TO_SEND);
    }

    @Test
    void everyChannelWithNoEventsIsWaitingWhileProcessing() {
        List<BffChannelDeliveryStatusV1> result = SentInformalNotificationChannelStatusResolver.populateChannelStatuses(
                InformalNotificationStatusV1.PROCESSING,
                List.of(),
                List.of(BffNotificationChannelType.EMAIL, BffNotificationChannelType.SMS));

        assertEquals(BffChannelStatusV1.WAITING_TO_SEND, result.get(0).getStatus());
        assertEquals(BffChannelStatusV1.WAITING_TO_SEND, result.get(1).getStatus());
    }

    @Test
    void readyToSendWhenAccepted() {
        assertStatus(InformalNotificationStatusV1.ACCEPTED, List.of(), BffNotificationChannelType.PEC, BffChannelStatusV1.READY_TO_SEND);
    }

    @Test
    void workflowEndedWhenChannelNeverAttempted() {
        List<InformalTimelineElementV1> timeline = List.of(
                new InformalTimelineElementV1().category(InformalTimelineElementCategoryV1.WORKFLOW_DONE_REACHED).eventTimestamp(T1)
        );

        assertStatus(InformalNotificationStatusV1.COMPLETED_UNREACHED, timeline, BffNotificationChannelType.SMS, BffChannelStatusV1.WORKFLOW_ENDED);
    }


    @Test
    void undeliverableWithoutWorkflowDoneReachedIsStillWaiting() {
        assertStatus(InformalNotificationStatusV1.UNDELIVERABLE, List.of(), BffNotificationChannelType.PEC, BffChannelStatusV1.WAITING_TO_SEND);
    }

    @Test
    void refusedNotificationFallsBackToReadyToSend() {
        assertStatus(InformalNotificationStatusV1.REFUSED, List.of(), BffNotificationChannelType.PEC, BffChannelStatusV1.READY_TO_SEND);
    }

    @Test
    void sendChannelIsFiledWithoutWebView() {
        assertStatus(InformalNotificationStatusV1.ACCEPTED, List.of(), BffNotificationChannelType.SEND, BffChannelStatusV1.FILED);
    }

    @Test
    void sendChannelIsFiledWhenRequestAccepted() {
        List<InformalTimelineElementV1> timeline = List.of(
                new InformalTimelineElementV1()
                        .category(InformalTimelineElementCategoryV1.REQUEST_ACCEPTED)
                        .eventTimestamp(T1)
        );

        assertStatus(InformalNotificationStatusV1.ACCEPTED, timeline, BffNotificationChannelType.SEND, BffChannelStatusV1.FILED);
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

    private InformalTimelineElementV1 deliveredEvent(String channel, OffsetDateTime timestamp) {
        return new InformalTimelineElementV1()
                .category(InformalTimelineElementCategoryV1.DELIVERED)
                .eventTimestamp(timestamp)
                .details(new InformalTimelineElementDetailsV1().channel(channel));
    }
}
