package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.AddressesApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
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

    @Override
    public Mono<ResponseEntity<Flux<BffUserAddress>>> getAddressesV1(String xPagopaPnCxId,
                                                                     CxTypeAuthFleet xPagopaPnCxType,
                                                                     String xPagopaPnCxRole,
                                                                     List<String> xPagopaPnCxGroups,
                                                                     final ServerWebExchange exchange
    ) {
        log.logStartingProcess("getUserAddressesV1");

        Flux<BffUserAddress> serviceResponse = addressesService.getUserAddresses(
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole
        );

        log.logEndingProcess("getUserAddressesV1");

        return serviceResponse
                .collectList()
                .map(contact -> ResponseEntity.ok(Flux.fromIterable(contact)));
    }

    @Override
    public Mono<ResponseEntity<BffAddressVerificationResponse>> createOrUpdateAddressV1(String xPagopaPnCxId,
                                                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                                                        String xPagopaPnCxRole,
                                                                                        BffAddressType addressType,
                                                                                        String senderId,
                                                                                        BffChannelType channelType,
                                                                                        Mono<BffAddressVerification> addressVerification,
                                                                                        List<String> xPagopaPnCxGroups,
                                                                                        final ServerWebExchange exchange) {
        log.logStartingProcess("createOrUpdateAddressV1");

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

        log.logEndingProcess("createOrUpdateAddressV1");

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
