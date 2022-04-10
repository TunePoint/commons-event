package ua.tunepoint.event.starter.registry;

import ua.tunepoint.event.model.DomainEventType;
import ua.tunepoint.event.starter.DomainRelation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DomainRegistry {

    // domain: (event_name: event_type)
    private final Map<String, EventRegistry> domainEventRegistry = new HashMap<>();

    private final Map<DomainRelation, Set<String>> domainRelationRegistry = new HashMap<>();

    public void register(String domain, Set<DomainEventType> events, Set<DomainRelation> relations) {
        var eventRegistry = new EventRegistry(events);

        domainEventRegistry.put(domain, eventRegistry);
        relations.forEach((relation) -> {
            domainRelationRegistry.compute(relation, (k, v) -> {
                v = v == null ? new HashSet<>() : v;
                v.add(domain);
                return v;
            }) ;
        });
    }

    public EventRegistry domainEvents(String domain) {
        return domainEventRegistry.get(domain);
    }

    public boolean isDomainRegistered(String domain) {
        return domainEventRegistry.containsKey(domain);
    }

    public Set<String> domains() {
        return domainEventRegistry.keySet();
    }

    public Set<String> domainsWithRelation(DomainRelation domainRelation) {
        return Collections.unmodifiableSet(domainRelationRegistry.getOrDefault(domainRelation, Collections.emptySet()));
    }
}
