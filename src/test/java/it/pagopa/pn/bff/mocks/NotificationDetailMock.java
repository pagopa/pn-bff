package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

public class NotificationDetailMock {
    RecipientsMock recipientsMock = new RecipientsMock();
    DocumentsMock documentsMock = new DocumentsMock();
    StatusHistoryMock statusHistoryMock = new StatusHistoryMock();
    TimelineMock timelineMock = new TimelineMock();

    public FullReceivedNotificationV23 notificationMultiRecipientMock() {
        FullReceivedNotificationV23 bffFullNotificationV1Mock = new FullReceivedNotificationV23();

        bffFullNotificationV1Mock.setAbstract("Abstract della notifica");
        bffFullNotificationV1Mock.setPaProtocolNumber("302011692956029071");
        bffFullNotificationV1Mock.setSubject("notifica analogica con cucumber");
        bffFullNotificationV1Mock.setNotificationFeePolicy(NotificationFeePolicy.FLAT_RATE);
        bffFullNotificationV1Mock.setPhysicalCommunicationType(FullReceivedNotificationV23.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER);
        bffFullNotificationV1Mock.setSenderDenomination("Comune di palermo");
        bffFullNotificationV1Mock.senderTaxId("80016350821");
        bffFullNotificationV1Mock.setGroup("000");
        bffFullNotificationV1Mock.setSenderPaId("5b994d4a-0fa8-47ac-9c7b-354f1d44a1ce");
        bffFullNotificationV1Mock.setIun("RTRD-UDGU-QTQY-202308-P-1");
        bffFullNotificationV1Mock.setSentAt(OffsetDateTime.parse("2023-08-25T09:33:58.709695008Z"));
        bffFullNotificationV1Mock.setDocumentsAvailable(true);
        bffFullNotificationV1Mock.setNotificationStatus(NotificationStatus.EFFECTIVE_DATE);
        bffFullNotificationV1Mock.setRecipients(recipientsMock.getRecipientsMock());
        bffFullNotificationV1Mock.setDocuments(documentsMock.getDocumentsMock());
        bffFullNotificationV1Mock.setNotificationStatusHistory(statusHistoryMock.getStatusHistoryMock());
        bffFullNotificationV1Mock.setTimeline(timelineMock.getTimelineMock());

        return bffFullNotificationV1Mock;
    }

    public FullReceivedNotificationV23 getOneRecipientNotification() {
        FullReceivedNotificationV23 oneRecipientNotification = notificationMultiRecipientMock();
        oneRecipientNotification.setRecipients(Collections.singletonList(oneRecipientNotification.getRecipients().get(0)));
        oneRecipientNotification.setTimeline(oneRecipientNotification.getTimeline()
                .stream()
                .filter(t -> t.getDetails() != null && t.getDetails().getRecIndex() == 0)
                .collect(Collectors.toList()));

        for (NotificationStatusHistoryElement status : oneRecipientNotification.getNotificationStatusHistory()) {
            status.setRelatedTimelineElements(status.getRelatedTimelineElements()
                    .stream()
                    .filter(el -> !el.contains("RECINDEX_1"))
                    .collect(Collectors.toList()));
        }

        return oneRecipientNotification;
    }

    public BffFullNotificationV1 notificationToFE() {
        FullReceivedNotificationV23 notificationMonoRecipient = getOneRecipientNotification();
        return NotificationDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationMonoRecipient);
    }

    public BffFullNotificationV1 notificationToFEMultiRecipient() {
        FullReceivedNotificationV23 notificationMultiRecipient = notificationMultiRecipientMock();
        return NotificationDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationMultiRecipient);
    }


    public BffFullNotificationV1 notificationToFERADD() {
        FullReceivedNotificationV23 notificationDTORadd = notificationMultiRecipientMock();
        notificationDTORadd.setTimeline(timelineMock.getTimelineRADDMock());
        return NotificationDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationDTORadd);
    }

    public TimelineElementV23 getTimelineElem(TimelineElementCategoryV23 category, TimelineElementDetailsV23 details) {
        return new TimelineElementV23()
                .category(category)
                .elementId(category + ".IUN_RTRD-UDGU-QTQY-202308-P-1")
                .timestamp(OffsetDateTime.parse("2023-08-25T11:38:05.392Z"))
                .details(details);
    }

}
