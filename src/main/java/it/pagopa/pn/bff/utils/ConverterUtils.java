package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;

public class ConverterUtils {

    public it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet convertCXType(CxTypeAuthFleet cxType) {
        return it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.valueOf(cxType.getValue());
    }
}
