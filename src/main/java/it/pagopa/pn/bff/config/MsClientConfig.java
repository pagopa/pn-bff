package it.pagopa.pn.bff.config;

import it.pagopa.pn.bff.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.ApiClient;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.api.InfoPaApi;
import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class MsClientConfig extends CommonBaseClient {

    @Bean
    @Primary
    RecipientReadApi recipientReadApi(PnBffConfigs cfg) {
        ApiClient apiClient = new ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new RecipientReadApi(apiClient);
    }

    @Bean
    @Primary
    SenderReadB2BApi senderReadB2BApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.ApiClient senderApiClient = new it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        senderApiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new SenderReadB2BApi(senderApiClient);
    }

    @Bean
    @Primary
    InfoPaApi infoPaApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries.ApiClient infoPaApiClient = new it.pagopa.pn.bff.generated.openapi.msclient.external_registries.ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        infoPaApiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new InfoPaApi(infoPaApiClient);
    }

}