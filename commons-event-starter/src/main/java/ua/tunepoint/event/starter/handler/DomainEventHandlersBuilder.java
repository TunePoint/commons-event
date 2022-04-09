package ua.tunepoint.event.starter.handler;

import ua.tunepoint.event.model.DomainEvent;
import ua.tunepoint.event.starter.registry.DomainRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DomainEventHandlersBuilder {

    private String currentDomain;
    private final List<DomainEventHandler> eventHandlers = new ArrayList<>();
    private final DomainRegistry registry;

    private DomainEventHandlersBuilder(DomainRegistry registry) {
        this.registry = registry;
    }

    public static DomainEventHandlersBuilder withRegistry(DomainRegistry registry) {
        return new DomainEventHandlersBuilder(registry);
    }

    public HandlersBuilder forDomain(String domain) {
        this.currentDomain = domain;
        return new HandlersBuilder();
    }

    public DomainEventHandlers build() {
        return new DomainEventHandlers(eventHandlers);
    }

    private DomainEventHandlers referenceBuild() {
        return build();
    }

    private List<DomainEventHandler> referenceHandlers() {
        return eventHandlers;
    }

    private HandlersBuilder referenceForDomain(String domain) {
        return forDomain(domain);
    }

    public class HandlersBuilder {

        private final List<DomainEventHandler> eventHandlers = new ArrayList<>();

        public <E extends DomainEvent> HandlersBuilder onEvent(Class<E> eventType, Consumer<E> handler) {
            var eventName = registry.domainEvents(currentDomain).eventWithType(eventType).getName();

            eventHandlers.add(new DomainEventHandler(currentDomain, eventName, ((Class<DomainEvent>) eventType), e -> handler.accept((E) e)));
            return this;
        }

        public HandlersBuilder forDomain(String domain) {
            referenceHandlers().addAll(eventHandlers);
            return referenceForDomain(domain);
        }

        public DomainEventHandlers build() {
            referenceHandlers().addAll(eventHandlers);
            return referenceBuild();
        }
    }
}
