package it.pagopa.pn.bff.utils;

public class PnBffRestConstants {

    public static final String CX_ID_HEADER = "x-pagopa-pn-cx-id";
    public static final String UID_HEADER = "x-pagopa-pn-uid";
    public static final String CX_TYPE_HEADER = "x-pagopa-pn-cx-type";
    public static final String CX_GROUPS_HEADER = "x-pagopa-pn-cx-groups";
    public static final String CX_ROLE_HEADER = "x-pagopa-pn-cx-role";
    public static final String SOURCE_CHANNEL_HEADER = "x-pagopa-pn-src-ch";
    public static final String SOURCE_CHANNEL_DETAILS_HEADER = "x-pagopa-pn-src-ch-details";
    private static final String BFF_PATH = "/bff";
    private static final String VERSION_1 = "/v1";
    public static final String NOTIFICATIONS_RECEIVED_PATH = BFF_PATH + VERSION_1 + "/notifications/received";
    public static final String NOTIFICATION_AAR_QR_CODE_PATH = NOTIFICATIONS_RECEIVED_PATH + "/check-aar-qr-code";
    public static final String NOTIFICATION_RETRIEVAL_ID_PATH = NOTIFICATIONS_RECEIVED_PATH + "/check-tpp";
    public static final String NOTIFICATIONS_RECEIVED_DELEGATED_PATH = NOTIFICATIONS_RECEIVED_PATH + "/delegated";
    public static final String NOTIFICATION_RECEIVED_PATH = NOTIFICATIONS_RECEIVED_PATH + "/{iun}";
    public static final String NOTIFICATION_RECEIVED_DOCUMENT_PATH = NOTIFICATION_RECEIVED_PATH + "/documents/{documentType}";
    public static final String NOTIFICATION_RECEIVED_PAYMENT_PATH = NOTIFICATION_RECEIVED_PATH + "/payments/{attachmentName}";
    public static final String NOTIFICATIONS_SENT_PATH = BFF_PATH + VERSION_1 + "/notifications/sent";
    public static final String NOTIFICATION_SENT_PATH = NOTIFICATIONS_SENT_PATH + "/{iun}";
    public static final String NOTIFICATION_SENT_DOCUMENT_PATH = NOTIFICATION_SENT_PATH + "/documents/{documentType}";
    public static final String NOTIFICATION_SENT_PAYMENT_PATH = NOTIFICATION_SENT_PATH + "/payments/{recipientIdx}/{attachmentName}";
    public static final String NOTIFICATION_SENT_CANCEL_PATH = NOTIFICATION_SENT_PATH + "/cancel";
    public static final String NOTIFICATION_SENT_PRELOAD_PATH = NOTIFICATIONS_SENT_PATH + "/documents/preload";
    public static final String GROUPS_PA_PATH = BFF_PATH + VERSION_1 + "/pa/groups";
    public static final String PA_LIST = BFF_PATH + VERSION_1 + "/pa-list";
    public static final String INSTITUTIONS_PATH = BFF_PATH + VERSION_1 + "/institutions";
    public static final String APIKEYS_PATH = BFF_PATH + VERSION_1 + "/api-keys";
    public static final String DOWNTIME_LOGS_PATH = BFF_PATH + VERSION_1 + "/downtime";
    public static final String PAYMENTS_INFO_PATH = BFF_PATH + VERSION_1 + "/payments/info";
    public static final String PAYMENTS_CART_PATH = BFF_PATH + VERSION_1 + "/payments/cart";
    public static final String PAYMENTS_TPP_PATH = BFF_PATH + VERSION_1 + "/payments/tpp";
    public static final String ADDRESSES_PATH = BFF_PATH + VERSION_1 + "/addresses";
    public static final String CREATE_DELETE_ADDRESS_PATH = ADDRESSES_PATH + "/{addressType}/{senderId}/{channelType}";
    public static final String MANDATE_PATH = BFF_PATH + VERSION_1 + "/mandate";
    public static final String SENDER_DASHBOARD_GET_DATA_PATH
            = BFF_PATH + VERSION_1 + "/sender-dashboard/dashboard-data-request/{cxType}/{cxId}";
    private static final String PG = "/pg";
    public static final String VIRTUALKEYS_PATH = BFF_PATH + VERSION_1 + PG + "/virtual-keys";
    public static final String GROUPS_PG_PATH = BFF_PATH + VERSION_1 + PG + "/groups";
    public static final String PUBLIC_KEYS_PATH = BFF_PATH + VERSION_1 + PG + "/public-keys";
    public static final String PUBLIC_KEYS_ROTATE_PATH = BFF_PATH + VERSION_1 + PG + "/public-keys/{kid}/rotate";
    public static final String PUBLIC_KEYS_STATUS_PATH = BFF_PATH + VERSION_1 + PG + "/public-keys/{kid}/status";
    public static final String PUBLIC_KEYS_CHECK_ISSUER_PATH = BFF_PATH + VERSION_1 + PG + "/public-keys/check-issuer";
    public static final String VIRTUAL_KEYS_PATH = BFF_PATH + VERSION_1 + PG + "/virtual-keys";
    public static final String TOS_PG_PRIVACY_PATH = BFF_PATH + VERSION_1 + PG + "/tos-privacy";
    private static final String VERSION_2 = "/v2";
    public static final String TOS_PRIVACY_PATH = BFF_PATH + VERSION_2 + "/tos-privacy";
    public static final String ADDITIONAL_LANGUAGES_PATH = BFF_PATH + VERSION_1 + "/pa/additional-languages";

    private PnBffRestConstants() {
    }
}