package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.addresses.AddressVerificationMapper;
import it.pagopa.pn.bff.mappers.addresses.AddressesMapper;
import it.pagopa.pn.bff.mocks.AddressesMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.AddressesService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.MonoComparator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(controllers = AddressesController.class)
public class AddressesControllerTest {
    private final UserMock userMock = new UserMock();
    private final AddressesMock addressesMock = new AddressesMock();

    @Autowired
    WebTestClient webTestClient;
    @MockBean
    AddressesService addressesService;
    @SpyBean
    AddressesController addressesController;

    @Test
    void getUserAddresses() {
        List<BffUserAddress> response = AddressesMapper.addressesMapper.mapUserAddresses(addressesMock.getUserAddressesResponseMock());

        Mockito.when(addressesService.getUserAddresses(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.fromIterable(response));

        webTestClient
                .get()
                .uri(PnBffRestConstants.ADDRESSES_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BffUserAddress.class)
                .isEqualTo(response);

        Mockito.verify(addressesService).getUserAddresses(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        );
    }

    @Test
    void getUserAddressesError() {
        Mockito.when(addressesService.getUserAddresses(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new WebClientResponseException(500, "Error", null, null, null)));

        webTestClient
                .get()
                .uri(PnBffRestConstants.ADDRESSES_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        Mockito.verify(addressesService).getUserAddresses(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        );
    }

    @Test
    void createOrUpdateUserAddress() {
        BffAddressVerification request = addressesMock.getBffAddressVerificationMock();
        BffAddressVerificationResponse response = AddressVerificationMapper.addressVerificationMapper.mapAddressVerificationResponse(
                addressesMock.addressVerificationCourtesyResponseMock()
        );

        Mockito.when(addressesService.createOrUpdateAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(BffAddressType.class),
                Mockito.anyString(),
                Mockito.any(BffChannelType.class),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.just(response));

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.CREATE_ADDRESS_PATH).build(
                        "COURTESY", "default", "EMAIL"
                ))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffAddressVerificationResponse.class)
                .isEqualTo(response);

        Mockito.verify(addressesService).createOrUpdateAddress(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ROLE),
                eq(BffAddressType.COURTESY),
                eq(UserMock.SENDER_ID),
                eq(BffChannelType.EMAIL),
                argThat((argumentToCompare -> MonoComparator.compare(argumentToCompare, Mono.just(request)))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void createOrUpdateUserAddressError() {
        BffAddressVerification request = addressesMock.getBffAddressVerificationMock();

        Mockito.when(addressesService.createOrUpdateAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(BffAddressType.class),
                Mockito.anyString(),
                Mockito.any(BffChannelType.class),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(500, "Error", null, null, null)));

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.CREATE_ADDRESS_PATH).build(
                        "COURTESY", "default", "EMAIL"
                ))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        Mockito.verify(addressesService).createOrUpdateAddress(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ROLE),
                eq(BffAddressType.COURTESY),
                eq(UserMock.SENDER_ID),
                eq(BffChannelType.EMAIL),
                argThat((argumentToCompare -> MonoComparator.compare(argumentToCompare, Mono.just(request)))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }
}
