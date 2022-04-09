package ua.tunepoint.event.starter.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "event")
public class EventProperties {

    private String bootstrapServers;

    private String serviceName;
}
