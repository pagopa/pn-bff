package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.UserAddresses;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.addresses.AddressVerificationMapper;
import it.pagopa.pn.bff.mappers.addresses.AddressesMapper;
import it.pagopa.pn.bff.mappers.addresses.ChannelTypeMapper;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
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
    private final PnBffExceptionUtility pnBffExceptionUtility;

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
        log.info("Get user addresses - recipientId: {} - type: {} - groups: {} - role: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole);
        Mono<UserAddresses> serviceRes = pnUserAttributesClient.getUserAddresses(xPagopaPnCxId,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        xPagopaPnCxGroups,
                        xPagopaPnCxRole
                )
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return serviceRes.flatMapMany(userAddresses ->
                Flux.fromIterable(AddressesMapper.addressesMapper.mapUserAddresses(userAddresses))
        );
    }

    /**
     * Create or update an address
     *
     * @param xPagopaPnCxId       User Identifier
     * @param xPagopaPnCxType     Public Administration Type
     * @param xPagopaPnCxRole     Public Administration Role
     * @param addressType         Address Type (LEGAL or COURTESY)
     * @param senderId            Sender Identifier
     * @param channelType         Channel Type (EMAIL, SMS, PEC or APPIO)
     * @param addressVerification Body of the request containing the address to be created or updated and the verification code
     * @param xPagopaPnCxGroups   Public Administration Group id List
     * @return the address created or updated
     */
    public Mono<BffAddressVerificationResponse> createOrUpdateAddress(String xPagopaPnCxId,
                                                                      CxTypeAuthFleet xPagopaPnCxType,
                                                                      String xPagopaPnCxRole,
                                                                      BffAddressType addressType,
                                                                      String senderId,
                                                                      BffChannelType channelType,
                                                                      Mono<BffAddressVerificationRequest> addressVerification,
                                                                      List<String> xPagopaPnCxGroups) {

        log.info("Create/Update user address - recipientId: {} - type: {} - groups: {} - role: {} - senderId: {} - addressType: {} - channelType: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, senderId, addressType, channelType);

        if (addressType.getValue().equals(BffAddressType.COURTESY.getValue())) {
            return addressVerification.flatMap(verification -> pnUserAttributesClient.createOrUpdateCourtesyAddress(
                            xPagopaPnCxId,
                            CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                            senderId,
                            ChannelTypeMapper.channelTypeMapper.mapCourtesyChannelType(channelType),
                            AddressVerificationMapper.addressVerificationMapper.mapAddressVerificationRequest(verification),
                            xPagopaPnCxGroups,
                            xPagopaPnCxRole
                    ).map(AddressVerificationMapper.addressVerificationMapper::mapAddressVerificationResponse)
                    .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException));
        }

        return addressVerification.flatMap(verification -> pnUserAttributesClient.createOrUpdateLegalAddress(
                        xPagopaPnCxId,
                        CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                        senderId,
                        ChannelTypeMapper.channelTypeMapper.mapLegalChannelType(channelType),
                        AddressVerificationMapper.addressVerificationMapper.mapAddressVerificationRequest(verification),
                        xPagopaPnCxGroups,
                        xPagopaPnCxRole
                ).map(AddressVerificationMapper.addressVerificationMapper::mapAddressVerificationResponse)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException));

    }

    /**
     * Delete an address
     *
     * @param xPagopaPnCxId     Receiver id
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxRole   Role
     * @param addressType       Address Type (LEGAL or COURTESY)
     * @param senderId          Sender Identifier
     * @param channelType       Channel Type (EMAIL, SMS, PEC or APPIO)
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return no content or error
     */
    public Mono<Void> deleteDigitalAddress(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                           String xPagopaPnCxRole, BffAddressType addressType, String senderId, BffChannelType channelType,
                                           List<String> xPagopaPnCxGroups) {
        log.info("Delete user address - recipientId: {} - type: {} - groups: {} - role: {} - senderId: {} - addressType: {} - channelType: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, senderId, addressType, channelType);

        if (addressType.getValue().equals(BffAddressType.COURTESY.getValue())) {
            return pnUserAttributesClient.deleteRecipientCourtesyAddress(
                    xPagopaPnCxId,
                    CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                    senderId,
                    ChannelTypeMapper.channelTypeMapper.mapCourtesyChannelType(channelType),
                    xPagopaPnCxGroups,
                    xPagopaPnCxRole
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
        }

        return pnUserAttributesClient.deleteRecipientLegalAddress(
                xPagopaPnCxId,
                CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(xPagopaPnCxType),
                senderId,
                ChannelTypeMapper.channelTypeMapper.mapLegalChannelType(channelType),
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

    }
}