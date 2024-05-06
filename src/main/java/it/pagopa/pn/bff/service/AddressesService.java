package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.UserAddresses;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.addresses.AddressVerificationMapper;
import it.pagopa.pn.bff.mappers.addresses.AddressesMapper;
import it.pagopa.pn.bff.mappers.addresses.ChannelTypeMapper;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressesService {
    private final PnUserAttributesClientImpl pnUserAttributesClient;

    /**
     * Get user addresses
     *
     * @param xPagopaPnCxId     Receiver id
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param xPagopaPnCxRole   Role
     * @return the list of user addresses
     */
    public Flux<BffUserAddress> getUserAddresses(String xPagopaPnCxId,
                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                 List<String> xPagopaPnCxGroups,
                                                 String xPagopaPnCxRole) {
        log.info("Get user addresses");
        Mono<UserAddresses> serviceRes = pnUserAttributesClient.getUserAddresses(xPagopaPnCxId,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        xPagopaPnCxGroups,
                        xPagopaPnCxRole
                )
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return serviceRes.flatMapMany(userAddresses ->
                Flux.fromIterable(AddressesMapper.addressesMapper.mapUserAddresses(userAddresses))
        );
    }

    public Mono<BffAddressVerificationResponse> createOrUpdateAddress(String xPagopaPnCxId,
                                                                      CxTypeAuthFleet xPagopaPnCxType,
                                                                      String xPagopaPnCxRole,
                                                                      BffAddressType addressType,
                                                                      String senderId,
                                                                      BffChannelType channelType,
                                                                      Mono<BffAddressVerification> addressVerification,
                                                                      List<String> xPagopaPnCxGroups) {

        log.info("Create or Update Address");

        if (addressType.getValue().equals(BffAddressType.COURTESY.getValue())) {
            return addressVerification.flatMap(verification -> pnUserAttributesClient.createOrUpdateCourtesyAddress(
                            xPagopaPnCxId,
                            CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                            senderId,
                            ChannelTypeMapper.channelTypeMapper.mapCourtesyChannelType(channelType),
                            AddressVerificationMapper.addressVerificationMapper.mapAddressVerification(verification),
                            xPagopaPnCxGroups,
                            xPagopaPnCxRole
                    ).map(AddressVerificationMapper.addressVerificationMapper::mapAddressVerificationResponse)
                    .onErrorMap(WebClientResponseException.class, PnBffException::wrapException));
        } else {
            return addressVerification.flatMap(verification -> pnUserAttributesClient.createOrUpdateLegalAddress(
                            xPagopaPnCxId,
                            CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                            senderId,
                            ChannelTypeMapper.channelTypeMapper.mapLegalChannelType(channelType),
                            AddressVerificationMapper.addressVerificationMapper.mapAddressVerification(verification),
                            xPagopaPnCxGroups,
                            xPagopaPnCxRole
                    ).map(AddressVerificationMapper.addressVerificationMapper::mapAddressVerificationResponse)
                    .onErrorMap(WebClientResponseException.class, PnBffException::wrapException));
        }
    }
}
