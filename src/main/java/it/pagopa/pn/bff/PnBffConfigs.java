package it.pagopa.pn.bff;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "pn.bff")
@Data
@Import({SharedAutoConfiguration.class})
@Slf4j
public class PnBffConfigs {
    private String deliveryBaseUrl;
    private String apikeyManagerBaseUrl;
    private String externalRegistriesBaseUrl;
    private String userAttributesBaseUrl;

    @PostConstruct
    public void init() {
        log.info("CONFIGURATION {}", this);
    }

    @Data
    public static class Topics {
    }
}