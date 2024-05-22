package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PreLoadRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PreLoadResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPreLoadRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPreLoadResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapstruct mapper interface, used to map the BffPreLoadRequest to the PreLoadRequest,
 * and the PreLoadResponse the BffPreLoadResponse
 */
@Mapper
public interface NotificationSentPreloadDocumentsMapper {
    // Instance of the mapper
    NotificationSentPreloadDocumentsMapper modelMapper = Mappers.getMapper(NotificationSentPreloadDocumentsMapper.class);

    /**
     * Maps a BffPreLoadRequest to a PreLoadRequest
     *
     * @param request the BffPreLoadRequest to map
     * @return the mapped PreLoadRequest
     */
    List<PreLoadRequest> mapRequest(List<BffPreLoadRequest> request);

    /**
     * Maps a PreLoadResponse to a BffPreLoadResponse
     *
     * @param response the PreLoadResponse to map
     * @return the mapped BffPreLoadResponse
     */
    BffPreLoadResponse mapResponse(PreLoadResponse response);
}