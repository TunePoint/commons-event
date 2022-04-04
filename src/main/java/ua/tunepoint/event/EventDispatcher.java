package ua.tunepoint.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;
import ua.tunepoint.event.annotation.DispatchEvent;
import ua.tunepoint.event.annotation.EventActor;
import ua.tunepoint.event.annotation.EventTarget;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class EventDispatcher {

    private final KafkaTemplate<Object, Object>  kafkaTemplate;
    private final DefaultEventConstructor defaultEventConstructor;

    @Pointcut("@annotation(ua.tunepoint.event.annotation.DispatchEvent)")
    public void dispatchEvent() { }

    @AfterReturning(value = "dispatchEvent()", returning = "returnedValue")
    public void triggerDispatch(JoinPoint joinPoint, Object returnedValue) {

        try {
            if (joinPoint.getSignature() instanceof MethodSignature signature) {
                var method = signature.getMethod();

                if (method.isAnnotationPresent(DispatchEvent.class)) {
                    var annotation = method.getAnnotation(DispatchEvent.class);

                    var topic = annotation.value();
                    var actor = extractActor(method, joinPoint.getArgs());
                    var target = extractField(method, joinPoint.getArgs(), returnedValue);

                    kafkaTemplate.send(topic, defaultEventConstructor.construct(actor, target));
                } else {
                    log.error("Annotation DispatchEvent is not readable?");
                }
            }

        } catch (Exception ex) {
            log.error("Error occurred while processing dispatch", ex);
        }
    }


    private Object extractField(Method method, Object[] args, Object returned) throws NoSuchFieldException, IllegalAccessException {
        if (method.isAnnotationPresent(EventTarget.class)) {
            if (returned == null) {
                throw new RuntimeException("Returned value should not be null");
            }

            var annotation = method.getAnnotation(EventTarget.class);
            return extractField(returned, annotation.value());
        }

        var parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            var annotations = parameterAnnotations[i];

            for (var annotation: annotations) {
                if (annotation instanceof EventTarget eventTarget) {
                    return extractField(args[i], eventTarget.value());
                }
            }
        }

        throw new RuntimeException("Target was not found");
    }

    private Object extractActor(Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {

        var parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            var annotations = parameterAnnotations[i];

            for (var annotation: annotations) {
                if (annotation instanceof EventActor eventActor) {
                    return extractField(args[i], eventActor.value());
                }
            }
        }

        throw new RuntimeException("Actor was not found");
    }

    private Object extractField(Object targetHolder, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (StringUtils.hasText(fieldName)) {
            var clazz = targetHolder.getClass();
            var field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);
            return field.get(targetHolder);
        }

        return targetHolder;
    }
}
