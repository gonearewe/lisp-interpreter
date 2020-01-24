package gonearewe;

import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        var input = new FileInputStream("D:\\MyProjects\\lisp-interpreter\\test_cases\\eval.lisp");
        var t = new Tokenizer(input);
        for (var token : t.tokenize()) {
            System.out.println(token.toString());
        }
    }
}