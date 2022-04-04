package ua.tunepoint.event;

public interface EventConstructor<A ,T> {

    ServiceEvent<A, T> construct(A actor, T target);
}
