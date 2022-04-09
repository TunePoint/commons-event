package ua.tunepoint.event.starter.registry;

import ua.tunepoint.event.model.DomainEventType;

import java.util.HashSet;
import java.util.Set;

public class EventRegistry {

    private final Set<DomainEventType> eventTypes = new HashSet<>();

    public EventRegistry(Set<DomainEventType> eventTypes) {
        this.eventTypes.addAll(eventTypes);
    }

    // TODO: apparently, the method is not needed, so class registry can be optimised by using map (type, name)
    public DomainEventType eventWithName(String name) {
        return eventTypes.stream().filter(it -> it.getName().equals(name)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Event with name '" + name + "' is not registered"));
    }

    public DomainEventType eventWithType(Class<?> clazz) {
        return eventTypes.stream().filter(it -> it.getType().equals(clazz)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Event with type '" + clazz.getSimpleName() + "' is not registered"));
    }
}
