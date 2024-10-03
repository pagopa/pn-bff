package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.AddressVerification;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CourtesyChannelType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.LegalChannelType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffAddressType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffAddressVerificationResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffChannelType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffUserAddress;
import it.pagopa.pn.bff.mappers.addresses.AddressVerificationMapper;
import it.pagopa.pn.bff.mappers.addresses.AddressesMapper;
import it.pagopa.pn.bff.mocks.AddressesMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
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
        PnBffExceptionUtility pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());

        addressesService = new AddressesService(pnUserAttributesClient, pnBffExceptionUtility);
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        );

        StepVerifier.create(addresses)
                .expectError()
                .verify();
    }

    @Test
    void createOrUpdateCourtesyDigitalAddress() {
        when(pnUserAttributesClient.createOrUpdateCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(CourtesyChannelType.class),
                Mockito.any(AddressVerification.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(addressesMock.addressVerificationCourtesyResponseMock()));

        BffAddressVerificationResponse bffUserAddress = AddressVerificationMapper
                .addressVerificationMapper
                .mapAddressVerificationResponse(addressesMock.addressVerificationCourtesyResponseMock());

        StepVerifier.create(addressesService.createOrUpdateAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.COURTESY,
                        AddressesMock.SENDER_ID,
                        BffChannelType.EMAIL,
                        Mono.just(addressesMock.getBffAddressVerificationMock()),
                        UserMock.PN_CX_GROUPS
                ))
                .expectNext(bffUserAddress)
                .verifyComplete();
    }

    @Test
    void createOrUpdateCourtesyDigitalAddressError() {
        when(pnUserAttributesClient.createOrUpdateCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(CourtesyChannelType.class),
                Mockito.any(AddressVerification.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(addressesService.createOrUpdateAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.COURTESY,
                        AddressesMock.SENDER_ID,
                        BffChannelType.EMAIL,
                        Mono.just(addressesMock.getBffAddressVerificationMock()),
                        UserMock.PN_CX_GROUPS
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void createOrUpdateLegalDigitalAddress() {
        when(pnUserAttributesClient.createOrUpdateLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(LegalChannelType.class),
                Mockito.any(AddressVerification.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(addressesMock.addressVerificationLegalResponseMock()));

        BffAddressVerificationResponse bffUserAddress = AddressVerificationMapper
                .addressVerificationMapper
                .mapAddressVerificationResponse(addressesMock.addressVerificationLegalResponseMock());

        StepVerifier.create(addressesService.createOrUpdateAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.LEGAL,
                        AddressesMock.SENDER_ID,
                        BffChannelType.PEC,
                        Mono.just(addressesMock.getBffAddressVerificationMock()),
                        UserMock.PN_CX_GROUPS
                ))
                .expectNext(bffUserAddress)
                .verifyComplete();
    }

    @Test
    void createOrUpdateLegalDigitalAddressError() {
        when(pnUserAttributesClient.createOrUpdateLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(LegalChannelType.class),
                Mockito.any(AddressVerification.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(addressesService.createOrUpdateAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.LEGAL,
                        AddressesMock.SENDER_ID,
                        BffChannelType.PEC,
                        Mono.just(addressesMock.getBffAddressVerificationMock()),
                        UserMock.PN_CX_GROUPS
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void deleteCourtesyAddress() {
        when(pnUserAttributesClient.deleteRecipientCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(CourtesyChannelType.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(addressesService.deleteDigitalAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.COURTESY,
                        AddressesMock.SENDER_ID,
                        BffChannelType.EMAIL,
                        UserMock.PN_CX_GROUPS
                ))
                .verifyComplete();
    }

    @Test
    void deleteCourtesyAddressError() {
        when(pnUserAttributesClient.deleteRecipientCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(CourtesyChannelType.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(addressesService.deleteDigitalAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.COURTESY,
                        AddressesMock.SENDER_ID,
                        BffChannelType.EMAIL,
                        UserMock.PN_CX_GROUPS
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void deleteLegalAddress() {
        when(pnUserAttributesClient.deleteRecipientLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(LegalChannelType.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(addressesService.deleteDigitalAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.LEGAL,
                        AddressesMock.SENDER_ID,
                        BffChannelType.PEC,
                        UserMock.PN_CX_GROUPS
                ))
                .verifyComplete();
    }

    @Test
    void deleteLegalAddressError() {
        when(pnUserAttributesClient.deleteRecipientLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(LegalChannelType.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(addressesService.deleteDigitalAddress(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        UserMock.PN_CX_ROLE,
                        BffAddressType.LEGAL,
                        AddressesMock.SENDER_ID,
                        BffChannelType.PEC,
                        UserMock.PN_CX_GROUPS
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}