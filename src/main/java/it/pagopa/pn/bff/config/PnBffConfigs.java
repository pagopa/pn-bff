package it.pagopa.pn.bff.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConfigurationProperties( prefix = "pn.bff")
@Data
@Import({SharedAutoConfiguration.class})
public class PnBffConfigs {
    @Data
    public static class Topics { }
}
