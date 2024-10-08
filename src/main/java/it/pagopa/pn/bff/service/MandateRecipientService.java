package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.mandate.*;
import it.pagopa.pn.bff.pnclient.mandate.PnMandateClientRecipientImpl;
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
public class MandateRecipientService {
    private final PnMandateClientRecipientImpl pnMandateClientRecipient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get total mandates based on required status if filter's specified.
     * If no filter is present, returns total of all pending and active mandates
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param status            Mandate status
     * @return mandates count
     */
    public Mono<BffMandatesCount> countMandatesByDelegate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                          List<String> xPagopaPnCxGroups, String xPagopaPnCxRole,
                                                          String status) {
        log.info("Count mandates by delegate - recipientId: {} - type: {} - groups: {} - role: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole);

        return pnMandateClientRecipient
                .countMandatesByDelegate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), xPagopaPnCxGroups, xPagopaPnCxRole, status)
                .map(MandateCountMapper.modelMapper::mapCount)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Create a new mandate.
     *
     * @param xPagopaPnUid      User id
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param newMandateRequest New mandate request
     * @return
     */
    public Mono<Void> createMandate(String xPagopaPnUid,
                                    String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole,
                                    Mono<BffNewMandateRequest> newMandateRequest) {
        log.info("Create mandate - recipientId: {} - type: {} - groups: {} - role: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole);

        return newMandateRequest.flatMap(req -> pnMandateClientRecipient
                .createMandate(xPagopaPnUid, xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), xPagopaPnCxGroups, xPagopaPnCxRole, NewMandateMapper.modelMapper.mapRequest(req))
                .then()
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException));
    }

    /**
     * Accept a mandate.
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param acceptRequest     The request containing the verification code and the groups
     * @return
     */
    public Mono<Void> acceptMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole,
                                    Mono<BffAcceptRequest> acceptRequest) {
        log.info("Accept mandate - recipientId: {} - type: {} - groups: {} - role: {} - mandateId: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, mandateId);

        return acceptRequest.flatMap(req -> pnMandateClientRecipient
                .acceptMandate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), mandateId, xPagopaPnCxGroups, xPagopaPnCxRole, AcceptMandateMapper.modelMapper.mapRequest(req))
                .then()
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException));
    }

    /**
     * Update a mandate.
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param updateRequest     The request containing the groups
     * @return
     */
    public Mono<Void> updateMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole,
                                    Mono<BffUpdateRequest> updateRequest) {
        log.info("Update mandate - recipientId: {} - type: {} - groups: {} - role: {} - mandateId: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, mandateId);

        return updateRequest.flatMap(req -> pnMandateClientRecipient
                .updateMandate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), mandateId, xPagopaPnCxGroups, xPagopaPnCxRole, UpdateMandateMapper.modelMapper.mapRequest(req))
                .then()
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException));
    }

    /**
     * Reject a mandate.
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @return
     */
    public Mono<Void> rejectMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole) {
        log.info("Reject mandate - recipientId: {} - type: {} - groups: {} - role: {} - mandateId: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, mandateId);

        return pnMandateClientRecipient
                .rejectMandate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), mandateId, xPagopaPnCxGroups, xPagopaPnCxRole)
                .then()
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Revoke a mandate.
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param mandateId         The id of the mandate created
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @return
     */
    public Mono<Void> revokeMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole) {
        log.info("Revoke mandate - recipientId: {} - type: {} - groups: {} - role: {} - mandateId: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole, mandateId);

        return pnMandateClientRecipient
                .revokeMandate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), mandateId, xPagopaPnCxGroups, xPagopaPnCxRole)
                .then()
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Return filtered mandates based on required status if filter's specified.
     * If no filter is present, returns all pending and active mandates
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param status            Mandate status
     * @return mandates list
     */
    public Flux<BffMandate> getMandatesByDelegate(String xPagopaPnCxId,
                                                  CxTypeAuthFleet xPagopaPnCxType,
                                                  List<String> xPagopaPnCxGroups,
                                                  String xPagopaPnCxRole,
                                                  String status) {
        log.info("Get mandates by delegate - recipientId: {} - type: {} - groups: {} - role: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole);

        return pnMandateClientRecipient
                .getMandatesByDelegate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), xPagopaPnCxGroups, xPagopaPnCxRole, status)
                .map(MandatesMapper.modelMapper::mapMandateByDelegate)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Return filtered mandates based on status, groups and delegators (taxId)
     *
     * @param xPagopaPnCxId        User id
     * @param xPagopaPnCxType      User Type
     * @param size                 Page size
     * @param xPagopaPnCxGroups    User Group id List
     * @param xPagopaPnCxRole      User role
     * @param nextPageKey          The key of the page
     * @param searchMandateRequest the request containing the filters
     * @return mandates list
     */
    public Mono<BffSearchMandateResponse> searchMandatesByDelegate(String xPagopaPnCxId,
                                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                                   Integer size,
                                                                   List<String> xPagopaPnCxGroups,
                                                                   String xPagopaPnCxRole,
                                                                   String nextPageKey,
                                                                   Mono<BffSearchMandateRequest> searchMandateRequest) {
        log.info("Search mandates by delegate - recipientId: {} - type: {} - groups: {} - role: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole);

        return searchMandateRequest.flatMap(request ->
                pnMandateClientRecipient
                        .searchMandatesByDelegate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), size, xPagopaPnCxGroups, xPagopaPnCxRole, nextPageKey, SearchMandateByDelegateMapper.modelMapper.mapRequest(request))
                        .map(SearchMandateByDelegateMapper.modelMapper::mapResponse)
                        .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
        );
    }

    /**
     * Return all the mandates created.
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @return mandates list
     */
    public Flux<BffMandate> getMandatesByDelegator(String xPagopaPnCxId,
                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                   List<String> xPagopaPnCxGroups,
                                                   String xPagopaPnCxRole) {
        log.info("Get mandates by delegator - recipientId: {} - type: {} - groups: {} - role: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, xPagopaPnCxRole);

        return pnMandateClientRecipient
                .getMandatesByDelegator(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), xPagopaPnCxGroups, xPagopaPnCxRole)
                .map(MandatesMapper.modelMapper::mapMandateByDelegator)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

}