package gonearewe;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, ASTree> env = new HashMap<>();
    private Environment parent;

    Environment() {
    }

    Environment(Environment old, String newKey, ASTree newVal) {
        env.put(newKey, newVal);
        parent = old;
    }

    //    Environment(Environment parent) {
    //        this.parent = parent;
    //    }

    public ASTree get(String key) {
        if (env.containsKey(key)) {
            return env.get(key);
        }
        if (parent != null) {
            return parent.get(key);
        }

        return null;
    }

    public void put(String key, ASTree val) {
        env.put(key, val);
    }

    public void setParent(Environment parent) {
        this.parent = parent;
    }
}
