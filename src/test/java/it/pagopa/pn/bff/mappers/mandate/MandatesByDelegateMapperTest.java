package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffMandate;
import it.pagopa.pn.bff.mocks.MandateMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MandatesByDelegateMapperTest {
    private final MandateMock mandateMock = new MandateMock();

    @Test
    void testMandatesByDelegateMapper() {
        List<BffMandate> mandates = mandateMock.getMandatesByDelegateMock()
                .stream()
                .map(MandatesByDelegateMapper.modelMapper::mapMandate)
                .toList();
        assertNotNull(mandates);
        for (int i = 0; i < mandates.size(); i++) {
            assertEquals(mandates.get(i).getMandateId(), mandateMock.getMandatesByDelegateMock().get(i).getMandateId());
            assertEquals(mandates.get(i).getDatefrom(), mandateMock.getMandatesByDelegateMock().get(i).getDatefrom());
            assertEquals(mandates.get(i).getDateto(), mandateMock.getMandatesByDelegateMock().get(i).getDateto());
            assertEquals(mandates.get(i).getStatus().getValue(), mandateMock.getMandatesByDelegateMock().get(i).getStatus().getValue());
            assertThat(mandates.get(i).getGroups()).usingRecursiveComparison().isEqualTo(mandateMock.getMandatesByDelegateMock().get(i).getGroups());
            assertThat(mandates.get(i).getDelegate()).usingRecursiveComparison().isEqualTo(mandateMock.getMandatesByDelegateMock().get(i).getDelegate());
            assertEquals(mandates.get(i).getVerificationCode(), mandateMock.getMandatesByDelegateMock().get(i).getVerificationCode());
            assertThat(mandates.get(i).getVisibilityIds()).usingRecursiveComparison().isEqualTo(mandateMock.getMandatesByDelegateMock().get(i).getVisibilityIds());
        }
        BffMandate mandateNull = MandatesByDelegateMapper.modelMapper.mapMandate(null);
        assertNull(mandateNull);
    }
}