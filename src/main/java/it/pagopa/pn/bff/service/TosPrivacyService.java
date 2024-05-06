package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyConsentActionMapper;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyConsentMapper;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.bff.exceptions.PnBffExceptionCodes.ERROR_CODE_BFF_BODYNOTFOUND;

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
        log.info("Get tos and privacy consents");
        Mono<BffConsent> tosConsent = pnUserAttributesClient.getTosConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType))
                .map(TosPrivacyConsentMapper.tosPrivacyConsentMapper::mapConsent)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        Mono<BffConsent> privacyConsent = pnUserAttributesClient.getPrivacyConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType))
                .map(TosPrivacyConsentMapper.tosPrivacyConsentMapper::mapConsent)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);


        return Mono.zip(tosConsent, privacyConsent, BffTosPrivacyConsent::new);
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
                                                Mono<BffTosPrivacyBody> tosPrivacyBody) {
        log.info("Accept or decline tos and privacy consents");
        return tosPrivacyBody.flatMap(body -> {
            Mono<Void> tosMono = Mono.empty();
            Mono<Void> privacyMono = Mono.empty();

            if (body.getTos() == null && body.getPrivacy() == null) {
                return Mono.error(new PnBffException(
                        "Body not found",
                        "The body of the request is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_BODYNOTFOUND
                ));
            }

            if (body.getTos() != null) {
                tosMono = pnUserAttributesClient.acceptConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        ConsentType.TOS,
                        new ConsentAction().action(TosPrivacyConsentActionMapper.tosPrivacyConsentActionMapper.convertConsentAction(body.getTos().getAction())),
                        body.getTos().getVersion()
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
            }

            if (body.getPrivacy() != null) {
                privacyMono = pnUserAttributesClient.acceptConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        ConsentType.DATAPRIVACY,
                        new ConsentAction().action(TosPrivacyConsentActionMapper.tosPrivacyConsentActionMapper.convertConsentAction(body.getPrivacy().getAction())),
                        body.getPrivacy().getVersion()
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
            }

            return tosMono.then(privacyMono);
        });

    }
}