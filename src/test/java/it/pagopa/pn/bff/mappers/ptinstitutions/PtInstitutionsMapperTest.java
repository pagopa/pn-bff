package it.pagopa.pn.bff.mappers.ptinstitutions;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtInstitution;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtInstitutionsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtInstitutionsResponse;
import it.pagopa.pn.bff.mocks.PtInstitutionsMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class PtInstitutionsMapperTest {
    private final PtInstitutionsMock ptInstitutionsMock = new PtInstitutionsMock();

    @Test
    void testToBffPtInstitutionsResponse() {
        PtInstitutionsResponse response = ptInstitutionsMock.getPtInstitutionsResponseMock();

        BffPtInstitutionsResponse bffResponse = PtInstitutionsMapper.modelMapper.toBffPtInstitutionsResponse(response);

        assertNotNull(bffResponse);
        assertEquals(response.getLastKey(), bffResponse.getLastKey());
        assertEquals(response.getItems().size(), bffResponse.getItems().size());
        for (int i = 0; i < response.getItems().size(); i++) {
            PtInstitution item = response.getItems().get(i);
            BffPtInstitution bffItem = bffResponse.getItems().get(i);
            assertEquals(item.getInstitutionId(), bffItem.getInstitutionId());
            assertEquals(item.getInstitutionName(), bffItem.getInstitutionName());
            assertEquals(item.getInstitutionTaxCode(), bffItem.getInstitutionTaxCode());
            assertEquals(item.getDelegationId(), bffItem.getDelegationId());
            assertEquals(item.getStatus().getValue(), bffItem.getStatus().getValue());
            assertEquals(item.getStatusChangedAt(), bffItem.getStatusChangedAt());
        }

        assertNull(PtInstitutionsMapper.modelMapper.toBffPtInstitutionsResponse(null));
    }

    @ParameterizedTest
    @EnumSource(BffPtDelegationStatus.class)
    void testToPtDelegationStatus(BffPtDelegationStatus status) {
        PtDelegationStatus result = PtInstitutionsMapper.modelMapper.toPtDelegationStatus(status);

        assertNotNull(result);
        assertEquals(status.getValue(), result.getValue());
    }

    @Test
    void testToPtDelegationStatusNull() {
        assertNull(PtInstitutionsMapper.modelMapper.toPtDelegationStatus(null));
    }
}