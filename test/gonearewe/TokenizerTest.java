package gonearewe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

class TokenizerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void tokenize() throws Exception {
        var input = new FileInputStream("D:\\MyProjects\\lisp-interpreter\\test_cases\\eval.lisp");
        var t = new Tokenizer(input);
        for (var token : t.tokenize()) {
            System.out.println(token.toString());
        }
    }
}