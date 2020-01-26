package gonearewe;

import java.util.ArrayList;

public class Closure {
    ArrayList<String> params;
    ASTTree body;
    Environment env;

    Closure(ArrayList<String> params, ASTTree body, Environment env) {
        this.params = params;
        this.body = body;
        this.env = env;
    }

    public String toString() {
        var s = new StringBuilder("Closure [ params: ");
        for (var param : params) {
            s.append(param).append(", ");
        }

        s.append("body: ").append(body.toString()).append(" ]");

        return s.toString();
    }
}
