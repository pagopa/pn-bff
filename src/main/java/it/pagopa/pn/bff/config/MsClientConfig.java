package it.pagopa.pn.bff.config;

import it.pagopa.pn.bff.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.ApiClient;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
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
    ConsentsApi consentsApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient consentsApi = new it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        consentsApi.setBasePath(cfg.getDeliveryBaseUrl());
        return new ConsentsApi(consentsApi);
    }
}