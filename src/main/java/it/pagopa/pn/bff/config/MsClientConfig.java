package it.pagopa.pn.bff.config;

import it.pagopa.pn.bff.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.ApiClient;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
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
        apiClient.setBasePath(cfg.getDeliveryPushBaseUrl());
        return new RecipientReadApi(apiClient);
    }

}
