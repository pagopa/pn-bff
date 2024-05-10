package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateCountsDto;

public class MandateMock {

    public MandateCountsDto getCountMock() {
        MandateCountsDto count = new MandateCountsDto();
        count.setValue(10);
        return count;
    }
}