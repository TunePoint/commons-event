package ua.tunepoint.event.model;

public interface DomainEventType {

    String getName();

    Class<?> getType();
}
