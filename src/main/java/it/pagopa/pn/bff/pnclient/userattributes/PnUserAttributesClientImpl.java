package it.pagopa.pn.bff.pnclient.userattributes;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnUserAttributesClientImpl {

    private final ConsentsApi consentsApi;

    public Mono<Consent> getTosConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String version) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getConsentByType TOS");
        
        Mono<Consent> tosConsent;
        tosConsent = consentsApi.getConsentByType(xPagopaPnUid, xPagopaPnCxType, ConsentType.TOS, version)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return tosConsent;
    }

    public Mono<Consent> getPrivacyConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String version) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getConsentByType DataPrivacy");
        return consentsApi.getConsentByType(xPagopaPnUid, xPagopaPnCxType, ConsentType.DATAPRIVACY, version)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}
