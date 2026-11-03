package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtInstitution;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtInstitutionsResponse;

import java.time.OffsetDateTime;
import java.util.List;

public class PtInstitutionsMock {

    public PtInstitutionsResponse getPtInstitutionsResponseMock() {
        PtInstitution active = new PtInstitution()
                .institutionId("mock-institution-id-1")
                .institutionName("Ente Fittizio 01")
                .institutionTaxCode("99999999901")
                .delegationId("mock-delegation-id-1")
                .status(PtDelegationStatus.ACTIVE)
                .statusChangedAt(OffsetDateTime.parse("2026-01-01T09:30:00Z"));

        PtInstitution revoked = new PtInstitution()
                .institutionId("mock-institution-id-2")
                .institutionName("Ente Fittizio 02")
                .institutionTaxCode("99999999902")
                .delegationId("mock-delegation-id-2")
                .status(PtDelegationStatus.REVOKED)
                .statusChangedAt(OffsetDateTime.parse("2026-01-02T09:30:00Z"));

        return new PtInstitutionsResponse()
                .items(List.of(active, revoked))
                .lastKey("mock-last-key");
    }
}