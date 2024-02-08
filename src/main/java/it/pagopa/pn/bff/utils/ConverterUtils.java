package it.pagopa.pn.bff.utils;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;

public class ConverterUtils {

    public static it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet convertRecipientCXType(CxTypeAuthFleet cxType) {
        return it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.valueOf(cxType.getValue());
    }

    public static it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet convertPACXType(CxTypeAuthFleet cxType) {
        return it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet.valueOf(cxType.getValue());
    }
}
