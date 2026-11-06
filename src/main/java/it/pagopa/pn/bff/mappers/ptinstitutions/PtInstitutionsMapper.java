package it.pagopa.pn.bff.mappers.ptinstitutions;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtInstitutionsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtInstitutionsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ext-registry PtInstitutionsResponse to the BffPtInstitutionsResponse,
 * and the BffPtDelegationStatus to the ext-registry PtDelegationStatus
 */
@Mapper
public interface PtInstitutionsMapper {
    PtInstitutionsMapper modelMapper = Mappers.getMapper(PtInstitutionsMapper.class);

    BffPtInstitutionsResponse toBffPtInstitutionsResponse(PtInstitutionsResponse ptInstitutionsResponse);

    PtDelegationStatus toPtDelegationStatus(BffPtDelegationStatus bffPtDelegationStatus);
}