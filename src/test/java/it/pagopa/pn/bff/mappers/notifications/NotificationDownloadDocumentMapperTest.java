package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDownloadDocumentMapperTest {
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();

    @Test
    void testDocumentDownloadMetadataResponseMapper() {
        DocumentDownloadMetadataResponse documentMock = notificationDownloadDocumentMock.getDocumentMock();

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = NotificationDownloadDocumentMapper.modelMapper.mapDocumentDownloadResponse(documentMock);
        assertNotNull(bffDocumentDownloadMetadataResponse);
        assertEquals(bffDocumentDownloadMetadataResponse.getFilename(), documentMock.getFilename());
        assertEquals(bffDocumentDownloadMetadataResponse.getContentLength(), documentMock.getContentLength());
        assertEquals(bffDocumentDownloadMetadataResponse.getUrl(), documentMock.getUrl());
        assertEquals(bffDocumentDownloadMetadataResponse.getRetryAfter(), documentMock.getRetryAfter());

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponseNull = NotificationDownloadDocumentMapper.modelMapper.mapDocumentDownloadResponse(null);
        assertNull(bffDocumentDownloadMetadataResponseNull);
    }

    @Test
    void testLegalFactDownloadMetadataResponseMapper() {
        LegalFactDownloadMetadataResponse legalFactMock = notificationDownloadDocumentMock.getLegalFactMock();

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = NotificationDownloadDocumentMapper.modelMapper.mapLegalFactDownloadResponse(legalFactMock);
        assertNotNull(bffDocumentDownloadMetadataResponse);
        assertEquals(bffDocumentDownloadMetadataResponse.getFilename(), legalFactMock.getFilename());
        assertEquals(bffDocumentDownloadMetadataResponse.getContentLength(), legalFactMock.getContentLength());
        assertEquals(bffDocumentDownloadMetadataResponse.getUrl(), legalFactMock.getUrl());
        assertEquals(bffDocumentDownloadMetadataResponse.getRetryAfter(), legalFactMock.getRetryAfter());

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponseNull = NotificationDownloadDocumentMapper.modelMapper.mapLegalFactDownloadResponse(null);
        assertNull(bffDocumentDownloadMetadataResponseNull);
    }

    @Test
    void testSentDocumentNotificationAttachmentDownloadMetadataResponseMapper() {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationAttachmentDownloadMetadataResponse attachmentMock = notificationDownloadDocumentMock.getPaAttachmentMock();

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = NotificationDownloadDocumentMapper.modelMapper.mapSentAttachmentDownloadResponse(attachmentMock);
        assertNotNull(bffDocumentDownloadMetadataResponse);
        assertEquals(bffDocumentDownloadMetadataResponse.getFilename(), attachmentMock.getFilename());
        assertEquals(bffDocumentDownloadMetadataResponse.getContentLength(), BigDecimal.valueOf(attachmentMock.getContentLength()));
        assertEquals(bffDocumentDownloadMetadataResponse.getUrl(), attachmentMock.getUrl());
        assertEquals(bffDocumentDownloadMetadataResponse.getRetryAfter(), attachmentMock.getRetryAfter() != null ? BigDecimal.valueOf(attachmentMock.getRetryAfter()) : null);

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponseNull = NotificationDownloadDocumentMapper.modelMapper.mapSentAttachmentDownloadResponse(null);
        assertNull(bffDocumentDownloadMetadataResponseNull);
    }

    @Test
    void testReceivedDocumentNotificationAttachmentDownloadMetadataResponseMapper() {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse attachmentMock = notificationDownloadDocumentMock.getRecipientAttachmentMock();

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = NotificationDownloadDocumentMapper.modelMapper.mapReceivedAttachmentDownloadResponse(attachmentMock);
        assertNotNull(bffDocumentDownloadMetadataResponse);
        assertEquals(bffDocumentDownloadMetadataResponse.getFilename(), attachmentMock.getFilename());
        assertEquals(bffDocumentDownloadMetadataResponse.getContentLength(), BigDecimal.valueOf(attachmentMock.getContentLength()));
        assertEquals(bffDocumentDownloadMetadataResponse.getUrl(), attachmentMock.getUrl());
        assertEquals(bffDocumentDownloadMetadataResponse.getRetryAfter(), attachmentMock.getRetryAfter() != null ? BigDecimal.valueOf(attachmentMock.getRetryAfter()) : null);

        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponseNull = NotificationDownloadDocumentMapper.modelMapper.mapReceivedAttachmentDownloadResponse(null);
        assertNull(bffDocumentDownloadMetadataResponseNull);
    }
}