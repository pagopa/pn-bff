package it.pagopa.pn.bff.pnclient.userattributes;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnUserAttributesClientImpl {

    private final ConsentsApi consentsApi;

    public Mono<Consent> getTosConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getConsentByType - TOS");
        return consentsApi.getConsentByType(xPagopaPnUid, xPagopaPnCxType, ConsentType.TOS, null)
                ;
    }

    public Mono<Consent> getPrivacyConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getConsentByType - DataPrivacy");
        return consentsApi.getConsentByType(xPagopaPnUid, xPagopaPnCxType, ConsentType.DATAPRIVACY, null)
                ;
    }

    public Mono<Void> acceptConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, ConsentType consentType, ConsentAction consentAction, String version) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "acceptConsent");
        return consentsApi.consentAction(xPagopaPnUid, xPagopaPnCxType, consentType, version, consentAction)
                ;
    }
}