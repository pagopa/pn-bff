package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalNotificationStatusHistoryElementV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalNotificationStatusV1;

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
        notification.setNotificationStatusHistory(statusHistoryWithAccepted());
        return notification;
    }

    /**
     * Same notification but the status history has no ACCEPTED element, so filedAt cannot be derived.
     */
    public FullReceivedInformalNotificationV1 getInformalNotificationWithoutAcceptedMock() {
        FullReceivedInformalNotificationV1 notification = getInformalNotificationMock();
        notification.setNotificationStatus(InformalNotificationStatusV1.IN_VALIDATION);
        notification.setNotificationStatusHistory(new ArrayList<>(List.of(
                statusHistoryElement(InformalNotificationStatusV1.IN_VALIDATION, SENT_AT)
        )));
        return notification;
    }

    private List<InformalNotificationStatusHistoryElementV1> statusHistoryWithAccepted() {
        List<InformalNotificationStatusHistoryElementV1> history = new ArrayList<>();
        history.add(statusHistoryElement(InformalNotificationStatusV1.IN_VALIDATION, SENT_AT));
        history.add(statusHistoryElement(InformalNotificationStatusV1.ACCEPTED, FILED_AT));
        return history;
    }

    private InformalNotificationStatusHistoryElementV1 statusHistoryElement(InformalNotificationStatusV1 status,
                                                                            OffsetDateTime activeFrom) {
        return new InformalNotificationStatusHistoryElementV1()
                .status(status)
                .activeFrom(activeFrom)
                .relatedTimelineElements(new ArrayList<>());
    }
}
