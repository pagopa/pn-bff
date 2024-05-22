package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.senderdashboard.SenderDashboardService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.SenderDashboardApi;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@CustomLog
@RestController
public class SenderDashboardController implements SenderDashboardApi {
    private final SenderDashboardService senderDashboardService;

    public SenderDashboardController(SenderDashboardService senderDashboardService) {
        this.senderDashboardService = senderDashboardService;
    }

    /**
     * GET /bff/v1/sender-dashboard/dashboard-data-request/{cxType}/{cxId}: data request
     * Get dashboard data
     *
     * @param xPagopaPnCxType The type of the user
     * @param xPagopaPnCxId   The id of the user
     * @param cxType          The type of the user
     * @param cxId            The id of the user
     * @param startDate       The search start date
     * @param endDate         The search end date (included)
     * @return the detailed list of payments
     */
    @Override
    public Mono<ResponseEntity<BffSenderDashboardDataResponse>> getDashboardDataV1(
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String cxType,
            String cxId,
            LocalDate startDate,
            LocalDate endDate,
            final ServerWebExchange exchange) {
        log.logStartingProcess("getDashboardDataV1");
//        if (! xPagopaPnCxType.getValue().equals(cxType) || ! xPagopaPnCxId.equals(cxId)) {
//            return null; // TODO 400 invalid input
//        }
        Mono<BffSenderDashboardDataResponse> serviceResponse =
                senderDashboardService.getDashboardData(xPagopaPnCxType.getValue(), xPagopaPnCxId, startDate, endDate);
        log.logEndingProcess("getDashboardDataV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
