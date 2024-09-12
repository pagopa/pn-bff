package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyConsentMapper;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TosPrivacyService {
    private final PnUserAttributesClientImpl pnUserAttributesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Retrieve tos and privacy consents for the user
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @return an object containing the tos and privacy consents
     */
    public Mono<BffTosPrivacyConsent> getTosPrivacy(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType) {
        log.info("Get tos and privacy consents - type: {}", xPagopaPnCxType);

        Flux<BffConsent> bffConsents = pnUserAttributesClient.getConsents(xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType))
                .filter(consent -> consent.getConsentType().equals(ConsentType.TOS) ||
                        consent.getConsentType().equals(ConsentType.DATAPRIVACY))
                .map(TosPrivacyConsentMapper.tosPrivacyConsentMapper::mapConsent)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return bffConsents.collectList().map(consents -> {
            BffTosPrivacyConsent bffTosPrivacyConsent = new BffTosPrivacyConsent();
            BffConsent tosConsent = consents.stream()
                    .filter(el -> el.getConsentType().getValue().equals(ConsentType.TOS.getValue()))
                    .findFirst()
                    .orElse(null);
            BffConsent privacyConsent = consents.stream()
                    .filter(el -> el.getConsentType().getValue().equals(ConsentType.DATAPRIVACY.getValue()))
                    .findFirst()
                    .orElse(null);
            if (tosConsent != null) {
                bffTosPrivacyConsent.setTos(tosConsent);
            }
            if (privacyConsent != null) {
                bffTosPrivacyConsent.setPrivacy(privacyConsent);
            }
            return bffTosPrivacyConsent;
        });
    }

    /**
     * Accept or decline tos and privacy consents
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param tosPrivacyBody  Body of the request containing the acceptance of the TOS and Privacy
     * @return void
     */
    public Mono<Void> acceptOrDeclineTosPrivacy(String xPagopaPnUid,
                                                CxTypeAuthFleet xPagopaPnCxType,
                                                Flux<BffTosPrivacyActionBody> tosPrivacyBody) {
        log.info("Accept or decline tos and privacy consents - type: {}", xPagopaPnCxType);

        Flux<Void> responses = tosPrivacyBody.flatMap(request -> {
            ConsentAction consentAction = new ConsentAction();
            consentAction.setAction(TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentAction(request.getAction()));
            return pnUserAttributesClient.acceptConsent(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                    TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentType(request.getType()),
                    consentAction,
                    request.getVersion()
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
        });
        return responses.collectList().then();
    }
}