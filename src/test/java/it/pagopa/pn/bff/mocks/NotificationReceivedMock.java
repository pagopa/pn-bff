package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchRow;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationReceivedMock {
    public NotificationSearchResponse getNotificationReceivedPNMock(){
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
}
