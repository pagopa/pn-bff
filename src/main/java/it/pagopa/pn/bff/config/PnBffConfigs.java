package it.pagopa.pn.bff.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "pn.bff")
@Data
@Import({SharedAutoConfiguration.class})
@Slf4j
@EnableScheduling
public class PnBffConfigs {
    private String deliveryBaseUrl;
    private String apikeyManagerBaseUrl;
    private String externalRegistriesBaseUrl;
    private String userAttributesBaseUrl;
    private String downtimeLogsBaseUrl;
    private String authFleetBaseUrl;
    private String deliveryPushBaseUrl;
    private String mandateBaseUrl;
    private String selfcareBaseUrl;
    private String selfcareSendProdId;
    private String emdBaseUrl;
    private String selfcareCdnUrl;
    // Sender dashboard
    private String dlBucketName;
    private String dlBucketRegion;
    private String dlAssumeRoleArn;
    private String dlOverviewObjectKey;
    private String dlFocusObjectKey;
    private String pnBucketRegion;
    private String pnBucketName;
    private String pnIndexObjectKey;
    private Long pnIndexObjectCacheExpirationMinutes;

    @PostConstruct
    public void init() {
        log.info("CONFIGURATION {}", this);
    }

    @Data
    public static class Topics {
    }
}