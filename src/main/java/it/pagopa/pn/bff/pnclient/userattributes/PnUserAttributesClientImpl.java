package it.pagopa.pn.bff.pnclient.userattributes;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.AllApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.CourtesyApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.LegalApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnUserAttributesClientImpl {

    private final ConsentsApi consentsApi;
    private final AllApi allAddressesApi;
    private final CourtesyApi courtesyApi;
    private final LegalApi legalApi;

    public Mono<Consent> getPgConsentByType(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, ConsentType type){
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getPgConsentByType");
        return consentsApi.getPgConsentByType( xPagopaPnUid,  xPagopaPnCxType,  type, null);
    }

    public Mono<Consent> getConsentByType(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, ConsentType type) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getConsentByType");
        return consentsApi.getConsentByType(xPagopaPnUid, xPagopaPnCxType, type, null);
    }

    public Mono<Void> acceptConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                    ConsentType consentType, ConsentAction consentAction, String version) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "acceptConsent");
        return consentsApi.consentAction(xPagopaPnUid, xPagopaPnCxType, consentType, version, consentAction);
    }

    public Mono<UserAddresses> getUserAddresses(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getAddressesByRecipient");
        return allAddressesApi.getAddressesByRecipient(xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole);
    }

    public Mono<AddressVerificationResponse> createOrUpdateCourtesyAddress(String xPagopaPnCxId,
                                                                           CxTypeAuthFleet xPagopaPnCxType,
                                                                           String senderId,
                                                                           CourtesyChannelType channelType,
                                                                           AddressVerification addressVerification,
                                                                           List<String> xPagopaPnCxGroups,
                                                                           String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "postRecipientCourtesyAddress");

        return courtesyApi.postRecipientCourtesyAddress(xPagopaPnCxId, xPagopaPnCxType, senderId,
                channelType, addressVerification, xPagopaPnCxGroups, xPagopaPnCxRole
        );
    }

    public Mono<AddressVerificationResponse> createOrUpdateLegalAddress(String xPagopaPnCxId,
                                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                                        String senderId,
                                                                        LegalChannelType channelType,
                                                                        AddressVerification addressVerification,
                                                                        List<String> xPagopaPnCxGroups,
                                                                        String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "postRecipientLegalAddress");

        return legalApi.postRecipientLegalAddress(xPagopaPnCxId, xPagopaPnCxType, senderId, channelType,
                addressVerification, xPagopaPnCxGroups, xPagopaPnCxRole
        );
    }

    public Mono<Void> deleteRecipientCourtesyAddress(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                     String senderId, CourtesyChannelType channelType,
                                                     List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "deleteRecipientCourtesyAddress");

        return courtesyApi.deleteRecipientCourtesyAddress(xPagopaPnCxId, xPagopaPnCxType, senderId, channelType,
                xPagopaPnCxGroups, xPagopaPnCxRole
        );
    }

    public Mono<Void> deleteRecipientLegalAddress(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                  String senderId, LegalChannelType channelType,
                                                  List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "deleteRecipientLegalAddress");

        return legalApi.deleteRecipientLegalAddress(xPagopaPnCxId, xPagopaPnCxType, senderId, channelType,
                xPagopaPnCxGroups, xPagopaPnCxRole
        );
    }
}