package it.pagopa.pn.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.reactive.UrlHandlerFilter;
import org.springframework.web.server.WebFilter;

/**
 * Backward compatibility with Spring Boot 2: since Spring 6 a request with a trailing slash
 * (e.g. /bff/v1/tos-privacy/) no longer matches the mapping without it and returns 404.
 * This filter strips the trailing slash before routing, so both forms keep working.
 */
@Configuration
public class TrailingSlashConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter trailingSlashFilter() {
        return UrlHandlerFilter.trailingSlashHandler("/**").mutateRequest().build();
    }
}
