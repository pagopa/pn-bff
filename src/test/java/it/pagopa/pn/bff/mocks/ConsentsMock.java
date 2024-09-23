package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyConsentMapper;

import java.util.ArrayList;
import java.util.List;

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

    public List<BffConsent> getBffTosPrivacyConsentMock() {
        List<BffConsent> bffTosPrivacyConsent = new ArrayList<>();

        bffTosPrivacyConsent.add(TosPrivacyConsentMapper.tosPrivacyConsentMapper.mapConsent(getTosConsentResponseMock()));
        bffTosPrivacyConsent.add(TosPrivacyConsentMapper.tosPrivacyConsentMapper.mapConsent(getPrivacyConsentResponseMock()));

        return bffTosPrivacyConsent;
    }

    public List<BffTosPrivacyActionBody> acceptTosPrivacyBodyMock() {
        List<BffTosPrivacyActionBody> bffTosPrivacyBody = new ArrayList<>();

        bffTosPrivacyBody.add(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1").type(it.pagopa.pn.bff.generated.openapi.server.v1.dto.ConsentType.TOS));
        bffTosPrivacyBody.add(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1").type(it.pagopa.pn.bff.generated.openapi.server.v1.dto.ConsentType.DATAPRIVACY));

        return bffTosPrivacyBody;
    }
}