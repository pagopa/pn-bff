package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody;
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

    public Consent getPgTosConsentResponseMock() {
        Consent consent = new Consent();
        consent.setConsentType(ConsentType.TOS_DEST_B2B);
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

    public List<BffConsent> getBffPgTosPrivacyConsentMock() {
        List<BffConsent> bffTosPrivacyConsent = new ArrayList<>();

        bffTosPrivacyConsent.add(TosPrivacyConsentMapper.tosPrivacyConsentMapper.mapConsent(getPgTosConsentResponseMock()));

        return bffTosPrivacyConsent;
    }

    public List<BffTosPrivacyActionBody> acceptTosPrivacyBodyMock() {
        List<BffTosPrivacyActionBody> bffTosPrivacyBody = new ArrayList<>();

        bffTosPrivacyBody.add(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1").type(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.TOS));
        bffTosPrivacyBody.add(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1").type(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.DATAPRIVACY));

        return bffTosPrivacyBody;
    }

    public List<BffTosPrivacyActionBody> acceptPgTosPrivacyBodyMock() {
        List<BffTosPrivacyActionBody> bffTosPrivacyBody = new ArrayList<>();

        bffTosPrivacyBody.add(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1").type(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.TOS_DEST_B2B));

        return bffTosPrivacyBody;
    }
}