package it.pagopa.pn.bff.pnclient.userattributes;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.AllApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnUserAttributesClientImpl {

    private final ConsentsApi consentsApi;

    private final AllApi allAddressesApi;

    public Mono<Consent> getTosConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getConsentByType - TOS");
        return consentsApi.getConsentByType(xPagopaPnUid, xPagopaPnCxType, ConsentType.TOS, null)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Mono<Consent> getPrivacyConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getConsentByType - DataPrivacy");
        return consentsApi.getConsentByType(xPagopaPnUid, xPagopaPnCxType, ConsentType.DATAPRIVACY, null)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Mono<Void> acceptConsent(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, ConsentType consentType, ConsentAction consentAction, String version) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "acceptConsent");
        return consentsApi.consentAction(xPagopaPnUid, xPagopaPnCxType, consentType, version, consentAction)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Mono<UserAddresses> getUserAddresses(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_USER_ATTRIBUTES, "getAddressesByRecipient");
        return allAddressesApi.getAddressesByRecipient(xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole)
                .doOnSuccess(userAddresses -> log.info("getAddressesByRecipient {}", userAddresses))
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}
