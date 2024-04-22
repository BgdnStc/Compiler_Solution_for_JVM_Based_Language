package org.bgdnstc;

import java.nio.file.Path;
import java.util.Scanner;

public class Parser {

    public static void parse(String path) {
        final Scanner scanner = new Scanner(SourceReader.readSource(Path.of(path)));
        while (scanner.hasNextLine()) {
            String[] line = Tokenizer.tokenize(scanner.nextLine());
        }
    }

    private Symbol nextSymbol() {
        return null;
    }

    private boolean match(Symbol expected) {
        Symbol current = nextSymbol();
        if (current.equals(expected)) {
            return true;
        } else {
            throw new IllegalArgumentException("Invalid token! Expected token: " + expected + ", received:" + current + "(" + ")");
        }
    }
}
