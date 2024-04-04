package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
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

    public Mono<TosPrivacyConsent> getTosPrivacy(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String version) {
        log.info("getTosPrivacy");
        Mono<BffConsent> tosConsent = pnUserAttributesClient.getTosConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        version)
                .map(TosPrivacyMapper.tosPrivacyMapper::mapTosPrivacyConsent)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        Mono<BffConsent> privacyConsent = pnUserAttributesClient.getPrivacyConsent(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        version)
                .map(TosPrivacyMapper.tosPrivacyMapper::mapTosPrivacyConsent)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        return Mono.zip(tosConsent, privacyConsent, TosPrivacyConsent::new);
    }
}
