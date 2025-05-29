package it.pagopa.pn.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

@Configuration
public class HeadersConfig {

    @Bean
    public WebFilter traceIdFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            exchange.getResponse().getHeaders()
                    .add("Access-Control-Expose-Headers", "x-amzn-trace-id");

            return chain.filter(exchange);
        };
    }

}