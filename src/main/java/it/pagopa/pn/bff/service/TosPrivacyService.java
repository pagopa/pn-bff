package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyConsent;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyMapper;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TosPrivacyService {
    private final PnUserAttributesClientImpl pnUserAttributesClient;

    public Mono<TosPrivacyConsent> getTosPrivacy(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType) {
        log.info("Get tos and privacy consents");
        Mono<BffConsent> tosConsent = pnUserAttributesClient.getTosConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType))
                .map(TosPrivacyMapper.tosPrivacyMapper::mapTosPrivacyConsent)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        Mono<BffConsent> privacyConsent = pnUserAttributesClient.getPrivacyConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType))
                .map(TosPrivacyMapper.tosPrivacyMapper::mapTosPrivacyConsent)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        return Mono.zip(tosConsent, privacyConsent, TosPrivacyConsent::new);
    }

    public Mono<Void> acceptOrDeclineTosPrivacy(String xPagopaPnUid,
                                                CxTypeAuthFleet xPagopaPnCxType,
                                                Mono<TosPrivacyBody> tosPrivacyBody) {
        log.info("Accept or decline tos and privacy consents");
        return tosPrivacyBody.flatMap(body -> {
            Mono<Void> tosMono = Mono.empty();
            Mono<Void> privacyMono = Mono.empty();

            if (body.getTos() == null && body.getPrivacy() == null) {
                return Mono.error(new PnBffException(
                        "Missing tos or privacy body",
                        "PN_GENERIC_ERROR",
                        "Missing tos or privacy body",
                        400,
                        "Missing tos or privacy body",
                        null
                ));
            }

            if (body.getTos() != null) {
                tosMono = pnUserAttributesClient.acceptConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        ConsentType.TOS,
                        new ConsentAction().action(TosPrivacyMapper.tosPrivacyMapper.convertConsentAction(body.getTos().getAction())),
                        body.getTos().getVersion()
                ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
            }

            if (body.getPrivacy() != null) {
                privacyMono = pnUserAttributesClient.acceptConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        ConsentType.DATAPRIVACY,
                        new ConsentAction().action(TosPrivacyMapper.tosPrivacyMapper.convertConsentAction(body.getPrivacy().getAction())),
                        body.getPrivacy().getVersion()
                ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
            }

            return tosMono.then(privacyMono);
        });

    }
}
