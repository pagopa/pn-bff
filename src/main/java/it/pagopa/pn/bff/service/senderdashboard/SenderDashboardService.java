package it.pagopa.pn.bff.service.senderdashboard;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.exceptions.PnBffExceptionCodes;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDataResponse;
import it.pagopa.pn.bff.service.senderdashboard.connectors.DatalakeS3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderDashboardService {

    private final DatalakeS3Resource datalakeResource;

    public Mono<BffSenderDashboardDataResponse> getDashboardData(String cxType, String cxId, LocalDate startDate,
            LocalDate endDate) {

        // TODO checkCxType
        return Mono.fromCallable(() -> datalakeResource.getDataResponse(cxId, startDate, endDate))
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
