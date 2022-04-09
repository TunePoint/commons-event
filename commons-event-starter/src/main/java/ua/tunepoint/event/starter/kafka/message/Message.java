package ua.tunepoint.event.starter.kafka.message;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private JsonNode payload;
    private Map<String, String> headers = new HashMap<>();

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getRequiredHeader(String name) {
        var value = getHeader(name);
        if (value == null) {
            throw new IllegalStateException("Header " + name + " is required");
        }
        return value;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
}
