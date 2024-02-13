package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mapper.CxTypeMapper;
import it.pagopa.pn.bff.mapper.NotificationDetailMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDetailPAService {

    private final PnDeliveryClientPAImpl pnDeliveryClientPA;

    public Mono<BffFullSentNotificationV23> getSentNotificationDetail(String xPagopaPnUid,
                                                                      CxTypeAuthFleet xPagopaPnCxType,
                                                                      String xPagopaPnCxId, String iun,
                                                                      List<String> xPagopaPnCxGroups
    ) {
        Mono<FullSentNotificationV23> notificationDetail;
        notificationDetail = pnDeliveryClientPA.getSentNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return notificationDetail.map(NotificationDetailMapper.modelMapper::mapSentNotificationDetail);
    }
}