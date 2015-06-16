package ar.com.klee.marvin.voiceControl.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author msalerno
 */
public class CommandHandlerContext {

    private Map<String, Object> contextMap = new HashMap<>();

    public <T> T get(String key, Class<T> clazz) {
        if(!contextMap.containsKey(key)) {
            throw new RuntimeException("Expected key: " + key + " in context, but it wasn't");
        }
        return (T) contextMap.get(key);
    }

    public void put(String key, Object value) {
        contextMap.put(key, value);
    }

    public boolean containsKey(String key) {
        return contextMap.containsKey(key);
    }
}
