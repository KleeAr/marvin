package ar.com.klee.marvin.voiceControl.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.klee.marvin.activities.CameraActivity;

/**
 * @author msalerno
 */
public class CommandHandlerContext {

    private Map<String, Object> contextMap = new HashMap<>();

    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    public <T> List<T> getList(String key, Class<T> elementsClass) {
        return (List<T>) get(key, List.class);
    }

    public String getString(String key) {
        return get(key, String.class);
    }

    private <T> T get(String key, Class<T> clazz) {
        if(!contextMap.containsKey(key)) {
            throw new RuntimeException("Expected key: " + key + " in context, but it wasn't");
        }
        return (T) contextMap.get(key);
    }

    public CommandHandlerContext put(String key, Object value) {
        contextMap.put(key, value);
        return this;
    }

    public boolean containsKey(String key) {
        return contextMap.containsKey(key);
    }

    public <T> T getObject(String key, Class<T> objectClass) {
        return get(key, objectClass);
    }
}
