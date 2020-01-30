package gonearewe;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(@NotNull String[] args) throws Exception {
        //        var input = new FileInputStream("D:\\MyProjects\\lisp-interpreter\\test_cases\\func_test\\atom_false.lisp");
        //        var t = new Tokenizer(input);
        //        try {
        //            ArrayList<ASTree> root = Parser.parse(t.tokenize());
        //            System.out.println(Lisp.evalAndPrint(root));
        //        } catch (Exception e) {
        //            System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
        //        }
        if (args.length == 2 && args[0].equals("-f")) {
            System.out.println(evalFile(args[1]));
            return;
        }

        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            showHelpInfo();
            return;
        }

        if (args.length == 0) {
            startREPL();
        }

        System.out.println("Wrong way to use !!!");
        showHelpInfo();
    }

    private static void showHelpInfo() {
        System.out.println("This is a DIY toy Lisp interpreter !");
        System.out.println("You shouldn't expect it as a real one, " + "though it can handle simple statements and expressions");
        System.out.println("Usage: ");
        System.out.println("    run directly to access a REPL");
        System.out.println("    -h or --help  : get help info, yeh, that's what you're seeing... ");
        System.out.println("    -f <filepath> : parse given source file and print result");
    }

    @NotNull
    private static String evalFile(String filepath) {
        try {
            var src = new FileInputStream(filepath);
            var tokenizer = new Tokenizer(src);
            var env = new Environment();
            ArrayList<ASTree> topRoots = Parser.parse(tokenizer.tokenize());
            return Lisp.evalAndPrint(topRoots, env);
        } catch (FileNotFoundException e) {
            return "unable to find source file: " + e.getMessage();
        } catch (Exception e) {
            return "unable to parse: " + e.getMessage();
        }
    }

    private static void startREPL() {
        var sc = new Scanner(System.in);
        var env = new Environment(); // Environment is given on the top level so that it can be altered
        while (true) {
            System.out.print(">>>");
            try {
                var input = new ByteArrayInputStream(sc.nextLine().getBytes());
                var tokenizer = new Tokenizer(input);
                ArrayList<ASTree> topRoots = Parser.parse(tokenizer.tokenize());
                System.out.println(Lisp.evalAndPrint(topRoots, env));
            } catch (Exception e) {
                System.out.println("unable to parse: " + e.getMessage());
            }
        }
    }
}