package it.pagopa.pn.bff.config;

import it.pagopa.pn.bff.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.api.ApiKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.ApiClient;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
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
    ApiKeysApi apiKeysApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.ApiClient apiClient = new it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getApikeyManagerBaseUrl());
        return new ApiKeysApi(apiClient);
    }

    @Bean
    @Primary
    InfoPaApi infoPaApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.ApiClient apiClient = new it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getExternalRegistriesBaseUrl());
        return new InfoPaApi(apiClient);
    }
}