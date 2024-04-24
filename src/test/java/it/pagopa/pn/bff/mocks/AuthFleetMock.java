package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeResponse;

public class AuthFleetMock {

    public final String ORIGIN = "http://localhost:8080";

    public TokenExchangeBody getTokenExchangeBody() {
        TokenExchangeBody tokenExchangeBody = new TokenExchangeBody();
        tokenExchangeBody.authorizationToken("authorization-token");

        return tokenExchangeBody;
    }

    public TokenExchangeResponse getTokenExchangeResponse() {
        TokenExchangeResponse tokenExchangeResponse = new TokenExchangeResponse();
        tokenExchangeResponse.sessionToken("mock-access-token");
        tokenExchangeResponse.exp(3600);
        tokenExchangeResponse.desiredExp(3600);
        tokenExchangeResponse.name("mock-name");
        tokenExchangeResponse.familyName("family-name");
        tokenExchangeResponse.uid("mock-uid");

        return tokenExchangeResponse;
    }
}
