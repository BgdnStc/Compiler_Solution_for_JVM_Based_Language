package org.bgdnstc;

import java.util.ArrayList;
import java.util.Scanner;

public class Tokenizer {

    public static String[] tokenize(String line) {
        StringBuilder token = new StringBuilder();
        ArrayList<String> tokens = new ArrayList<>();
        Scanner scanner = new Scanner(line).useDelimiter("");
        while(scanner.hasNext()) {
            String current = scanner.next();
            if (current.matches("\\d") && token.isEmpty()) {
                token.append(current);
                while (scanner.hasNext() && !current.equals(" ")) {
                    current = scanner.next();
                    if (!current.equals(" ")) {
                        token.append(current);
                    }
                }
                tokens.add(token.toString());
                token.setLength(0);
            } else {
                switch (current) {
                    case "\"" -> {
                        token.setLength(0);
                        token.append(current);
                        do {
                            current = scanner.next();
                            token.append(current);
                        } while (!current.equals("\""));
                    }
                    case " " -> {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                    case "." -> {
                        tokens.add(token.toString());
                        tokens.add(".");
                        token.setLength(0);
                    }
                    case "(" -> {
                        tokens.add(token.toString());
                        tokens.add("(");
                        token.setLength(0);
                    }
                    case ")" -> {
                        tokens.add(token.toString());
                        tokens.add(")");
                        token.setLength(0);
                    }
                    case "{" -> {
                        tokens.add(token.toString());
                        tokens.add("{");
                        token.setLength(0);
                    }
                    case "}" -> {
                        tokens.add(token.toString());
                        tokens.add("}");
                        token.setLength(0);
                    }
                    default -> token.append(current);
                }
            }
        }
        if (!token.isEmpty()) {
            tokens.add(token.toString());
        }
        String[] tokensArray = new String[tokens.size()];
        tokens.toArray(tokensArray);
        return tokensArray;
    }
}
