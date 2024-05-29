package it.pagopa.pn.bff.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PnBffExceptionTest {
    @Test
    void testConstructor() {
        PnBffException pnBffException = new PnBffException(
                "An error occurred",
                "This is the description of the error",
                HttpStatus.NOT_FOUND.value(),
                ERROR_CODE_PN_GENERIC_ERROR,
                "This a detailed description of the error",
                new Throwable("Error")
        );

        assertEquals(404, pnBffException.getStatus());
        assertEquals("An error occurred", pnBffException.getProblem().getTitle());
        assertEquals(404, pnBffException.getProblem().getStatus().intValue());
        assertEquals("GENERIC_ERROR", pnBffException.getProblem().getType());
        assertEquals("This is the description of the error", pnBffException.getProblem().getDetail());
        assertEquals("PN_GENERIC_ERROR", pnBffException.getProblem().getErrors().get(0).getCode());
        assertEquals("This a detailed description of the error", pnBffException.getProblem().getErrors().get(0).getDetail());
    }
}