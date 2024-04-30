package org.bgdnstc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    private static int index = 0;
    private static String[] line = null;
    private static final HashMap<String, String[]> identifiers = new HashMap<>();
    private static Integer identifierIndex = 0;

    private Parser() {
    }

    public static void parse(String path) {
        final Scanner scanner = new Scanner(SourceReader.readSource(Path.of(path)));
        final String className = WriterClass.pathToClassName(path);
        BytecodeGenerator.createClass(className);
        while (scanner.hasNextLine()) {
            index = 0;
            line = Tokenizer.tokenize(scanner.nextLine());
            statement();
        }
        WriterClass.writeClass(className, BytecodeGenerator.closeClass());
    }

    private static Symbol nextSymbol() {
        String token = line[index];
        if (token != null) {
            switch (token) {
                case "=":
                    return Symbol.EQUALS;
                case "ADD":
                    return Symbol.ADD;
                case "SUB":
                    return Symbol.SUB;
                case "MUL":
                    return Symbol.MUL;
                case "DIV":
                    return Symbol.DIV;
                case "loop":
                    return Symbol.LOOP;
                case "while":
                    return Symbol.WHILE;
                case "int":
                    return Symbol.TYPE_INT;
                case "float":
                    return Symbol.TYPE_FLOAT;
                case "string":
                    return Symbol.TYPE_STRING;
                case "Socket":
                    return Symbol.SOCKET;
                case "UDPServer":
                    return Symbol.UDP_SERVER;
                case "UDPClient":
                    return Symbol.UDP_CLIENT;
                case "print":
                    return Symbol.PRINT;
                case "send":
                    return Symbol.SEND;
                case "receive":
                    return Symbol.RECEIVE;
                case ".":
                    return Symbol.DOT;
                case "(":
                    return Symbol.L_PARENTHESIS;
                case ")":
                    return Symbol.R_PARENTHESIS;
                case "{":
                    return Symbol.L_BRACKET;
                case "}":
                    return Symbol.R_BRACKET;
                case "@":
                    return Symbol.AT;
                default:
                    if (token.matches("\\d*")) {
                        return Symbol.INT;
                    } else if (token.matches("^\\d*\\.\\d*$")) {
                        return Symbol.FLOAT;
                    } else if (token.matches("^[a-zA-Z].*")) {
                        return Symbol.IDENTIFIER;
                    } else if (token.matches("^\".*\"$")) {
                        return Symbol.STRING;
                    } else {
                        throw new IllegalArgumentException("Unexpected token received.");
                    }
            }
        } else {
            index = 0;
            return Symbol.EOL;
        }
    }

    private static void statement() {
        if (check(Symbol.SOCKET)) {
            match(Symbol.SOCKET);
            match(Symbol.IDENTIFIER);
            identifiers.put(line[1], new String[]{DatagramSocket.class.toString(), (++identifierIndex).toString()});
            match(Symbol.EQUALS);
            expression(1);
        } else if (check(Symbol.TYPE_INT)) {
            match(Symbol.TYPE_INT);
            match(Symbol.IDENTIFIER);
            match(Symbol.EQUALS);
            identifiers.put(line[1], new String[]{int.class.toString(), (++identifierIndex).toString()});
            expression(2);
        } else if (check(Symbol.INT)) {
            expression(3);
        } else if (check(Symbol.IDENTIFIER)) {
            expression(0);
        } else if (check(Symbol.PRINT)) {
            match(Symbol.PRINT);
            if (check(Symbol.INT)) {
                // TODO
                BytecodeGenerator.printGetStatic();
                expression(3);
                BytecodeGenerator.printInvokeVirtualInt();
            }
        } else {
            throw new IllegalArgumentException("Unexpected token received. This is not a statement.");
        }
    }

    private static void expression(int path) {
        if (path == 1) {
            if (check(Symbol.UDP_SERVER)) {
                match(Symbol.UDP_SERVER);
                match(Symbol.AT);
                match(Symbol.INT);
                BytecodeGenerator.createServerSocket(Integer.parseInt(line[index - 1]), identifierIndex);
            } else if (check(Symbol.UDP_CLIENT)) {
                match(Symbol.UDP_CLIENT);
                match(Symbol.AT);
                match(Symbol.INT);
            } else {
                throw new IllegalArgumentException("Unexpected token received. Expected: UDPServer / UDPClient" + ", received token: " + line[index] + ".");
            }
        } else if (path == 2) {
            match(Symbol.INT);
            if (line.length == index) {
                BytecodeGenerator.pushByteInt(Integer.parseInt(line[index - 1]));
                BytecodeGenerator.storeInt(identifierIndex);
            } else if (check(Symbol.INT)) {
                match(Symbol.INT);
                if (check(Symbol.ADD)) {
                    match(Symbol.ADD);
                    BytecodeGenerator.addIntegers(Integer.parseInt(line[index - 2]), Integer.parseInt(line[index - 3]));
                    BytecodeGenerator.storeInt(identifierIndex);
                } else if (check(Symbol.SUB)) {
                    match(Symbol.SUB);
                    // TODO handle operation
                } else if (check(Symbol.MUL)) {
                    match(Symbol.MUL);
                } else if (check(Symbol.DIV)) {
                    match((Symbol.DIV));
                } else {
                    match(nextSymbol());
                }
            }
        } else if (path == 3) {
            match(Symbol.INT);
            if (line.length == index) {
                BytecodeGenerator.pushByteInt(Integer.parseInt(line[index - 1]));
            } else if (check(Symbol.INT)) {
                match(Symbol.INT);
                if (check(Symbol.ADD)) {
                    match(Symbol.ADD);
                    BytecodeGenerator.addIntegers(Integer.parseInt(line[index - 2]), Integer.parseInt(line[index - 3]));
                } else if (check(Symbol.SUB)) {
                    match(Symbol.SUB);
                    // TODO handle operation
                } else if (check(Symbol.MUL)) {
                    match(Symbol.MUL);
                } else if (check(Symbol.DIV)) {
                    match((Symbol.DIV));
                } else {
                    match(nextSymbol());
                }
            }
        } else if (path == 0) {
            if (check(Symbol.IDENTIFIER)) {
                String[] identifier = identifiers.getOrDefault(line[index], null);
                match(Symbol.IDENTIFIER);
                if (identifier != null) {
                    match(Symbol.DOT);
                    if (identifier[0].equals(DatagramSocket.class.toString())) {
                        if (check(Symbol.SEND)) {
                            if (identifier[0].equals(DatagramSocket.class.toString())) {
                                match(Symbol.SEND);
                                match(Symbol.STRING);
                            } else {
                                try {
                                    throw new NoSuchMethodException("UDP Client does not have such a method.");
                                } catch (NoSuchMethodException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else if (check(Symbol.RECEIVE)) {
                            if (identifier[0].equals(DatagramSocket.class.toString())) {
                                match(Symbol.RECEIVE);
                                identifierIndex++;
                                BytecodeGenerator.receiveUDP(identifierIndex, Integer.parseInt(identifier[1]));
                            } else {
                                try {
                                    throw new NoSuchMethodException("UDP Server does not have such a method.");
                                } catch (NoSuchMethodException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("Provided identifier does not exist or it has not been declared.");
                }
            }
        } else {
            throw new IllegalArgumentException("Unexpected tokens. Invalid expression.");
        }
    }

    private static boolean check(Symbol expected) {
        Symbol current = nextSymbol();
        return current.equals(expected);
    }

    private static void match(Symbol expected) {
        Symbol current = nextSymbol();
        if (current.equals(expected)) {
            index++;
        } else {
            throw new IllegalArgumentException("Unexpected token received. Expected token: " + expected + ", received token: " + line[index] + " (" + current + ").");
        }
    }
}
