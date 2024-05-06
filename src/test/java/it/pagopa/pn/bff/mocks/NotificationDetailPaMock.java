package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.mappers.notifications.NotificationDetailMapper;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationDetailPaMock {

    private NotificationDocument getDocumentMock(String idx) {
        NotificationDocument document = new NotificationDocument();
        document.setDocIdx(idx);
        document.setTitle("Document_" + idx);
        document.setContentType("application/pdf");
        document.setDigests(new NotificationAttachmentDigests().sha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlE=" + idx));
        document.setRef(
                new NotificationAttachmentBodyRef()
                        .key("PN_NOTIFICATION_ATTACHMENTS-abb7804b6e442c8b2223648af970cd1-" + idx + ".pdf")
                        .versionToken("v1")
        );
        return document;
    }

    private ArrayList<NotificationDocument> getDocumentsMock() {
        ArrayList<NotificationDocument> notificationDocuments = new ArrayList<>();
        notificationDocuments.add(getDocumentMock("0"));
        return notificationDocuments;
    }

    private NotificationPaymentItem getPaymentMock(String type, String index, boolean hasAttachment, String f24Title) {
        NotificationPaymentItem paymentItem = new NotificationPaymentItem();
        if (Objects.equals(type, "PagoPa") || Objects.equals(type, "PagoPaAndF24")) {
            PagoPaPayment pagoPaPayment = new PagoPaPayment();
            pagoPaPayment.setCreditorTaxId("77777777777");
            pagoPaPayment.setNoticeCode("33333333333333333" + index);
            pagoPaPayment.setApplyCost(Objects.equals(index, "0"));
            if (hasAttachment) {
                NotificationPaymentAttachment paymentAttachment = new NotificationPaymentAttachment();
                paymentAttachment.setContentType("application/pdf");
                paymentAttachment.setDigests(new NotificationAttachmentDigests().sha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlA=" + index));
                paymentAttachment.setRef(new NotificationAttachmentBodyRef()
                        .key("PN_NOTIFICATION_ATTACHMENTS-abb7804b6e442c8b2223648af970cd1-" + index + ".pdf")
                        .versionToken("v1"));
                pagoPaPayment.setAttachment(paymentAttachment);
                paymentItem.setPagoPa(pagoPaPayment);
            }
        }
        if (Objects.equals(type, "F24") || Objects.equals(type, "PagoPaAndF24")) {
            F24Payment f24Payment = new F24Payment();
            f24Payment.setApplyCost(Objects.equals(index, "0"));
            f24Payment.setTitle(f24Title);
            NotificationMetadataAttachment metadataAttachment = new NotificationMetadataAttachment();
            metadataAttachment.setContentType("application/pdf");
            metadataAttachment.setDigests(new NotificationAttachmentDigests().sha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlB=" + index));
            metadataAttachment.setRef(new NotificationAttachmentBodyRef()
                    .key("PN_NOTIFICATION_ATTACHMENTS-bbb7804b6e442c8b2223648af970cd1-" + index + ".pdf")
                    .versionToken("v1"));
            f24Payment.setMetadataAttachment(metadataAttachment);
            paymentItem.setF24(f24Payment);
        }
        return paymentItem;
    }

    private ArrayList<NotificationPaymentItem> getPaymentsMockRec0() {
        ArrayList<NotificationPaymentItem> notificationPaymentItems = new ArrayList<>();
        notificationPaymentItems.add(getPaymentMock("PagoPa", "0", true, null));
        notificationPaymentItems.add(getPaymentMock("PagoPa", "1", true, null));
        notificationPaymentItems.add(getPaymentMock("PagoPa", "2", true, null));
        notificationPaymentItems.add(getPaymentMock("PagoPa", "3", true, null));
        notificationPaymentItems.add(getPaymentMock("PagoPa", "4", true, null));
        notificationPaymentItems.add(getPaymentMock("PagoPa", "5", true, null));
        notificationPaymentItems.add(getPaymentMock("PagoPa", "6", false, null));

        return notificationPaymentItems;
    }

    private ArrayList<NotificationPaymentItem> getPaymentsMockRec1() {
        ArrayList<NotificationPaymentItem> notificationPaymentItems = new ArrayList<>();
        notificationPaymentItems.add(getPaymentMock("PagoPa", "7", true, "F24 prima rata TARI"));
        notificationPaymentItems.add(getPaymentMock("F24", "8", false, "F24 seconda rata TARI"));
        notificationPaymentItems.add(getPaymentMock("F24", "9", false, "F24 terza rata TARI"));

        return notificationPaymentItems;
    }

    private NotificationRecipientV23 getRecipientMock(NotificationRecipientV23.RecipientTypeEnum recipientType, String taxId, String denomination, ArrayList<NotificationPaymentItem> payments) {
        NotificationRecipientV23 recipient = new NotificationRecipientV23();
        recipient.setRecipientType(recipientType);
        recipient.setTaxId(taxId);
        recipient.setDenomination(denomination);
        recipient.setDigitalDomicile(new NotificationDigitalAddress()
                .type(NotificationDigitalAddress.TypeEnum.PEC)
                .address("notifichedigitali-uat@pec.pagopa.it"));
        recipient.setPhysicalAddress(new NotificationPhysicalAddress()
                .at("Presso")
                .address("VIA SENZA NOME")
                .addressDetails("SCALA B")
                .zip("87100")
                .municipality("MILANO")
                .municipalityDetails("MILANO")
                .province("MI")
                .foreignState("ITALIA"));
        recipient.setPayments(payments);
        return recipient;
    }

    private ArrayList<NotificationRecipientV23> getRecipientsMock() {
        ArrayList<NotificationRecipientV23> recipients = new ArrayList<>();
        recipients.add(getRecipientMock(NotificationRecipientV23.RecipientTypeEnum.PF, "LVLDAA85T50G702B", "Ada Lovelace", getPaymentsMockRec0()));
        recipients.add(getRecipientMock(NotificationRecipientV23.RecipientTypeEnum.PF, "FRMTTR76M06B715E", "Ettore Fieramosca", getPaymentsMockRec1()));
        return recipients;
    }

    private ArrayList<NotificationStatusHistoryElement> getStatusHistoryMock() {
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

    private ArrayList<TimelineElementV23> getTimelineMock() {
        ArrayList<TimelineElementV23> timeline = new ArrayList<>();

        timeline.add(
                new TimelineElementV23()
                        .elementId("REQUEST_ACCEPTED.IUN_RTRD-UDGU-QTQY-202308-P-1")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:34:58.041398918Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.REQUEST_ACCEPTED)
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("AAR_GEN.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:27.328299384Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.AAR_GENERATION)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .generatedAarUrl("PN_AAR-7e3c456307f743669b42105aa9357dae.pdf")
                                .numberOfPages(1)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("AAR_GEN.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:27.34501351Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.AAR_GENERATION)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .generatedAarUrl("PN_AAR-6dc9aa2aceec4a18b4b073df09a1ed12.pdf")
                                .numberOfPages(1)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("SEND_COURTESY_MESSAGE.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.COURTESYADDRESSTYPE_SMS")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:28.673819084Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.SEND_COURTESY_MESSAGE)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .sendDate(OffsetDateTime.parse("2023-08-25T09:35:28.673626121Z"))
                                .digitalAddress(new DigitalAddress().type("SMS").address("+393889533897"))
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_PLATFORM.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:37.430018585Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.GET_ADDRESS)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .digitalAddressSource(DigitalAddressSource.PLATFORM)
                                .isAvailable(false)
                                .attemptDate(OffsetDateTime.parse("2023-08-25T09:35:37.430013304Z"))
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_PLATFORM.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:37.438177621Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.GET_ADDRESS)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .digitalAddressSource(DigitalAddressSource.PLATFORM)
                                .isAvailable(false)
                                .attemptDate(OffsetDateTime.parse("2023-08-25T09:35:37.438172821Z"))
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:37.459264115Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.GET_ADDRESS)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .isAvailable(true)
                                .attemptDate(OffsetDateTime.parse("2023-08-25T09:35:37.459259637Z"))
                        )
        );


        timeline.add(
                new TimelineElementV23()
                        .elementId("GET_ADDRESS.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:37.467148235Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.GET_ADDRESS)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .isAvailable(true)
                                .attemptDate(OffsetDateTime.parse("2023-08-25T09:35:37.467144969Z"))
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("SEND_DIGITAL.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:37.972607129Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.SEND_DIGITAL_DOMICILE)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .digitalAddress(new DigitalAddress().type("PEC").address("notifichedigitali-uat@pec.pagopa.it"))
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .retryNumber(0)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("SEND_DIGITAL.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:40.989759156Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.SEND_DIGITAL_DOMICILE)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .digitalAddress(new DigitalAddress().type("PEC").address("testpagopa3@pec.pagopa.it"))
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .retryNumber(0)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("DIGITAL_PROG.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0.IDX_1")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:48.01877805Z"))
                        .legalFactsIds(List.of(new LegalFactsId()
                                .key("PN_EXTERNAL_LEGAL_FACTS-bf46b5cb7617404095595a4ed53a4022.xml")
                                .category(LegalFactCategory.PEC_RECEIPT)
                        ))
                        .category(TimelineElementCategoryV23.SEND_DIGITAL_PROGRESS)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .digitalAddress(new DigitalAddress().type("PEC").address("notifichedigitali-uat@pec.pagopa.it"))
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .retryNumber(0)
                                .notificationDate(OffsetDateTime.parse("2023-08-25T09:35:49.409272045Z"))
                                .deliveryDetailCode("C001")
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("DIGITAL_PROG.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0.IDX_1")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:50.895375375Z"))
                        .legalFactsIds(List.of(new LegalFactsId()
                                .key("PN_EXTERNAL_LEGAL_FACTS-10446363e8904ff9b93cc1835e8f6253.xml")
                                .category(LegalFactCategory.PEC_RECEIPT)
                        ))
                        .category(TimelineElementCategoryV23.SEND_DIGITAL_PROGRESS)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .digitalAddress(new DigitalAddress().type("PEC").address("testpagopa3@pec.pagopa.it"))
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .retryNumber(0)
                                .notificationDate(OffsetDateTime.parse("2023-08-25T09:35:52.20063392Z"))
                                .deliveryDetailCode("C001")
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("SEND_DIGITAL_FEEDBACK.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:58.40995459Z"))
                        .legalFactsIds(List.of(new LegalFactsId()
                                .key("PN_EXTERNAL_LEGAL_FACTS-d0b33189dcb24f51bdd50363e14c001d.xml")
                                .category(LegalFactCategory.PEC_RECEIPT)
                        ))
                        .category(TimelineElementCategoryV23.SEND_DIGITAL_FEEDBACK)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .sendDate(OffsetDateTime.parse("2023-08-25T09:35:53.20063392Z"))
                                .digitalAddress(new DigitalAddress().type("EMAIL").address("notifichedigitali-uat@pec.pagopa.it"))
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .responseStatus(ResponseStatus.OK)
                                .notificationDate(OffsetDateTime.parse("2023-08-25T09:35:58.40995459Z"))
                                .deliveryDetailCode("C003")
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("SCHEDULE_REFINEMENT_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:36:00.079496693Z"))
                        .category(TimelineElementCategoryV23.SCHEDULE_REFINEMENT)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("SEND_DIGITAL_FEEDBACK.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1.SOURCE_SPECIAL.REPEAT_false.ATTEMPT_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:36:01.184038269Z"))
                        .legalFactsIds(List.of(new LegalFactsId()
                                .key("PN_EXTERNAL_LEGAL_FACTS-3d98741cbeeb4712a3fc709261f83241.xml")
                                .category(LegalFactCategory.PEC_RECEIPT)
                        ))
                        .category(TimelineElementCategoryV23.SEND_DIGITAL_FEEDBACK)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .digitalAddress(new DigitalAddress().type("PEC").address("testpagopa3@pec.pagopa.it"))
                                .digitalAddressSource(DigitalAddressSource.SPECIAL)
                                .responseStatus(ResponseStatus.OK)
                                .notificationDate(OffsetDateTime.parse("2023-08-25T09:36:01.184038269Z"))
                                .deliveryDetailCode("C003")
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("SCHEDULE_REFINEMENT_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:36:02.927741692Z"))
                        .category(TimelineElementCategoryV23.SCHEDULE_REFINEMENT)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("DIGITAL_SUCCESS_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:36:17.520532258Z"))
                        .legalFactsIds(List.of(new LegalFactsId()
                                .key("PN_LEGAL_FACTS-4ba554c616344022838ff39a617ab0df.pdf")
                                .category(LegalFactCategory.DIGITAL_DELIVERY)
                        ))
                        .category(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .digitalAddress(new DigitalAddress().type("EMAIL").address("notifichedigitali-uat@pec.pagopa.it"))
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("DIGITAL_SUCCESS_WORKFLOW.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:36:17.545859567Z"))
                        .legalFactsIds(List.of(new LegalFactsId()
                                .key("PN_LEGAL_FACTS-b7d638d7b3eb407fac78160b7e1e92d5.pdf")
                                .category(LegalFactCategory.DIGITAL_DELIVERY)
                        ))
                        .category(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .digitalAddress(new DigitalAddress().type("EMAIL").address("testpagopa3@pec.pagopa.it"))
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("REFINEMENT.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_0")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:39:07.843258714Z"))
                        .category(TimelineElementCategoryV23.REFINEMENT)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("REFINEMENT.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_1")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:39:07.855372374Z"))
                        .category(TimelineElementCategoryV23.REFINEMENT)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("NOTIFICATION_PAID.IUN_RTRD-UDGU-QTQY-202308-P-1.CODE_PPA30201169295602908877777777777")
                        .timestamp(OffsetDateTime.parse("2023-08-25T11:36:32.24Z"))
                        .category(TimelineElementCategoryV23.PAYMENT)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(0)
                                .recipientType(RecipientType.PF)
                                .amount(200)
                                .creditorTaxId("77777777777")
                                .noticeCode("333333333333333330")
                                .paymentSourceChannel("EXTERNAL_REGISTRY")
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("NOTIFICATION_PAID.IUN_RTRD-UDGU-QTQY-202308-P-1.CODE_PPA30201169295602909677777777777")
                        .timestamp(OffsetDateTime.parse("2023-08-25T11:38:05.392Z"))
                        .category(TimelineElementCategoryV23.PAYMENT)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(1)
                                .recipientType(RecipientType.PF)
                                .amount(200)
                                .creditorTaxId("77777777777")
                                .noticeCode("333333333333333338")
                                .paymentSourceChannel("EXTERNAL_REGISTRY")
                        )
        );


        return timeline;
    }

    private ArrayList<TimelineElementV23> getTimelineRADDMock() {
        ArrayList<TimelineElementV23> timeline = new ArrayList<>();

        BeanUtils.copyProperties(getTimelineMock(), timeline);

        timeline.add(
                new TimelineElementV23()
                        .elementId("NOTIFICATION_RADD_RETRIEVED_mock")
                        .timestamp(OffsetDateTime.parse("2022-06-21T11:44:28Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.NOTIFICATION_RADD_RETRIEVED)
                        .details(
                                new TimelineElementDetailsV23()
                                        .recIndex(1)
                                        .eventTimestamp(OffsetDateTime.parse("2022-06-21T11:44:28Z"))
                                        .raddType("ALT")
                                        .raddTransactionId("6")
                        )
        );

        return timeline;
    }

    public FullSentNotificationV23 getNotificationMultiRecipientMock() {
        FullSentNotificationV23 bffFullNotificationV1Mock = new FullSentNotificationV23();

        bffFullNotificationV1Mock.setAbstract("Abstract della notifica");
        bffFullNotificationV1Mock.setPaProtocolNumber("302011692956029071");
        bffFullNotificationV1Mock.setSubject("notifica analogica con cucumber");
        bffFullNotificationV1Mock.setNotificationFeePolicy(NotificationFeePolicy.FLAT_RATE);
        bffFullNotificationV1Mock.setPhysicalCommunicationType(FullSentNotificationV23.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER);
        bffFullNotificationV1Mock.setSenderDenomination("Comune di palermo");
        bffFullNotificationV1Mock.senderTaxId("80016350821");
        bffFullNotificationV1Mock.setGroup("000");
        bffFullNotificationV1Mock.setSenderPaId("5b994d4a-0fa8-47ac-9c7b-354f1d44a1ce");
        bffFullNotificationV1Mock.setIun("RTRD-UDGU-QTQY-202308-P-1");
        bffFullNotificationV1Mock.setSentAt(OffsetDateTime.parse("2023-08-25T09:33:58.709695008Z"));
        bffFullNotificationV1Mock.setDocumentsAvailable(true);
        bffFullNotificationV1Mock.setNotificationStatus(NotificationStatus.EFFECTIVE_DATE);
        bffFullNotificationV1Mock.setRecipients(getRecipientsMock());
        bffFullNotificationV1Mock.setDocuments(getDocumentsMock());
        bffFullNotificationV1Mock.setNotificationStatusHistory(getStatusHistoryMock());
        bffFullNotificationV1Mock.setTimeline(getTimelineMock());

        return bffFullNotificationV1Mock;
    }

    public FullSentNotificationV23 getOneRecipientNotification() {
        FullSentNotificationV23 oneRecipientNotification = getNotificationMultiRecipientMock();
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

    public BffFullNotificationV1 notificationToFERADD() {
        FullSentNotificationV23 notificationDTORadd = getNotificationMultiRecipientMock();
        notificationDTORadd.setTimeline(getTimelineRADDMock());
        return NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDTORadd);
    }

    public TimelineElementV23 getTimelineElem(TimelineElementCategoryV23 category, TimelineElementDetailsV23 details) {
        return new TimelineElementV23()
                .category(category)
                .elementId(category + ".IUN_RTRD-UDGU-QTQY-202308-P-1")
                .timestamp(OffsetDateTime.parse("2023-08-25T11:38:05.392Z"))
                .details(details);
    }

}