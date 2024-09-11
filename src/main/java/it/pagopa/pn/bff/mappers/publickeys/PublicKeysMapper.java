//package it.pagopa.pn.bff.mappers.publickeys;
//
//import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
//import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeysResponse;
//import org.mapstruct.Context;
//import org.mapstruct.Mapper;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//
// /**
// * Mapstruct mapper interface, used to map the PublicKeysResponse
// * to the BffPublicKeysResponse
// */
//@Mapper
//public interface PublicKeysMapper {
//    PublicKeysMapper modelMapper = Mappers.getMapper(PublicKeysMapper.class);
//
//    /**
//     * Maps a PublicKeysResponse to a BffPublicKeysResponse
//     *
//     * @param publicKeysResponse the PubliceysResponse to map
//     * @param paGroups        groups retrieved from selfcare and linked to current Public Administration and user
//     * @return the mapped BffApiKeysResponse
//     */
//    BffPublicKeysResponse mapPublicKeysResponse(PublicKeysResponse publicKeyResponse, @Context List<PgGroup> pgGroups);
//}