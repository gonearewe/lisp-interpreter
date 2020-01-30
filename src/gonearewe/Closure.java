package gonearewe;

import java.util.ArrayList;

public class Closure {
    final ArrayList<String> params;
    final ASTree body;
    final Environment env;

    Closure(ArrayList<String> params, ASTree body, Environment env) {
        this.params = params;
        this.body = body;
        this.env = env;
    }

    @Override
    public String toString() {
        var s = new StringBuilder("Closure [ params: ");
        for (var param : params) {
            s.append(param).append(", ");
        }

        s.append("body: ").append(body.toString()).append(" ]");

        return s.toString();
    }
}
