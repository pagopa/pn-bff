package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.*;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimelineMock {

    public static ArrayList<TimelineElementV23> getTimelineMock() {
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
                        .elementId("AAR_GEN.IUN_RTRD-UDGU-QTQY-202308-P-1.RECINDEX_2")
                        .timestamp(OffsetDateTime.parse("2023-08-25T09:35:27.328299384Z"))
                        .legalFactsIds(new ArrayList<>())
                        .category(TimelineElementCategoryV23.AAR_GENERATION)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(2)
                                .generatedAarUrl("PN_AAR-7e3c456307f743669b42105aa9357dac.pdf")
                                .numberOfPages(1)
                        )
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
                                .noticeCode("302011692956029088")
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
                                .noticeCode("302011692956029096")
                                .paymentSourceChannel("EXTERNAL_REGISTRY")
                        )
        );

        timeline.add(
                new TimelineElementV23()
                        .elementId("NOTIFICATION_PAID.IUN_RTRD-UDGU-QTQY-202308-P-1.CODE_PPA30218167745972026777777777777")
                        .timestamp(OffsetDateTime.parse("2023-08-25T11:38:05.392Z"))
                        .category(TimelineElementCategoryV23.PAYMENT)
                        .details(new TimelineElementDetailsV23()
                                .recIndex(2)
                                .recipientType(RecipientType.PG)
                                .amount(65)
                                .creditorTaxId("77777777777")
                                .noticeCode("302181677459720267")
                                .paymentSourceChannel("EXTERNAL_REGISTRY")
                        )
        );


        return timeline;
    }

    public static ArrayList<TimelineElementV23> getTimelineRADDMock() {
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
}
