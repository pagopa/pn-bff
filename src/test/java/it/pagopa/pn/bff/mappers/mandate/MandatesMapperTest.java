package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.UserDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.BffMandate;
import it.pagopa.pn.bff.mocks.MandateMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MandatesMapperTest {
    private final MandateMock mandateMock = new MandateMock();

    @Test
    void testMandatesByDelegateMapper() {
        List<MandateDto> mandatesToMock = mandateMock.getMandatesByDelegateMock();
        UserDto delegate = new UserDto();
        delegate.setPerson(true);
        mandatesToMock.get(0).setDelegate(delegate);
        List<BffMandate> mandates = mandatesToMock
                .stream()
                .map(MandatesMapper.modelMapper::mapMandateByDelegate)
                .toList();
        assertNotNull(mandates);
        for (int i = 0; i < mandates.size(); i++) {
            assertEquals(mandates.get(i).getMandateId(), mandatesToMock.get(i).getMandateId());
            assertEquals(mandates.get(i).getDatefrom(), mandatesToMock.get(i).getDatefrom());
            assertEquals(mandates.get(i).getDateto(), mandatesToMock.get(i).getDateto());
            assertEquals(mandates.get(i).getStatus().getValue(), mandatesToMock.get(i).getStatus().getValue());
            assertThat(mandates.get(i).getGroups()).usingRecursiveComparison().isEqualTo(mandatesToMock.get(i).getGroups());
            assertThat(mandates.get(i).getDelegator()).usingRecursiveComparison().isEqualTo(mandatesToMock.get(i).getDelegator());
            assertEquals(mandates.get(i).getVerificationCode(), mandatesToMock.get(i).getVerificationCode());
            assertThat(mandates.get(i).getVisibilityIds()).usingRecursiveComparison().isEqualTo(mandatesToMock.get(i).getVisibilityIds());
            assertNull(mandates.get(i).getDelegate());
        }
        BffMandate mandateNull = MandatesMapper.modelMapper.mapMandateByDelegate(null);
        assertNull(mandateNull);
    }

    @Test
    void testMandatesByDelegatorMapper() {
        List<MandateDto> mandatesToMock = mandateMock.getMandatesByDelegatorMock();
        UserDto delegator = new UserDto();
        delegator.setPerson(true);
        mandatesToMock.get(0).setDelegator(delegator);
        List<BffMandate> mandates = mandatesToMock
                .stream()
                .map(MandatesMapper.modelMapper::mapMandateByDelegator)
                .toList();
        assertNotNull(mandates);
        for (int i = 0; i < mandates.size(); i++) {
            assertEquals(mandates.get(i).getMandateId(), mandatesToMock.get(i).getMandateId());
            assertEquals(mandates.get(i).getDatefrom(), mandatesToMock.get(i).getDatefrom());
            assertEquals(mandates.get(i).getDateto(), mandatesToMock.get(i).getDateto());
            assertEquals(mandates.get(i).getStatus().getValue(), mandatesToMock.get(i).getStatus().getValue());
            assertThat(mandates.get(i).getGroups()).usingRecursiveComparison().isEqualTo(mandatesToMock.get(i).getGroups());
            assertThat(mandates.get(i).getDelegate()).usingRecursiveComparison().isEqualTo(mandatesToMock.get(i).getDelegate());
            assertEquals(mandates.get(i).getVerificationCode(), mandatesToMock.get(i).getVerificationCode());
            assertThat(mandates.get(i).getVisibilityIds()).usingRecursiveComparison().isEqualTo(mandatesToMock.get(i).getVisibilityIds());
            assertNull(mandates.get(i).getDelegator());
        }
        BffMandate mandateNull = MandatesMapper.modelMapper.mapMandateByDelegator(null);
        assertNull(mandateNull);
    }
}