package it.pagopa.pn.bff.utils.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementCategoryV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationAttachmentBodyRef;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationAttachmentDigests;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailDocument;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
class PopulateOtherDocumentsTest {

    @Test
    void populateOtherDocumentsTest() {
        FullReceivedNotificationV23 notificationDTOMultiRecipient = NotificationDetailMock.notificationMultiRecipientMock();
        FullReceivedNotificationV23 notificationDTO = NotificationDetailMock.getOneRecipientNotification();

        FullReceivedNotificationV23 noAARNotification = new FullReceivedNotificationV23();
        BeanUtils.copyProperties(notificationDTOMultiRecipient, noAARNotification);
        noAARNotification.setTimeline(noAARNotification.getTimeline().stream().filter(
                t -> t.getCategory() != TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toList()));

        BffFullNotificationV1 calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(noAARNotification);

        assert calculatedParsedNotification.getOtherDocuments().isEmpty();

        ArrayList<TimelineElementV23> AARTimelineElements = notificationDTO.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTO);

        assert calculatedParsedNotification.getOtherDocuments().size() == 1;

        assert calculatedParsedNotification.getOtherDocuments().get(0).equals(
                new NotificationDetailDocument()
                        .recIndex(0)
                        .documentId(AARTimelineElements.get(0).getDetails().getGeneratedAarUrl())
                        .documentType("AAR")
                        .title("Avviso di avvenuta ricezione")
                        .digests(new NotificationAttachmentDigests().sha256(""))
                        .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                        .contentType("")
        );

        // MULTI RECIPIENT
        ArrayList<TimelineElementV23> AARTimelineElementsMultiRecipient = notificationDTOMultiRecipient.getTimeline().stream().filter(
                t -> t.getCategory() == TimelineElementCategoryV23.AAR_GENERATION
        ).collect(Collectors.toCollection(ArrayList::new));

        calculatedParsedNotification = NotificationDetailMapper.modelMapper.mapNotificationDetail(notificationDTOMultiRecipient);

        assert calculatedParsedNotification.getOtherDocuments().size() == AARTimelineElementsMultiRecipient.size();

        int index = 0;
        for (TimelineElementV23 element : AARTimelineElementsMultiRecipient) {
            assert calculatedParsedNotification.getOtherDocuments().get(index).equals(
                    new NotificationDetailDocument()
                            .recIndex(element.getDetails().getRecIndex())
                            .documentId(element.getDetails().getGeneratedAarUrl())
                            .documentType("AAR")
                            .title("Avviso di avvenuta ricezione - " +
                                    notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getDenomination() +
                                    "(" + notificationDTOMultiRecipient.getRecipients().get(element.getDetails().getRecIndex()).getTaxId() + ")"
                            )
                            .digests(new NotificationAttachmentDigests().sha256(""))
                            .ref(new NotificationAttachmentBodyRef().key("").versionToken(""))
                            .contentType("")
            );
            index++;
        }
    }
}