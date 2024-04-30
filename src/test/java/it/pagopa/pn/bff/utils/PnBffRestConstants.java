package it.pagopa.pn.bff.utils;

public class PnBffRestConstants {

    public static final String CX_ID_HEADER = "x-pagopa-pn-cx-id";
    public static final String UID_HEADER = "x-pagopa-pn-uid";
    public static final String CX_TYPE_HEADER = "x-pagopa-pn-cx-type";
    public static final String CX_GROUPS_HEADER = "x-pagopa-pn-cx-groups";
    public static final String SOURCECHANNEL_HEADER = "x-pagopa-pn-src-ch";
    private static final String BFF_PATH = "/bff";
    private static final String VERSION_1 = "/v1";
    public static final String NOTIFICATION_RECEIVED_PATH = BFF_PATH + VERSION_1 + "/notifications/received/{iun}";
    public static final String NOTIFICATION_SENT_PATH = BFF_PATH + VERSION_1 + "/notifications/sent/{iun}";
    public static final String NOTIFICATION_SEARCH_PATH = BFF_PATH + VERSION_1 + "/notifications/sent";
    public static final String INSTITUTIONS_PATH = BFF_PATH + VERSION_1 + "/institutions";
    public static final String APIKEYS_PATH = BFF_PATH + VERSION_1 + "/api-keys";
    public static final String TOS_PRIVACY_PATH = BFF_PATH + VERSION_1 + "/tos-privacy";
    public static final String DOWNTIME_LOGS_PATH = BFF_PATH + VERSION_1 + "/downtime";

    private PnBffRestConstants() {
    }
}