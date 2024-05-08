package it.pagopa.pn.bff.utils;

public class PnBffRestConstants {

    public static final String CX_ID_HEADER = "x-pagopa-pn-cx-id";
    public static final String UID_HEADER = "x-pagopa-pn-uid";
    public static final String CX_TYPE_HEADER = "x-pagopa-pn-cx-type";
    public static final String CX_GROUPS_HEADER = "x-pagopa-pn-cx-groups";
    public static final String SOURCECHANNEL_HEADER = "x-pagopa-pn-src-ch";
    private static final String BFF_PATH = "/bff";
    private static final String VERSION_1 = "/v1";
    public static final String NOTIFICATIONS_RECEIVED_PATH = BFF_PATH + VERSION_1 + "/notifications/received";
    public static final String NOTIFICATIONS_RECEIVED_DELEGATED_PATH = NOTIFICATIONS_RECEIVED_PATH + "/delegated";
    public static final String NOTIFICATION_RECEIVED_PATH = NOTIFICATIONS_RECEIVED_PATH + "/{iun}";
    public static final String NOTIFICATION_RECEIVED_DOCUMENT_PATH = NOTIFICATION_RECEIVED_PATH + "/documents";
    public static final String NOTIFICATIONS_SENT_PATH = BFF_PATH + VERSION_1 + "/notifications/sent";
    public static final String NOTIFICATION_SENT_PATH = NOTIFICATIONS_SENT_PATH + "/{iun}";
    public static final String NOTIFICATION_SENT_DOCUMENT_PATH = NOTIFICATION_SENT_PATH + "/documents";
    public static final String INSTITUTIONS_PATH = BFF_PATH + VERSION_1 + "/institutions";
    public static final String APIKEYS_PATH = BFF_PATH + VERSION_1 + "/api-keys";
    public static final String TOS_PRIVACY_PATH = BFF_PATH + VERSION_1 + "/tos-privacy";
    public static final String DOWNTIME_LOGS_PATH = BFF_PATH + VERSION_1 + "/downtime";
    public static final String TOKEN_EXCHANGE_PATH = BFF_PATH + VERSION_1 + "/token-exchange";
    public static final String PAYMENTS_INFO_PATH = BFF_PATH + VERSION_1 + "/payments/info";
    public static final String PAYMENTS_CART_PATH = BFF_PATH + VERSION_1 + "/payments/cart";

    private PnBffRestConstants() {
    }
}