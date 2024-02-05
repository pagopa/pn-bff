package it.pagopa.pn.bff.utils;

public class PnBffRestConstants {

    private PnBffRestConstants() {
    }

    public static final String CX_ID_HEADER = "x-pagopa-pn-cx-id";
    public static final String UID_HEADER = "x-pagopa-pn-uid";
    public static final String CX_TYPE_HEADER = "x-pagopa-pn-cx-type";
    public static final String CX_GROUPS_HEADER = "x-pagopa-pn-cx-groups";

    private static final String BFF_PATH = "bff";

    public static final String NOTIFICATION_RECEIVED_PATH = BFF_PATH + "/notifications/received/{iun}";

}
