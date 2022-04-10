package ua.tunepoint.event.starter.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "event")
public class EventProperties {

    private String bootstrapServers;

    private String serviceName;

    private ConsumerProperties consumer;

    private ProducerProperties producer;

    @Data
    @NoArgsConstructor
    public static class ConsumerProperties {

        private Map<String, Object> properties;
    }

    @Data
    @NoArgsConstructor
    public static class ProducerProperties {

        private Map<String, Object> properties;
    }
}
