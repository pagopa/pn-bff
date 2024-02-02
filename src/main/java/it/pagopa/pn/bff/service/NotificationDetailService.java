package it.pagopa.pn.bff.service;


import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV21;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientImpl;
import it.pagopa.pn.bff.utils.ConverterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDetailService {

    private final PnDeliveryClientImpl pnDeliveryClient;
    private final NotificationDetailMapper notificationDetailMapper;
    private final ConverterUtils converterUtils;

    public Mono<BffFullReceivedNotificationV21> getNotificationDetail(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                      String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                                                      String iun, String mandateId) {
        return pnDeliveryClient.retrieveNotification(
                xPagopaPnUid,
                converterUtils.convertCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        ).map(notificationDetailMapper::mapNotificationDetail);
    }
}
