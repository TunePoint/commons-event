package ua.tunepoint.event.starter.handler;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DomainEventHandlers {

    private final List<DomainEventHandler> handlers;

    public List<DomainEventHandler> forEvent(String domain, String event) {
        return handlers.stream().filter(h -> h.handles(domain, event)).collect(Collectors.toList());
    }
}
