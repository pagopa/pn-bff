package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.SearchMandateRequestDto;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.SearchMandateResponseDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateResponse;
import it.pagopa.pn.bff.mocks.MandateMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchMandateByDelegateMapperTest {
    private final MandateMock mandateMock = new MandateMock();

    @Test
    void testSearchMandatesByDelegateRequestMapper() {
        BffSearchMandateRequest requestToMap = mandateMock.getBffSearchMandatesByDelegateRequestMock();
        SearchMandateRequestDto request = SearchMandateByDelegateMapper.modelMapper.mapRequest(requestToMap);
        assertNotNull(request);
        assertEquals(request.getTaxId(), requestToMap.getTaxId());
        assertEquals(request.getGroups(), requestToMap.getGroups());
        assertEquals(request.getStatus(), requestToMap.getStatus());
        SearchMandateRequestDto requestNull = SearchMandateByDelegateMapper.modelMapper.mapRequest(null);
        assertNull(requestNull);
    }

    @Test
    void testSearchMandatesByDelegateResponseMapper() {
        SearchMandateResponseDto responseToMap = mandateMock.getSearchMandatesByDelegateResponseMock();
        BffSearchMandateResponse response = SearchMandateByDelegateMapper.modelMapper.mapResponse(responseToMap);
        assertNotNull(response);
        assertEquals(response.getMoreResult(), responseToMap.getMoreResult());
        assertEquals(response.getNextPagesKey(), responseToMap.getNextPagesKey());
        assertEquals(response.getResultsPage().size(), responseToMap.getResultsPage().size());
        for (int i = 0; i < response.getResultsPage().size(); i++) {
            assertEquals(response.getResultsPage().get(i).getDatefrom(), responseToMap.getResultsPage().get(i).getDatefrom());
            assertEquals(response.getResultsPage().get(i).getDateto(), responseToMap.getResultsPage().get(i).getDateto());
            assertEquals(response.getResultsPage().get(i).getMandateId(), responseToMap.getResultsPage().get(i).getMandateId());
            assertEquals(response.getResultsPage().get(i).getVerificationCode(), responseToMap.getResultsPage().get(i).getVerificationCode());
            assertEquals(response.getResultsPage().get(i).getStatus().getValue(), responseToMap.getResultsPage().get(i).getStatus().getValue());
            assertThat(response.getResultsPage().get(i).getGroups()).usingRecursiveComparison().isEqualTo(responseToMap.getResultsPage().get(i).getGroups());
            assertThat(response.getResultsPage().get(i).getDelegate()).usingRecursiveComparison().isEqualTo(responseToMap.getResultsPage().get(i).getDelegate());
            assertThat(response.getResultsPage().get(i).getVisibilityIds()).usingRecursiveComparison().isEqualTo(responseToMap.getResultsPage().get(i).getVisibilityIds());
        }
        BffSearchMandateResponse responseNull = SearchMandateByDelegateMapper.modelMapper.mapResponse(null);
        assertNull(responseNull);
    }
}