import java.util.ArrayList;

public class Tokenizer {
    public static String[] tokenize(String line) {
        StringBuilder token = new StringBuilder();
        ArrayList<String> tokens = new ArrayList<>();
        Integer index = 0;
        char[] inputChars = line.toCharArray();
        for (char c : inputChars) {
            if (c == ' ') {
                tokens.add(token.toString());
                token.setLength(0);
            } else if (c == '(') {
                tokens.add(token.toString());
                tokens.add("(");
                token.setLength(0);
            } else if (c == ')') {
                tokens.add(token.toString());
                tokens.add(")");
                token.setLength(0);
            } else if (c == '{') {
                tokens.add(token.toString());
                tokens.add("{");
                token.setLength(0);
            } else if (c == '}') {
                tokens.add(token.toString());
                tokens.add("}");
                token.setLength(0);
            } else {
                token.append(c);
            }
        }

        String[] tokensArray = new String[tokens.size()];
        tokens.toArray(tokensArray);
        return tokensArray;
    }
}
