package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.ProblemError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProblemErrorMapperTest {
    @Test
    void testConvertPnCommonsProblemError() {
        List<ProblemError> problemErrors = new ArrayList<>();
        ProblemError problemError1 = new ProblemError();
        problemError1.setCode("PN_ERROR_1");
        problemError1.setDetail("This the detail of the error 1");
        ProblemError problemError2 = new ProblemError();
        problemError2.setCode("PN_ERROR_2");
        problemError2.setDetail("This the detail of the error 2");
        problemErrors.add(problemError1);
        problemErrors.add(problemError2);
        List<it.pagopa.pn.commons.exceptions.dto.ProblemError> pnCommonsProblemError = ProblemErrorMapper.modelMapper.convertPnCommonsProblemError(problemErrors);
        assertNotNull(pnCommonsProblemError);
        assertEquals(pnCommonsProblemError.size(), problemErrors.size());
        for (int i = 0; i < pnCommonsProblemError.size(); i++) {
            assertEquals(pnCommonsProblemError.get(i).getCode(), problemErrors.get(i).getCode());
            assertEquals(pnCommonsProblemError.get(i).getDetail(), problemErrors.get(i).getDetail());
        }
    }
}