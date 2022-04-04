package ua.tunepoint.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEvent<A, T> {

    private A actor;
    private T target;
    private LocalDateTime time;
}
