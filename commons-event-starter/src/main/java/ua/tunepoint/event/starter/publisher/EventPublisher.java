package ua.tunepoint.event.starter.publisher;

import ua.tunepoint.event.model.DomainEvent;

import java.util.List;

public interface EventPublisher {

    <T extends DomainEvent> void publish(String domain, List<T> event);
}
