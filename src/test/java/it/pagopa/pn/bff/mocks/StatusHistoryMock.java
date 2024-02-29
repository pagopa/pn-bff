package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusHistoryElement;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatusHistoryMock {
    public ArrayList<NotificationStatusHistoryElement> getStatusHistoryMock() {
        ArrayList<NotificationStatusHistoryElement> notificationStatusHistory = new ArrayList<>();

        notificationStatusHistory.add(
                new NotificationStatusHistoryElement()
                        .status(NotificationStatus.ACCEPTED)
                        .activeFrom(OffsetDateTime.parse("2023-08-25T09:33:58.709695008Z"))
                        .relatedTimelineElements(List.of(
                                "REQUEST_ACCEPTED.IUN_RTRD-UDGU-QTQY-202308-P-1",
                                "AAR_CREATION_REQUEST.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0",
                                "AAR_CREATION_REQUEST.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1",
                                "AAR_CREATION_REQUEST.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_2",
                                "AAR_GEN.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1",
                                "AAR_GEN.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0",
                                "AAR_GEN.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_2",
                                "SEND_COURTESY_MESSAGE.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.COURTESYADDRESSTYPE_SMS",
                                "PROBABLE_SCHEDULING_ANALOG_DATE.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0",
                                "GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_PLATFORM.ATTEMPT_0",
                                "GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_PLATFORM.ATTEMPT_0",
                                "GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.ATTEMPT_0",
                                "GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.ATTEMPT_0"
                        ))
        );

        notificationStatusHistory.add(
                new NotificationStatusHistoryElement()
                        .status(NotificationStatus.DELIVERING)
                        .activeFrom(OffsetDateTime.parse("2023-08-25T09:35:37.972607129Z"))
                        .relatedTimelineElements(List.of(
                                "SEND_DIGITAL.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0",
                                "SEND_DIGITAL.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0",
                                "DIGITAL_PROG.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0.IDX_1",
                                "DIGITAL_PROG.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0.IDX_1",
                                "SEND_DIGITAL_FEEDBACK.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0",
                                "DIGITAL_DELIVERY_CREATION_REQUEST.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0",
                                "SCHEDULE_REFINEMENT_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0",
                                "SEND_DIGITAL_FEEDBACK.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0"
                        ))
        );

        notificationStatusHistory.add(
                new NotificationStatusHistoryElement()
                        .status(NotificationStatus.DELIVERED)
                        .activeFrom(OffsetDateTime.parse("2023-08-25T09:36:02.708723361Z"))
                        .relatedTimelineElements(List.of(
                                "DIGITAL_DELIVERY_CREATION_REQUEST.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1",
                                "SCHEDULE_REFINEMENT_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1",
                                "DIGITAL_SUCCESS_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0",
                                "DIGITAL_SUCCESS_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1"
                        ))
        );

        notificationStatusHistory.add(
                new NotificationStatusHistoryElement()
                        .status(NotificationStatus.EFFECTIVE_DATE)
                        .activeFrom(OffsetDateTime.parse("2023-08-25T09:36:02.708723361Z"))
                        .relatedTimelineElements(List.of(
                                "REFINEMENT.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0",
                                "REFINEMENT.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1",
                                "NOTIFICATION_PAID.IUN_RTRD-UDGU-QTQY-202308-P-1.CODE_PPA30201169295602908877777777777",
                                "NOTIFICATION_PAID.IUN_RTRD-UDGU-QTQY-202308-P-1.CODE_PPA30201169295602909677777777777",
                                "NOTIFICATION_PAID.IUN_RTRD-UDGU-QTQY-202308-P-1.CODE_PPA30218167745972026777777777777"
                        ))
        );

        return notificationStatusHistory;
    }
}
