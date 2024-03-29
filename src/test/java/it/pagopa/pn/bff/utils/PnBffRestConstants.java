package it.pagopa.pn.bff.utils;

public class PnBffRestConstants {

    private PnBffRestConstants() {
    }

    public static final String CX_ID_HEADER = "x-pagopa-pn-cx-id";
    public static final String UID_HEADER = "x-pagopa-pn-uid";
    public static final String CX_TYPE_HEADER = "x-pagopa-pn-cx-type";
    public static final String CX_GROUPS_HEADER = "x-pagopa-pn-cx-groups";

    private static final String BFF_PATH = "/bff";
    private static final String VERSION_1 = "/v1";

    public static final String NOTIFICATION_RECEIVED_PATH = BFF_PATH + VERSION_1 + "/notifications/received/{iun}";
    public static final String NOTIFICATION_SENT_PATH = BFF_PATH + VERSION_1 + "/notifications/sent/{iun}";

}
