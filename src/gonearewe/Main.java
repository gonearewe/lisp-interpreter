package gonearewe;

import java.io.FileInputStream;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        var input = new FileInputStream("D:\\MyProjects\\lisp-interpreter\\test_cases\\func_test\\atom_false.lisp");
        var t = new Tokenizer(input);
        try {
            ASTTree root = Parser.parse(t.tokenize());
            System.out.println(Lisp.evalAndPrint(root));
        } catch (Exception e) {
            System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
    }
}