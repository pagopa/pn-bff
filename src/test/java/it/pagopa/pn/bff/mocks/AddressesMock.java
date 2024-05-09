package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAddressVerificationRequest;

import java.util.ArrayList;
import java.util.List;

public class AddressesMock {
    public static final String SENDER_ID = "default";

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

    public BffAddressVerificationRequest getBffAddressVerificationMock() {
        return new BffAddressVerificationRequest()
                .value("test@test.com")
                .verificationCode("12345")
                .requestId("1234567890");
    }

    public AddressVerification getAddressVerificationBodyMock() {
        return new AddressVerification()
                .value("test@test.com")
                .verificationCode("12345")
                .requestId("123456789");
    }

    public AddressVerificationResponse addressVerificationCourtesyResponseMock() {
        return new AddressVerificationResponse()
                .result(AddressVerificationResponse.ResultEnum.CODE_VERIFICATION_REQUIRED);
    }

    public AddressVerificationResponse addressVerificationLegalResponseMock() {
        return new AddressVerificationResponse()
                .result(AddressVerificationResponse.ResultEnum.PEC_VALIDATION_REQUIRED);
    }

}
