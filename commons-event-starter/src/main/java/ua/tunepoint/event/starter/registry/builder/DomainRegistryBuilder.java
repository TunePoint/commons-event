package ua.tunepoint.event.starter.registry.builder;

import ua.tunepoint.event.model.DomainEventType;
import ua.tunepoint.event.starter.DomainRelation;
import ua.tunepoint.event.starter.registry.DomainRegistry;

import java.util.Set;

public class DomainRegistryBuilder {

    private final DomainRegistry domainRegistry = new DomainRegistry();

    public DomainRegistryBuilder register(String domain, DomainEventType[] eventTypes, DomainRelation relation) {
        domainRegistry.register(domain, Set.of(eventTypes), relation);
        return this;
    }

    public DomainRegistryBuilder register(String domain, Set<DomainEventType> eventTypes, DomainRelation relation) {
        domainRegistry.register(domain, eventTypes, relation);
        return this;
    }

    public DomainRegistry build() {
        return domainRegistry;
    }
}
