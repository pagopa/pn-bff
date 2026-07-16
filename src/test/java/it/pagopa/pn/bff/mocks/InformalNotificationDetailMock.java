package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalNotificationStatusV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalTimelineElementCategoryV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalTimelineElementV1;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class InformalNotificationDetailMock {

    public static final String IUN = "IUN-INFORMAL-123";
    public static final String SENDER_PA_ID = "sender-pa-id-1234";
    public static final OffsetDateTime SENT_AT = OffsetDateTime.parse("2024-01-01T10:00:00Z");
    public static final OffsetDateTime FILED_AT = OffsetDateTime.parse("2024-01-02T11:30:00Z");

    public FullReceivedInformalNotificationV1 getInformalNotificationMock() {
        FullReceivedInformalNotificationV1 notification = new FullReceivedInformalNotificationV1();
        notification.setIun(IUN);
        notification.setSenderDenomination("Comune di Milano");
        notification.setCampaignId("campaign-1");
        notification.setSubject("Oggetto notifica bonaria");
        notification.setGroup("group-1");
        notification.setSentAt(SENT_AT);
        notification.setSenderPaId(SENDER_PA_ID);
        notification.setDocumentsAvailable(true);
        notification.setNotificationStatus(InformalNotificationStatusV1.ACCEPTED);
        notification.setTimeline(timelineWithRequestAccepted());
        return notification;
    }

    /**
     * Same notification but the timeline has no REQUEST_ACCEPTED element, so filedAt cannot be derived.
     */
    public FullReceivedInformalNotificationV1 getInformalNotificationWithoutRequestAcceptedMock() {
        FullReceivedInformalNotificationV1 notification = getInformalNotificationMock();
        notification.setTimeline(new ArrayList<>(List.of(
                timelineElement(InformalTimelineElementCategoryV1.VALIDATE_NORMALIZE_ADDRESSES_REQUEST, SENT_AT)
        )));
        return notification;
    }

    private List<InformalTimelineElementV1> timelineWithRequestAccepted() {
        List<InformalTimelineElementV1> timeline = new ArrayList<>();
        timeline.add(timelineElement(InformalTimelineElementCategoryV1.VALIDATE_NORMALIZE_ADDRESSES_REQUEST, SENT_AT));
        timeline.add(timelineElement(InformalTimelineElementCategoryV1.REQUEST_ACCEPTED, FILED_AT));
        return timeline;
    }

    private InformalTimelineElementV1 timelineElement(InformalTimelineElementCategoryV1 category,
                                                      OffsetDateTime timestamp) {
        return new InformalTimelineElementV1()
                .elementId(category.getValue() + "-element")
                .category(category)
                .eventTimestamp(timestamp);
    }
}
