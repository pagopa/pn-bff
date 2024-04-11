package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;

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
}
