package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffUserAddress;
import it.pagopa.pn.bff.mappers.addresses.AddressesMapper;
import it.pagopa.pn.bff.mocks.AddressesMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {AddressesService.class})
public class AddressesServiceTest {
    private static AddressesService addressesService;
    private static PnUserAttributesClientImpl pnUserAttributesClient;
    AddressesMock addressesMock = new AddressesMock();

    @BeforeAll
    public static void setup() {
        pnUserAttributesClient = mock(PnUserAttributesClientImpl.class);
        addressesService = new AddressesService(pnUserAttributesClient);
    }

    @Test
    void testGetUserAddresses() {
        when(pnUserAttributesClient.getUserAddresses(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(addressesMock.getUserAddressesResponseMock()));

        Flux<BffUserAddress> addresses = addressesService.getUserAddresses(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );

        List<BffUserAddress> bffUserAddresses = AddressesMapper.addressesMapper.mapUserAddresses(addressesMock.getUserAddressesResponseMock());

        StepVerifier.create(addresses.collectList())
                .expectNext(bffUserAddresses)
                .verifyComplete();
    }

    @Test
    void testGetUserAddressesError() {
        when(pnUserAttributesClient.getUserAddresses(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Flux<BffUserAddress> addresses = addressesService.getUserAddresses(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );

        StepVerifier.create(addresses)
                .expectError()
                .verify();
    }

}
