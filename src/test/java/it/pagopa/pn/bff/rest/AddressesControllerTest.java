package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.*;
import it.pagopa.pn.bff.mappers.addresses.AddressVerificationMapper;
import it.pagopa.pn.bff.mappers.addresses.AddressesMapper;
import it.pagopa.pn.bff.mocks.AddressesMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.AddressesService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.MonoMatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(controllers = AddressesController.class)
class AddressesControllerTest {
    private final AddressesMock addressesMock = new AddressesMock();

    @Autowired
    WebTestClient webTestClient;
    @MockBean
    AddressesService addressesService;

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
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }

    @Test
    void getUserAddressesError() {
        Mockito.when(addressesService.getUserAddresses(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

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
                .isNotFound();

        Mockito.verify(addressesService).getUserAddresses(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );
    }

    @Test
    void createOrUpdateUserAddress() {
        BffAddressVerificationRequest request = addressesMock.getBffAddressVerificationMock();
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
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.CREATE_DELETE_ADDRESS_PATH).build(
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
                eq(AddressesMock.SENDER_ID),
                eq(BffChannelType.EMAIL),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void createOrUpdateUserSuccess() {
        BffAddressVerificationRequest request = addressesMock.getBffAddressVerificationMock();

        Mockito.when(addressesService.createOrUpdateAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(BffAddressType.class),
                Mockito.anyString(),
                Mockito.any(BffChannelType.class),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.CREATE_DELETE_ADDRESS_PATH).build(
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
                .isNoContent();

        Mockito.verify(addressesService).createOrUpdateAddress(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ROLE),
                eq(BffAddressType.COURTESY),
                eq(AddressesMock.SENDER_ID),
                eq(BffChannelType.EMAIL),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void createOrUpdateUserAddressError() {
        BffAddressVerificationRequest request = addressesMock.getBffAddressVerificationMock();

        Mockito.when(addressesService.createOrUpdateAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(BffAddressType.class),
                Mockito.anyString(),
                Mockito.any(BffChannelType.class),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.CREATE_DELETE_ADDRESS_PATH).build(
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
                .isNotFound();

        Mockito.verify(addressesService).createOrUpdateAddress(
                eq(UserMock.PN_CX_ID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ROLE),
                eq(BffAddressType.COURTESY),
                eq(AddressesMock.SENDER_ID),
                eq(BffChannelType.EMAIL),
                argThat(new MonoMatcher<>(Mono.just(request))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void deleteDigitalAddress() {
        Mockito.when(addressesService.deleteDigitalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(BffAddressType.class),
                Mockito.anyString(),
                Mockito.any(BffChannelType.class),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.CREATE_DELETE_ADDRESS_PATH).build(
                        "COURTESY", "default", "EMAIL"
                ))
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNoContent();

        Mockito.verify(addressesService).deleteDigitalAddress(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ROLE,
                BffAddressType.COURTESY,
                AddressesMock.SENDER_ID,
                BffChannelType.EMAIL,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void deleteDigitalAddressError() {
        Mockito.when(addressesService.deleteDigitalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(BffAddressType.class),
                Mockito.anyString(),
                Mockito.any(BffChannelType.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.CREATE_DELETE_ADDRESS_PATH).build(
                        "COURTESY", "default", "EMAIL"
                ))
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(PnBffRestConstants.CX_ROLE_HEADER, UserMock.PN_CX_ROLE)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(addressesService).deleteDigitalAddress(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ROLE,
                BffAddressType.COURTESY,
                AddressesMock.SENDER_ID,
                BffChannelType.EMAIL,
                UserMock.PN_CX_GROUPS
        );
    }
}