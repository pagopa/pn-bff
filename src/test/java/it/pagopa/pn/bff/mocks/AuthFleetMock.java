package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeResponse;

public class AuthFleetMock {

    public final static String ORIGIN = "http://localhost:8080";

    public BffTokenExchangeBody getTokenExchangeBody() {
        BffTokenExchangeBody tokenExchangeBody = new BffTokenExchangeBody();
        tokenExchangeBody.authorizationToken("authorization-token");

        return tokenExchangeBody;
    }

    public BffTokenExchangeResponse getTokenExchangeResponse() {
        BffTokenExchangeResponse tokenExchangeResponse = new BffTokenExchangeResponse();
        tokenExchangeResponse.sessionToken("mock-access-token");
        tokenExchangeResponse.exp(3600);
        tokenExchangeResponse.desiredExp(3600);
        tokenExchangeResponse.name("mock-name");
        tokenExchangeResponse.familyName("family-name");
        tokenExchangeResponse.uid("mock-uid");

        return tokenExchangeResponse;
    }
}
