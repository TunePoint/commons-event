package ua.tunepoint.event;

import java.time.LocalDateTime;

public class DefaultEventConstructor implements EventConstructor<Object, Object> {

    @Override
    public ServiceEvent<Object, Object> construct(Object actor, Object target) {

        return DefaultServiceEvent.builder()
                .actor(actor)
                .target(target)
                .time(LocalDateTime.now())
                .build();
    }
}
