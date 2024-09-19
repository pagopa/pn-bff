package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchRow;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.ResponseCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCheckAarRequest;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationsReceivedMock {

    public static final String RECIPIENT_ID = "RECIPIENT_ID";
    public static final String SENDER_ID = "SENDER_ID";
    public static final String MANDATE_ID = "MANDATE_ID";
    public static final String SUBJECT_REG_EXP = "SUBJECT";
    public static final String IUN_MATCH = "IUN";
    public static final it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.NotificationStatus STATUS = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.NotificationStatus.ACCEPTED;
    public static final int SIZE = 10;
    public static final String START_DATE = "2014-04-30T00:00:00.000Z";
    public static final String END_DATE = "2024-04-30T00:00:00.000Z";
    public static final String GROUP = "GROUP";
    public static final String NEXT_PAGES_KEY = "NEXT_PAGES_KEY";

    public NotificationSearchResponse getNotificationReceivedPNMock() {
        NotificationSearchResponse notificationSearchResponse = new NotificationSearchResponse();
        List<NotificationSearchRow> notificationSearchRows = new ArrayList<>();
        NotificationSearchRow notificationSearchRowOne = new NotificationSearchRow();
        notificationSearchRowOne.setIun("IUN1");
        notificationSearchRowOne.setPaProtocolNumber("Protocol Number One");
        notificationSearchRowOne.setSender("Sender One");
        notificationSearchRowOne.setSentAt(OffsetDateTime.parse("2024-04-29T13:54:42.563421537Z"));
        notificationSearchRowOne.setSubject("Subject One");
        notificationSearchRowOne.setNotificationStatus(NotificationStatus.ACCEPTED);
        notificationSearchRowOne.setRecipients(new ArrayList<>(Arrays.asList("Person 1", "Person 2")));
        notificationSearchRowOne.setRequestAcceptedAt(OffsetDateTime.parse("2024-04-29T13:58:57.90787261Z"));
        notificationSearchRowOne.setGroup("Group One");
        NotificationSearchRow notificationSearchRowTwo = new NotificationSearchRow();
        notificationSearchRowTwo.setIun("IUN2");
        notificationSearchRowTwo.setPaProtocolNumber("Protocol Number Two");
        notificationSearchRowTwo.setSender("Sender Two");
        notificationSearchRowTwo.setSentAt(OffsetDateTime.parse("2024-04-29T13:54:42.563421537Z"));
        notificationSearchRowTwo.setSubject("Subject Two");
        notificationSearchRowTwo.setNotificationStatus(NotificationStatus.ACCEPTED);
        notificationSearchRowTwo.setRecipients(new ArrayList<>(Arrays.asList("Person 3", "Person 4")));
        notificationSearchRowTwo.setRequestAcceptedAt(OffsetDateTime.parse("2024-04-29T13:58:57.90787261Z"));
        notificationSearchRowTwo.setGroup("Group Two");
        notificationSearchRows.add(notificationSearchRowOne);
        notificationSearchRows.add(notificationSearchRowTwo);
        notificationSearchResponse.setResultsPage(notificationSearchRows);
        notificationSearchResponse.setMoreResult(false);
        return notificationSearchResponse;
    }

    public ResponseCheckAarMandateDto getResponseCheckAarMandateDtoPNMock() {
        ResponseCheckAarMandateDto responseCheckAarMandateDto = new ResponseCheckAarMandateDto();
        responseCheckAarMandateDto.setMandateId("MANDATE_ID");
        responseCheckAarMandateDto.setIun("IUN");
        return responseCheckAarMandateDto;
    }

    public BffCheckAarRequest getRequestCheckAarMandateDtoPNMock() {
        BffCheckAarRequest bffRequestCheckAarMandateDto = new BffCheckAarRequest();
        bffRequestCheckAarMandateDto.setAarQrCodeValue("S0FVRC1XTUFLLUdLTVktMjAyNDAzLU4tMV9QRi0zNzY1NDU2MS00NDZhLTRjODgtYjMyOC02Njk5YTgzMjJiMzNfZGI0YmJiZDktM2UwNy00YWJlLTk2ZTktOGY4ZTVlMzRkYWM2");
        return bffRequestCheckAarMandateDto;
    }
}