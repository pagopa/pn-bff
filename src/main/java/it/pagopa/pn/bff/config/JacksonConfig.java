package it.pagopa.pn.bff.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDigitalNotificationFocus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardNotificationOverview;
import it.pagopa.pn.bff.service.senderdashboard.deserializer.CustomFocusDeserializer;
import it.pagopa.pn.bff.service.senderdashboard.deserializer.CustomOverviewDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Register custom deserializer
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BffSenderDashboardNotificationOverview.class, new CustomOverviewDeserializer());
        module.addDeserializer(BffSenderDashboardDigitalNotificationFocus.class, new CustomFocusDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }
}