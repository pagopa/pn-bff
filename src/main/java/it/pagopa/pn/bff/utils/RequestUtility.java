package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;

import java.util.List;

public class RequestUtility {
    public static boolean checkCxIdIsValid(List<String> cxId) {
        return cxId != null && !cxId.get(0).isEmpty();
    }

    public static boolean checkCxTypeIsValid(List<String> cxType, List<CxTypeAuthFleet> acceptedCxType) {
        return cxType != null && !cxType.isEmpty() && acceptedCxType.contains(CxTypeAuthFleet.valueOf(cxType.get(0)));
    }
}
