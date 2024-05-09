package it.pagopa.pn.bff.config;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.api.ApiKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.DocumentsWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.LegalFactsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderReadWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.api.PaymentInfoApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.AllApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.CourtesyApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.LegalApi;
import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MsClientConfig extends CommonBaseClient {

    @Bean
    @Primary
    RecipientReadApi recipientReadApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.ApiClient.buildWebClientBuilder())
                );
        apiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new RecipientReadApi(apiClient);
    }

    @Bean
    @Primary
    SenderReadB2BApi senderReadB2BApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.ApiClient.buildWebClientBuilder())
                );
        apiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new SenderReadB2BApi(apiClient);
    }

    @Bean
    @Primary
    SenderReadWebApi senderReadWebApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.ApiClient.buildWebClientBuilder())
                );
        apiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new SenderReadWebApi(apiClient);
    }

    @Bean
    @Primary
    ApiKeysApi apiKeysApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.ApiClient.buildWebClientBuilder())
                );
        apiClient.setBasePath(cfg.getApikeyManagerBaseUrl());
        return new ApiKeysApi(apiClient);
    }

    @Bean
    @Primary
    InfoPaApi infoPaApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.ApiClient.buildWebClientBuilder())
                );
        apiClient.setBasePath(cfg.getExternalRegistriesBaseUrl());
        return new InfoPaApi(apiClient);
    }

    @Bean
    @Primary
    PaymentInfoApi paymentInfoApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.ApiClient.buildWebClientBuilder())
                );
        apiClient.setBasePath(cfg.getExternalRegistriesBaseUrl());
        return new PaymentInfoApi(apiClient);
    }

    @Bean
    @Primary
    ConsentsApi consentsApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getUserAttributesBaseUrl());
        return new ConsentsApi(apiClient);
    }

    @Bean
    @Primary
    AllApi allAddressApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getUserAttributesBaseUrl());
        return new AllApi(apiClient);
    }

    @Bean
    @Primary
    CourtesyApi courtesyApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getUserAttributesBaseUrl());
        return new CourtesyApi(apiClient);
    }

    @Bean
    @Primary
    LegalApi legalApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getUserAttributesBaseUrl());
        return new LegalApi(apiClient);
    }

    @Bean
    @Primary
    DowntimeApi downtimeLogsApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getDowntimeLogsBaseUrl());
        return new DowntimeApi(apiClient);
    }

    @Bean
    @Primary
    DocumentsWebApi documentsWebApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new DocumentsWebApi(apiClient);
    }

    @Bean
    @Primary
    LegalFactsApi legalFactsApi(PnBffConfigs cfg) {
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.ApiClient apiClient =
                new it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.ApiClient(
                        initWebClient(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new LegalFactsApi(apiClient);
    }
}