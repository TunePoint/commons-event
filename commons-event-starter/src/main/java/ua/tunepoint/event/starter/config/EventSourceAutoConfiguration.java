package ua.tunepoint.event.starter.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ua.tunepoint.event.starter.DomainRelation;
import ua.tunepoint.event.starter.handler.DomainEventHandlers;
import ua.tunepoint.event.starter.kafka.KafkaEventConsumer;
import ua.tunepoint.event.starter.kafka.KafkaEventPublisher;
import ua.tunepoint.event.starter.kafka.message.Message;
import ua.tunepoint.event.starter.publisher.EventPublisher;
import ua.tunepoint.event.starter.registry.DomainRegistry;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnBean({ DomainEventHandlers.class, DomainRegistry.class })
@EnableConfigurationProperties(EventProperties.class)
public class EventSourceAutoConfiguration {

    @Autowired
    private DomainRegistry domainRegistry;

    @Autowired
    private DomainEventHandlers domainEventHandlers;

    @Autowired
    private EventProperties eventProperties;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Bean
    public MessageListener<String, Message> kafkaListener() {
        return new KafkaEventConsumer(domainEventHandlers, objectMapper);
    }

    @Bean
    public ContainerProperties containerProperties() {
        ContainerProperties containerProperties = new ContainerProperties(
                domainRegistry.domainsWithRelation(DomainRelation.CONSUMER).toArray(new String[0])
        );
        containerProperties.setMessageListener(kafkaListener());

        return containerProperties;
    }

    @Bean
    public ConsumerFactory<String, Message> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties(),
                StringDeserializer::new,
                () -> new ErrorHandlingDeserializer<>(new JsonDeserializer<>(Message.class))
        );
    }

    @Bean
    @Primary
    public MessageListenerContainer messageListenerContainer() {
        return new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProperties());
    }

    @Bean
    public Map<String, Object> consumerProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, eventProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, eventProperties.getServiceName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        return props;
    }

    @Bean
    public ProducerFactory<String, Message> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                producerProperties(), StringSerializer::new, JsonSerializer::new
        );
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public EventPublisher eventPublisher() {
         return new KafkaEventPublisher(kafkaTemplate(), objectMapper, domainRegistry);
    }

    @Bean
    public Map<String, Object> producerProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, eventProperties.getBootstrapServers());
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        return props;
    }
}
