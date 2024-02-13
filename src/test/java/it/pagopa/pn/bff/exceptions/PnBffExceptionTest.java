package it.pagopa.pn.bff.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PnBffExceptionTest {
    @Test
    void testConstructor() {
        PnBffException pnBffException = new PnBffException(
                "An error occurred",
                "PN_GENERIC_ERROR",
                "An error occurred",
                404,
                "Element",
                new Throwable("Error")
        );
        
        assertEquals(404, pnBffException.getStatus());
        assertEquals("Element", pnBffException.getProblem().getTitle());
        assertEquals(404, pnBffException.getProblem().getStatus().intValue());
        assertEquals("GENERIC_ERROR", pnBffException.getProblem().getType());
        assertEquals("An error occurred", pnBffException.getProblem().getDetail());
        assertEquals("PN_GENERIC_ERROR", pnBffException.getProblem().getErrors().get(0).getCode());
        assertEquals("An error occurred", pnBffException.getProblem().getErrors().get(0).getDetail());
    }
}
