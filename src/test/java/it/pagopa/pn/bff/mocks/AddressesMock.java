package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAddressVerification;

import java.util.ArrayList;
import java.util.List;

public class AddressesMock {

    public UserAddresses getUserAddressesResponseMock() {
        UserAddresses userAddresses = new UserAddresses();
        List<LegalAndUnverifiedDigitalAddress> legal = new ArrayList<>();
        legal.add(
                new LegalAndUnverifiedDigitalAddress()
                        .addressType(LegalAddressType.LEGAL)
                        .channelType(LegalChannelType.PEC)
                        .codeValid(true)
                        .pecValid(true)
                        .value("test@pec.it")
                        .recipientId("1234567890")
                        .senderId("default")
        );

        List<CourtesyDigitalAddress> courtesy = new ArrayList<>();
        courtesy.add(
                new CourtesyDigitalAddress()
                        .addressType(CourtesyAddressType.COURTESY)
                        .channelType(CourtesyChannelType.EMAIL)
                        .value("test@test.it")
                        .recipientId("1234567890")
                        .senderId("default")
        );

        courtesy.add(
                new CourtesyDigitalAddress()
                        .addressType(CourtesyAddressType.COURTESY)
                        .channelType(CourtesyChannelType.SMS)
                        .value("3333333333")
                        .recipientId("1234567890")
                        .senderId("default")
        );


        userAddresses.setLegal(legal);
        userAddresses.setCourtesy(courtesy);

        return userAddresses;
    }

    public BffAddressVerification getAddressVerificationMock() {
        return new BffAddressVerification()
                .value("test")
                .verificationCode("123456")
                .requestId("1234567890");
    }
}
