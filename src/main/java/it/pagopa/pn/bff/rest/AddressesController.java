package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.AddressesApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.*;
import it.pagopa.pn.bff.service.AddressesService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class AddressesController implements AddressesApi {
    private final AddressesService addressesService;

    public AddressesController(AddressesService addressesService) {
        this.addressesService = addressesService;
    }

    /**
     * GET /bff/v1/addresses: Get the addresses of the user
     *
     * @param xPagopaPnCxId     User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxRole   Public Administration Role
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param exchange          ServerWebExchange
     * @return the list of the addresses or error
     */
    @Override
    public Mono<ResponseEntity<Flux<BffUserAddress>>> getAddressesV1(String xPagopaPnCxId,
                                                                     CxTypeAuthFleet xPagopaPnCxType,
                                                                     List<String> xPagopaPnCxGroups,
                                                                     String xPagopaPnCxRole,
                                                                     final ServerWebExchange exchange
    ) {

        Flux<BffUserAddress> serviceResponse = addressesService.getUserAddresses(
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole
        );

        return serviceResponse
                .collectList()
                .map(contact -> ResponseEntity.ok(Flux.fromIterable(contact)));
    }

    /**
     * POST /bff/v1/addresses: Create or update an address
     *
     * @param xPagopaPnCxId       User Identifier
     * @param xPagopaPnCxType     Public Administration Type
     * @param xPagopaPnCxRole     Public Administration Role
     * @param addressType         Address Type (LEGAL or COURTESY)
     * @param senderId            Sender Identifier
     * @param channelType         Channel Type (EMAIL, SMS, PEC or APPIO)
     * @param addressVerification Body of the request containing the address to be created or updated and the verification code
     * @param xPagopaPnCxGroups   Public Administration Group id List
     * @param exchange            ServerWebExchange
     * @return the address created or updated or error
     */
    @Override
    public Mono<ResponseEntity<BffAddressVerificationResponse>> createOrUpdateAddressV1(String xPagopaPnCxId,
                                                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                                                        BffAddressType addressType,
                                                                                        String senderId,
                                                                                        BffChannelType channelType,
                                                                                        Mono<BffAddressVerificationRequest> addressVerification,
                                                                                        List<String> xPagopaPnCxGroups,
                                                                                        String xPagopaPnCxRole,
                                                                                        final ServerWebExchange exchange) {

        Mono<BffAddressVerificationResponse> serviceResponse = addressesService.createOrUpdateAddress(
                xPagopaPnCxId,
                xPagopaPnCxType,
                xPagopaPnCxRole,
                addressType,
                senderId,
                channelType,
                addressVerification,
                xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    /**
     * DELETE /bff/v1/addresses: Delete an address
     *
     * @param xPagopaPnCxId     User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxRole   Public Administration Role
     * @param addressType       Address Type (LEGAL or COURTESY)
     * @param senderId          Sender Identifier
     * @param channelType       Channel Type (EMAIL, SMS, PEC or APPIO)
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param exchange          ServerWebExchange
     * @return no content or error
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteAddressV1(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                      BffAddressType addressType, String senderId,
                                                      BffChannelType channelType, List<String> xPagopaPnCxGroups,
                                                      String xPagopaPnCxRole, final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = addressesService.deleteDigitalAddress(
                xPagopaPnCxId,
                xPagopaPnCxType,
                xPagopaPnCxRole,
                addressType,
                senderId,
                channelType,
                xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }
}