package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyConsent;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyConsentMapper;

public class ConsentsMock {
    public Consent getTosConsentResponseMock() {
        Consent consent = new Consent();
        consent.setConsentType(ConsentType.TOS);
        consent.setConsentVersion("1");
        consent.setAccepted(true);
        consent.setRecipientId("1234567890");
        consent.setIsFirstAccept(false);
        return consent;
    }

    public Consent getPrivacyConsentResponseMock() {
        Consent consent = new Consent();
        consent.setConsentType(ConsentType.DATAPRIVACY);
        consent.setConsentVersion("1");
        consent.setAccepted(true);
        consent.setRecipientId("1234567890");
        consent.setIsFirstAccept(false);
        return consent;
    }

    public ConsentAction requestConsentActionMock() {
        ConsentAction consentAction = new ConsentAction();
        consentAction.setAction(ConsentAction.ActionEnum.ACCEPT);
        return consentAction;
    }

    public BffTosPrivacyConsent getBffTosPrivacyConsentMock() {
        BffTosPrivacyConsent bffTosPrivacyConsent = new BffTosPrivacyConsent();

        bffTosPrivacyConsent.setTos(TosPrivacyConsentMapper.tosPrivacyConsentMapper.mapConsent(getTosConsentResponseMock()));
        bffTosPrivacyConsent.setPrivacy(TosPrivacyConsentMapper.tosPrivacyConsentMapper.mapConsent(getPrivacyConsentResponseMock()));

        return bffTosPrivacyConsent;
    }

    public BffTosPrivacyBody acceptTosPrivacyBodyMock() {
        BffTosPrivacyBody bffTosPrivacyBody = new BffTosPrivacyBody();

        bffTosPrivacyBody.privacy(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1"));
        bffTosPrivacyBody.tos(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1"));

        return bffTosPrivacyBody;
    }
}