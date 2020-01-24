package gonearewe;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, ASTTree> env = new HashMap<>();
    private Environment parent;

    Environment(String key, ASTTree val) {
        env.put(key, val);
    }

    public ASTTree get(String key) {
        if (env.containsKey(key)) {
            return env.get(key);
        }
        if (parent != null) {
            return parent.get(key);
        }

        return null;
    }

    public void put(String key, ASTTree val) {
        env.put(key, val);
    }
}
