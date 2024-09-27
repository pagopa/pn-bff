package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TosPrivacyService {
    private final PnUserAttributesClientImpl pnUserAttributesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Accept or decline pg tos and privacy consents
     *
     * @param xPagopaPnCxId           User Identifier
     * @param xPagopaPnCxType         Customer/Recipient Type
     * @param bffTosPrivacyActionBody Body of the request containing the acceptance of the Pg TOS and Privacy
     * @param xPagopaPnCxRole         Customer/Recipient Role
     * @param xPagopaPnCxGroups       List of Customer/Recipient groups
     * @return void
     */
    public Mono<Void> acceptOrDeclinePgTosPrivacy(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                  Flux<BffTosPrivacyActionBody> bffTosPrivacyActionBody,
                                                  String xPagopaPnCxRole, List<String> xPagopaPnCxGroups) {

        log.info("Accept or decline pg tos and privacy consents - type: {}", xPagopaPnCxType);

        Flux<Void> responses = bffTosPrivacyActionBody.flatMap(request -> {
            ConsentAction consentAction = new ConsentAction();
            consentAction.setAction(TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentAction(request.getAction()));
            return pnUserAttributesClient.acceptConsentPg(
                    xPagopaPnCxId,
                    CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                    TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentType(request.getType()),
                    xPagopaPnCxRole,
                    request.getVersion(),
                    consentAction,
                    xPagopaPnCxGroups
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
        });
        return responses.collectList().then();

    }

    /**
     * Retrieve pg tos and privacy consents for the user
     *
     * @param xPagopaPnCxId    User Identifier
     * @param xPagopaPnCxType Customer/Recipient Type
     * @return an object containing the tos and privacy consents
     */
    public Flux<BffConsent> getPgTosPrivacy(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, List<ConsentType> type) {
        log.info("Get pg tos and privacy consents - type: {}", xPagopaPnCxType);

        Flux<Consent> consents = Flux.empty();
        for (ConsentType t : type) {
            Mono<Consent> consent = pnUserAttributesClient.getPgConsentByType(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType), TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentType(t));
            consents = Flux.concat(consents, consent);
        }

        return consents
                .map(TosPrivacyConsentMapper.tosPrivacyConsentMapper::mapConsent)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Retrieve tos and privacy consents for the user
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param type            List of consents to retrieve
     * @return an object containing the tos and privacy consents
     */
    public Flux<BffConsent> getTosPrivacy(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, List<ConsentType> type) {
        log.info("Get tos and privacy consents - type: {}", xPagopaPnCxType);

        Flux<Consent> consents = Flux.empty();
        for (ConsentType t : type) {
            Mono<Consent> consent = pnUserAttributesClient.getConsentByType(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType), TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentType(t));
            consents = Flux.concat(consents, consent);
        }

        return consents
                .map(TosPrivacyConsentMapper.tosPrivacyConsentMapper::mapConsent)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
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