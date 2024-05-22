package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RequestUtilityTest {

    @Test
    void testCheckCxIdIsValid() {
        Assertions.assertFalse(RequestUtility.checkCxIdIsValid(null));
        Assertions.assertFalse(RequestUtility.checkCxIdIsValid(List.of("")));
        Assertions.assertTrue(RequestUtility.checkCxIdIsValid(List.of("test")));
    }

    @Test
    void testCheckCxType() {
        Assertions.assertFalse(RequestUtility.checkCxTypeIsValid(null, List.of(CxTypeAuthFleet.PF)));
        Assertions.assertFalse(RequestUtility.checkCxTypeIsValid(List.of("PF"), List.of()));
        Assertions.assertFalse(RequestUtility.checkCxTypeIsValid(List.of("PF"), List.of(CxTypeAuthFleet.PG)));
        Assertions.assertTrue(RequestUtility.checkCxTypeIsValid(List.of("PF"), List.of(CxTypeAuthFleet.PF))
        );
    }
}
