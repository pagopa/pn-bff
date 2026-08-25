package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimelineEventUtilityTest {

    private BffNotificationTimelineEvent event(BffTimelineCategory category, BffNotificationDetailTimelineDetails details) {
        return new BffNotificationTimelineEvent()
                .elementId("EL_ID.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.ATTEMPT_2")
                .category(category)
                .details(details);
    }

    @Test
    void extractChannelDigital() {
        BffNotificationTimelineEvent event = event(BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                new BffNotificationDetailTimelineDetails().digitalAddress(new DigitalAddress().type("pec")));

        assertEquals("PEC", TimelineEventUtility.extractChannel(event, BffNotificationTimelineGroupCategory.DIGITAL));
    }

    @Test
    void extractChannelAnalog() {
        BffNotificationTimelineEvent event = event(BffTimelineCategory.SEND_ANALOG_DOMICILE,
                new BffNotificationDetailTimelineDetails().serviceLevel(ServiceLevel.AR_REGISTERED_LETTER));

        assertEquals("AR_REGISTERED_LETTER", TimelineEventUtility.extractChannel(event, BffNotificationTimelineGroupCategory.ANALOG));
    }

    @Test
    void extractChannelWithoutDigitalAddress() {
        // digitalAddress is optional on the digital progress details
        BffNotificationTimelineEvent event = event(BffTimelineCategory.SEND_DIGITAL_PROGRESS,
                new BffNotificationDetailTimelineDetails().retryNumber(0));

        assertNull(TimelineEventUtility.extractChannel(event, BffNotificationTimelineGroupCategory.DIGITAL));
    }

    @Test
    void extractChannelWithoutServiceLevel() {
        // serviceLevel is optional on the analog progress details
        BffNotificationTimelineEvent event = event(BffTimelineCategory.SEND_ANALOG_PROGRESS,
                new BffNotificationDetailTimelineDetails().sentAttemptMade(0));

        assertNull(TimelineEventUtility.extractChannel(event, BffNotificationTimelineGroupCategory.ANALOG));
    }

    @Test
    void extractAttemptIsOneBased() {
        BffNotificationTimelineEvent digital = event(BffTimelineCategory.SEND_DIGITAL_DOMICILE,
                new BffNotificationDetailTimelineDetails().retryNumber(0));
        BffNotificationTimelineEvent analog = event(BffTimelineCategory.SEND_ANALOG_DOMICILE,
                new BffNotificationDetailTimelineDetails().sentAttemptMade(1));

        assertEquals(1, TimelineEventUtility.extractAttempt(digital, BffNotificationTimelineGroupCategory.DIGITAL, "PEC"));
        assertEquals(2, TimelineEventUtility.extractAttempt(analog, BffNotificationTimelineGroupCategory.ANALOG, "AR_REGISTERED_LETTER"));
    }

    @Test
    void extractChannelWithoutDetails() {
        BffNotificationTimelineEvent event = event(BffTimelineCategory.SEND_DIGITAL_DOMICILE, null);

        assertNull(TimelineEventUtility.extractChannel(event, BffNotificationTimelineGroupCategory.DIGITAL));
    }

    @Test
    void extractAttemptWithoutDetailsFallsBackOnElementId() {
        BffNotificationTimelineEvent event = event(BffTimelineCategory.SEND_DIGITAL_DOMICILE, null);

        assertEquals(3, TimelineEventUtility.extractAttempt(event, BffNotificationTimelineGroupCategory.DIGITAL, "PEC"));
    }

    @Test
    void extractPrepareFailureAttemptWithoutDetails() {
        BffNotificationTimelineEvent event = event(BffTimelineCategory.PREPARE_ANALOG_DOMICILE_FAILURE, null);

        assertNull(TimelineEventUtility.extractPrepareFailureAttempt(event));
    }

    @Test
    void extractAttemptOfFlowWithoutAttempts() {
        BffNotificationTimelineEvent courtesy = event(BffTimelineCategory.SEND_COURTESY_MESSAGE,
                new BffNotificationDetailTimelineDetails().retryNumber(0));

        assertNull(TimelineEventUtility.extractAttempt(courtesy, BffNotificationTimelineGroupCategory.COURTESY, "COURTESY"));
    }

    @Test
    void extractRecIndexFallsBackOnElementId() {
        // the element id is parsed only when the details do not carry the recipient index
        String elementId = "SEND_DIGITAL.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_3.ATTEMPT_0";

        BffNotificationTimelineEvent withoutRecIndex = new BffNotificationTimelineEvent()
                .elementId(elementId)
                .details(new BffNotificationDetailTimelineDetails());
        BffNotificationTimelineEvent withRecIndex = new BffNotificationTimelineEvent()
                .elementId(elementId)
                .details(new BffNotificationDetailTimelineDetails().recIndex(1));

        assertEquals(3, TimelineEventUtility.extractRecIndex(withoutRecIndex));
        assertEquals(1, TimelineEventUtility.extractRecIndex(withRecIndex));
    }
}
