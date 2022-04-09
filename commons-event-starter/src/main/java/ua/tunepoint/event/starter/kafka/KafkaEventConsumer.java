package ua.tunepoint.event.starter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import ua.tunepoint.event.model.DomainEvent;
import ua.tunepoint.event.starter.handler.DomainEventHandlers;
import ua.tunepoint.event.starter.kafka.message.Message;
import ua.tunepoint.event.starter.kafka.message.MessageHeaders;

@Slf4j
@RequiredArgsConstructor
public class KafkaEventConsumer implements MessageListener<String, Message> {

    private final DomainEventHandlers eventHandlers;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(ConsumerRecord<String, Message> record) {
        try {
            var message = record.value();
            String domain = message.getRequiredHeader(MessageHeaders.DOMAIN_TYPE),
                    event = message.getRequiredHeader(MessageHeaders.EVENT_TYPE);

            for (var handler: eventHandlers.forEvent(domain, event)) {
                handler.invoke((DomainEvent) objectMapper.convertValue(message.getPayload(), handler.eventClass()));
            }
        } catch (Exception ex) {
            log.error("Error occurred while handling record", ex);
        }
    }
}
