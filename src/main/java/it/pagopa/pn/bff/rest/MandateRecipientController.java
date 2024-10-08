package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.MandateApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.*;
import it.pagopa.pn.bff.service.MandateRecipientService;
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
public class MandateRecipientController implements MandateApi {
    private final MandateRecipientService mandateRecipientService;

    public MandateRecipientController(MandateRecipientService mandateRecipientService) {
        this.mandateRecipientService = mandateRecipientService;
    }

    /**
     * GET bff/v1/mandate/delegate/count: Mandates count
     * Get total mandates based on required status if filter's specified.
     * If no filter is present, returns total of all pending and active mandates
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxRole   User role
     * @param xPagopaPnCxGroups User Group id List
     * @param status            Mandate status
     * @param exchange
     * @return mandates count
     */
    @Override
    public Mono<ResponseEntity<BffMandatesCount>> countMandatesByDelegateV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            String status,
            final ServerWebExchange exchange) {

        Mono<BffMandatesCount> serviceResponse = mandateRecipientService.countMandatesByDelegate(
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, status
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * POST bff/v1/mandate: Create mandate
     *
     * @param xPagopaPnUid      User id
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param newMandateRequest New mandate request
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> createMandateV1(
            String xPagopaPnUid,
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            Mono<BffNewMandateRequest> newMandateRequest,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = mandateRecipientService.createMandate(
                xPagopaPnUid, xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, newMandateRequest
        );

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
    }

    /**
     * PATCH bff/v1/mandate/{mandateId}/accept: Accept mandate
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param acceptRequest     The request containing the groups
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> acceptMandateV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            String mandateId,
            Mono<BffAcceptRequest> acceptRequest,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = mandateRecipientService.acceptMandate(
                xPagopaPnCxId, xPagopaPnCxType, mandateId, xPagopaPnCxGroups, xPagopaPnCxRole, acceptRequest
        );

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    /**
     * PATCH bff/v1/mandate/{mandateId}/update: Update mandate
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param updateRequest     The request containing the groups
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> updateMandateV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            String mandateId,
            Mono<BffUpdateRequest> updateRequest,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = mandateRecipientService.updateMandate(
                xPagopaPnCxId, xPagopaPnCxType, mandateId, xPagopaPnCxGroups, xPagopaPnCxRole, updateRequest
        );

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    /**
     * PATCH bff/v1/mandate/{mandateId}/reject: Reject mandate
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> rejectMandateV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            String mandateId,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = mandateRecipientService.rejectMandate(
                xPagopaPnCxId, xPagopaPnCxType, mandateId, xPagopaPnCxGroups, xPagopaPnCxRole
        );

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    /**
     * PATCH bff/v1/mandate/{mandateId}/revoke: Revoke mandate
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> revokeMandateV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            String mandateId,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = mandateRecipientService.revokeMandate(
                xPagopaPnCxId, xPagopaPnCxType, mandateId, xPagopaPnCxGroups, xPagopaPnCxRole
        );

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    /**
     * GET bff/v1/mandate/delegate: Get mandates
     * Return filtered mandates based on required status if filter's specified.
     * If no filter is present, returns all pending and active mandates
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param status            Mandate status
     * @param exchange
     * @return list of mandates
     */
    @Override
    public Mono<ResponseEntity<Flux<BffMandate>>> getMandatesByDelegateV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            String status,
            final ServerWebExchange exchange) {

        Flux<BffMandate> serviceResponse = mandateRecipientService.getMandatesByDelegate(
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, status
        );

        return serviceResponse
                .collectList()
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(response)));
    }

    /**
     * POST bff/v1/mandate/delegate: Search mandates
     * Return filtered mandates based on status, groups and delegators (taxId)
     *
     * @param xPagopaPnCxId        User id
     * @param xPagopaPnCxType      User Type
     * @param size                 Page size
     * @param xPagopaPnCxGroups    User Group id List
     * @param xPagopaPnCxRole      User role
     * @param nextPageKey          The key of the page
     * @param searchMandateRequest the request containing the filters
     * @param exchange
     * @return list of mandates
     */
    @Override
    public Mono<ResponseEntity<BffSearchMandateResponse>> searchMandatesByDelegateV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            Integer size,
            Mono<BffSearchMandateRequest> searchMandateRequest,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            String nextPageKey,
            final ServerWebExchange exchange) {

        Mono<BffSearchMandateResponse> serviceResponse = mandateRecipientService.searchMandatesByDelegate(
                xPagopaPnCxId, xPagopaPnCxType, size, xPagopaPnCxGroups, xPagopaPnCxRole, nextPageKey, searchMandateRequest
        );

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/mandate/delegator: Get created mandates.
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param exchange
     * @return list of mandates
     */
    @Override
    public Mono<ResponseEntity<Flux<BffMandate>>> getMandatesByDelegatorV1(
            String xPagopaPnCxId,
            CxTypeAuthFleet xPagopaPnCxType,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnCxRole,
            final ServerWebExchange exchange) {

        Flux<BffMandate> serviceResponse = mandateRecipientService.getMandatesByDelegator(
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole
        );

        return serviceResponse
                .collectList()
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(response)));
    }
}