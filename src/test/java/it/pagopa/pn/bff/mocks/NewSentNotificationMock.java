package it.pagopa.pn.bff.mocks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNewNotificationRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPreLoadRequest;

import java.util.ArrayList;
import java.util.List;

public class NewSentNotificationMock {

    private final ObjectMapper mapper = new ObjectMapper();

    public NewSentNotificationMock() {
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private NotificationDocument getDocumentMock(int index) {
        NotificationDocument document = new NotificationDocument();
        document.setTitle("Doc " + index);
        document.setContentType("application/pdf");
        document.setDocIdx(String.valueOf(index));
        NotificationAttachmentBodyRef ref = new NotificationAttachmentBodyRef();
        ref.setKey("ref-key-" + index);
        ref.setVersionToken("version-token-" + index);
        document.setRef(ref);
        NotificationAttachmentDigests digests = new NotificationAttachmentDigests();
        digests.setSha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlE" + index);
        document.setDigests(digests);
        return document;
    }

    private NotificationRecipientV23 getRecipientMock(int index, NotificationRecipientV23.RecipientTypeEnum recipientType) {
        NotificationRecipientV23 recipient = new NotificationRecipientV23();
        recipient.setRecipientType(recipientType);
        recipient.setDenomination("Recipient " + index);
        recipient.setTaxId("LVLDAA85T50G70" + index + "B");
        NotificationDigitalAddress digitalDomicile = new NotificationDigitalAddress();
        digitalDomicile.setType(NotificationDigitalAddress.TypeEnum.PEC);
        digitalDomicile.setAddress("recipient-" + index + "@pec.it");
        recipient.setDigitalDomicile(digitalDomicile);
        NotificationPhysicalAddress physicalAddress = new NotificationPhysicalAddress();
        physicalAddress.setProvince("RM");
        physicalAddress.setAddress("Via delle vie");
        physicalAddress.setMunicipality("Rome");
        physicalAddress.setZip("0012" + index);
        physicalAddress.setAt("3" + index);
        physicalAddress.setForeignState("Italia");
        recipient.setPhysicalAddress(physicalAddress);
        return recipient;
    }

    private NotificationPaymentItem getPaymentMock(int recIndex, int index, Boolean hasPagoPa, Boolean hasF24) {
        NotificationPaymentItem payment = new NotificationPaymentItem();
        // pago pa payment
        if (hasPagoPa) {
            PagoPaPayment pagoPa = new PagoPaPayment();
            pagoPa.setApplyCost(index == 0);
            pagoPa.setCreditorTaxId("77777777777");
            pagoPa.setNoticeCode("3020001000000194" + index + recIndex);
            NotificationPaymentAttachment attachment = new NotificationPaymentAttachment();
            attachment.setContentType("application/pdf");
            NotificationAttachmentBodyRef ref = new NotificationAttachmentBodyRef();
            ref.setKey("ref-key-" + index + "_recIndex-" + recIndex);
            ref.setVersionToken("version-token-" + index + "_recIndex-" + recIndex);
            attachment.setRef(ref);
            NotificationAttachmentDigests digests = new NotificationAttachmentDigests();
            digests.setSha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RM" + index + "recIndex" + recIndex);
            attachment.setDigests(digests);
            pagoPa.setAttachment(attachment);
            payment.setPagoPa(pagoPa);
        }
        // f24 payment
        if (hasF24) {
            F24Payment f24 = new F24Payment();
            f24.setApplyCost(index == 0);
            f24.setTitle("F24 payment " + index + " for recipient " + recIndex);
            NotificationMetadataAttachment metadataAttachment = new NotificationMetadataAttachment();
            metadataAttachment.setContentType("application/json");
            NotificationAttachmentBodyRef ref = new NotificationAttachmentBodyRef();
            ref.setKey("ref-key-" + index + "_recIndex-" + recIndex);
            ref.setVersionToken("version-token-" + index + "_recIndex-" + recIndex);
            metadataAttachment.setRef(ref);
            NotificationAttachmentDigests digests = new NotificationAttachmentDigests();
            digests.setSha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RM" + index + "recIndex" + recIndex);
            metadataAttachment.setDigests(digests);
            f24.setMetadataAttachment(metadataAttachment);
            payment.setF24(f24);
        }
        return payment;
    }

    public NewNotificationRequestV23 getNewSentNotificationRequest() {
        NewNotificationRequestV23 request = new NewNotificationRequestV23();
        request.setAbstract("Description of mocked notification");
        request.setSubject("Title of mocked notification");
        request.setAmount(999);
        request.setSenderDenomination("Mocked sender");
        request.setGroup("group-1");
        request.setIdempotenceToken("idempotence-token");
        request.setNotificationFeePolicy(NotificationFeePolicy.DELIVERY_MODE);
        request.setPaFee(99);
        request.setVat(9);
        request.setPagoPaIntMode(NewNotificationRequestV23.PagoPaIntModeEnum.NONE);
        request.setPaymentExpirationDate("2024-11-23");
        request.setPaProtocolNumber("12345678910");
        request.setSenderTaxId("77777777777");
        request.setTaxonomyCode("010801N");
        request.setPhysicalCommunicationType(NewNotificationRequestV23.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER);
        List<NotificationDocument> documents = new ArrayList<>();
        documents.add(getDocumentMock(0));
        documents.add(getDocumentMock(1));
        request.setDocuments(documents);
        List<NotificationRecipientV23> recipients = new ArrayList<>();
        NotificationRecipientV23 recipient1 = getRecipientMock(0, NotificationRecipientV23.RecipientTypeEnum.PF);
        List<NotificationPaymentItem> payments1 = new ArrayList<>();
        payments1.add(getPaymentMock(0, 0, true, true));
        payments1.add(getPaymentMock(0, 1, true, true));
        recipient1.setPayments(payments1);
        NotificationRecipientV23 recipient2 = getRecipientMock(1, NotificationRecipientV23.RecipientTypeEnum.PG);
        List<NotificationPaymentItem> payments2 = new ArrayList<>();
        payments2.add(getPaymentMock(1, 0, false, true));
        payments2.add(getPaymentMock(1, 1, false, true));
        recipient2.setPayments(payments2);
        NotificationRecipientV23 recipient3 = getRecipientMock(2, NotificationRecipientV23.RecipientTypeEnum.PF);
        List<NotificationPaymentItem> payments3 = new ArrayList<>();
        payments3.add(getPaymentMock(2, 0, true, false));
        payments3.add(getPaymentMock(2, 1, false, true));
        recipient3.setPayments(payments3);
        recipients.add(recipient1);
        recipients.add(recipient2);
        recipients.add(recipient3);
        request.setRecipients(recipients);
        return request;
    }

    public BffNewNotificationRequest getBffNewSentNotificationRequest() {
        NewNotificationRequestV23 msRequest = getNewSentNotificationRequest();
        return mapper.convertValue(msRequest, BffNewNotificationRequest.class);
    }

    public NewNotificationResponse getNewSentNotificationResponse() {
        NewNotificationResponse response = new NewNotificationResponse();
        response.setIdempotenceToken("idempotence-token");
        response.setNotificationRequestId("RVBRTi1OVExOLUtUS0otMjAyNDA1LUotMQ==");
        response.setPaProtocolNumber("12345678910");
        return response;
    }

    public List<PreLoadRequest> getPreloadRequestMock() {
        List<PreLoadRequest> preLoadRequests = new ArrayList<>();
        PreLoadRequest preLoadRequest = new PreLoadRequest();
        preLoadRequest.setContentType("application/pdf");
        preLoadRequest.setSha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlE0");
        preLoadRequests.add(preLoadRequest);
        preLoadRequest.setContentType("application/pdf");
        preLoadRequest.setSha256("jezIVxlG1M1woCSUngM6KipUN3/p8cG5RMIPnuEanlE1");
        preLoadRequests.add(preLoadRequest);
        return preLoadRequests;
    }

    public List<BffPreLoadRequest> getBffPreloadRequestMock() {
        List<BffPreLoadRequest> preLoadRequests = new ArrayList<>();
        for (PreLoadRequest msRequest : getPreloadRequestMock()) {
            preLoadRequests.add(mapper.convertValue(msRequest, BffPreLoadRequest.class));
        }
        return preLoadRequests;
    }

    public List<PreLoadResponse> getPreloadResponseMock() {
        List<PreLoadResponse> preLoadResponses = new ArrayList<>();
        PreLoadResponse preLoadResponse = new PreLoadResponse();
        preLoadResponse.setKey("key-0");
        preLoadResponse.setUrl("https://url-0.com");
        preLoadResponse.setHttpMethod(PreLoadResponse.HttpMethodEnum.POST);
        preLoadResponse.setSecret("secret-0");
        preLoadResponses.add(preLoadResponse);
        preLoadResponse.setKey("key-1");
        preLoadResponse.setUrl("https://url-1.com");
        preLoadResponse.setHttpMethod(PreLoadResponse.HttpMethodEnum.POST);
        preLoadResponse.setSecret("secret-1");
        preLoadResponses.add(preLoadResponse);
        return preLoadResponses;
    }
}