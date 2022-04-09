package ua.tunepoint.event.starter.handler;

import lombok.RequiredArgsConstructor;
import ua.tunepoint.event.model.DomainEvent;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class DomainEventHandler {

    private final String domainType;
    private final String eventType;
    private final Class<DomainEvent> eventClass;
    private final Consumer<DomainEvent> handler;

    public boolean handles(String domainType, String eventType) {
        return this.domainType.equals(domainType) && this.eventType.equals(eventType);
    }

    public Class<?> eventClass() {
        return this.eventClass;
    }

    public void invoke(DomainEvent event) {
        handler.accept(event);
    }
}
