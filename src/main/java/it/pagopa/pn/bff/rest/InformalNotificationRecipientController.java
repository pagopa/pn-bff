package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.InformalNotificationRecipientService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class InformalNotificationRecipientController {
    private final InformalNotificationRecipientService informalNotificationRecipientService;

    public InformalNotificationRecipientController(InformalNotificationRecipientService informalNotificationRecipientService) {
        this.informalNotificationRecipientService = informalNotificationRecipientService;
    }

    /**
     * GET /bff/v1/notifications/informal/received/{iun}: Informal Notification detail
     * Get the detail of an informal notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iun               Informal Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param exchange
     * @return the detail of the informal notification with a specific IUN
     */
    public Mono<ResponseEntity<BffFullInformalNotificationV1>> getReceivedInformalNotificationV1(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails,
            final ServerWebExchange exchange) {

        Mono<BffFullInformalNotificationV1> informalNotification = informalNotificationRecipientService.getInformalNotificationDetail(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails
        );

        return informalNotification.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }
}
