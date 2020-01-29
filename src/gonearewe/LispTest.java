package gonearewe;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

class LispTest {
    static final String testsPath = "D:\\MyProjects\\lisp-interpreter\\test_cases";
    ArrayList<FileInputStream> testFiles = new ArrayList<>();
    // expected results are given on the first line of each test file
    ArrayList<String> expectedResults = new ArrayList<>();
    int count; // increase as each test file is passed, serve as index of testFiles and expectedResults
    int numTestFile;

    public static void main(String[] args) throws IOException {
        var t = new LispTest();
        t.setUp();
        while (t.count != t.numTestFile) {
            t.evalAndPrint(t.count);
            t.count++;
        }
    }

    void setUp() throws IOException {
        File[] dirs = new File(testsPath).listFiles();
        for (var e : dirs) {
            if (e.isDirectory()) {
                for (var f : e.listFiles()) {
                    testFiles.add(new FileInputStream(f));
                    numTestFile++;
                }
            } else {
                testFiles.add(new FileInputStream(e));
                numTestFile++;
            }
        }

        for (var test : testFiles) {
            var s = new StringBuilder();
            while (true) {
                var c = (char) test.read();
                if (c == '\r') {
                    test.read(); // pass '\n'
                    break;
                }

                s.append(c);
            }

            expectedResults.add(s.toString());
        }
    }

    void evalAndPrint(int cnt) {
        var input = testFiles.get(cnt);
        var t = new Tokenizer(input);
        try {
            ArrayList<ASTree> topRoots = Parser.parse(t.tokenize());
            System.out.println("ID " + cnt + " " + expectedResults.get(cnt) + " " + Lisp.evalAndPrint(topRoots));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}