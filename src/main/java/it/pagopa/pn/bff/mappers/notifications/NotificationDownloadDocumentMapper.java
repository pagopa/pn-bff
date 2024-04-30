package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffDocumentDownloadMetadataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the DocumentDownloadMetadataResponse, LegalFactDownloadMetadataResponse and
 * NotificationAttachmentDownloadMetadataResponse to the BffDocumentDownloadMetadataResponse
 */
@Mapper()
public interface NotificationDownloadDocumentMapper {
    // Instance of the mapper
    NotificationDownloadDocumentMapper modelMapper = Mappers.getMapper(NotificationDownloadDocumentMapper.class);

    /**
     * Maps a DocumentDownloadMetadataResponse to a BffDocumentDownloadMetadataResponse
     *
     * @param document the DocumentDownloadMetadataResponse to map
     * @return the mapped BffDocumentDownloadMetadataResponse
     */
    BffDocumentDownloadMetadataResponse mapDocumentDownloadResponse(DocumentDownloadMetadataResponse document);

    /**
     * Maps a LegalFactDownloadMetadataResponse to a BffDocumentDownloadMetadataResponse
     *
     * @param document the LegalFactDownloadMetadataResponse to map
     * @return the mapped BffDocumentDownloadMetadataResponse
     */
    BffDocumentDownloadMetadataResponse mapLegalFactDownloadResponse(LegalFactDownloadMetadataResponse document);

    /**
     * Maps a NotificationAttachmentDownloadMetadataResponse to a BffDocumentDownloadMetadataResponse
     *
     * @param document the NotificationAttachmentDownloadMetadataResponse to map
     * @return the mapped BffDocumentDownloadMetadataResponse
     */
    BffDocumentDownloadMetadataResponse mapSentAttachmentDownloadResponse(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.NotificationAttachmentDownloadMetadataResponse document);

    /**
     * Maps a NotificationAttachmentDownloadMetadataResponse to a BffDocumentDownloadMetadataResponse
     *
     * @param document the NotificationAttachmentDownloadMetadataResponse to map
     * @return the mapped BffDocumentDownloadMetadataResponse
     */
    BffDocumentDownloadMetadataResponse mapReceivedAttachmentDownloadResponse(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse document);

}