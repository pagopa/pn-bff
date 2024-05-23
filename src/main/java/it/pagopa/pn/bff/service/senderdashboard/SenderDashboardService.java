package it.pagopa.pn.bff.service.senderdashboard;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.exceptions.PnBffExceptionCodes;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDataResponse;
import it.pagopa.pn.bff.service.senderdashboard.connectors.DatalakeS3Resource;
import it.pagopa.pn.bff.service.senderdashboard.exceptions.SenderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderDashboardService {

    private final DatalakeS3Resource datalakeResource;


    /**
     * Fetches dashboard data for the specified sender.
     *
     * @param cxType    Public Administration Type
     * @param cxId      Public Administration id
     * @param pathCxType Public Administration Type
     * @param pathCxId  Public Administration id
     * @param startDate the start date for the data request
     * @param endDate   the end date for the data request.
     * @return a {@link Mono} emitting the {@link BffSenderDashboardDataResponse}.
     */
    public Mono<BffSenderDashboardDataResponse> getDashboardData(
            String cxType,
            String cxId,
            String pathCxType,
            String pathCxId,
            LocalDate startDate,
            LocalDate endDate) {

        if(!Objects.equals(cxType, pathCxType) || !Objects.equals(cxId, pathCxId)) {
            return Mono.error(
                    new PnBffException(
                        "cxType and cxId mismatch",
                        "The provided cxType and cxId do not match the values in the headers x-pagopa-pn-cx-type and x-pagopa-pn-cx-id",
                        HttpStatus.BAD_REQUEST.value(),
                        PnBffExceptionCodes.ERROR_CODE_PN_GENERIC_ERROR
                    ));
        }

        return Mono.fromCallable(() -> datalakeResource.getDataResponse(cxId, startDate, endDate))
                .onErrorMap(SenderNotFoundException.class, e -> {
                    log.error("Exception occurred while fetching data from Datalake", e);
                    return new PnBffException(
                            "cxId not found ",
                            "cxId not found ",
                            HttpStatus.NOT_FOUND.value(),
                            PnBffExceptionCodes.ERROR_CODE_PN_GENERIC_ERROR
                    );
                })
                .onErrorMap(Exception.class,  e -> {
                    log.error("Exception occurred while fetching data from Datalake", e);
                    return new PnBffException(
                            e.getMessage(),
                            e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            PnBffExceptionCodes.ERROR_CODE_PN_GENERIC_ERROR
                    );
                });
    }
}
