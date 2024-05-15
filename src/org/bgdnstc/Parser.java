package org.bgdnstc;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.net.DatagramSocket;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class Parser {
    // index of the current token
    private static int index = 0;
    // current line as token array
    private static String[] line = null;
    // current available index for variables
    private static Integer identifierIndex = 0;
    // [ identifier, {class, identifier index} ]
    private static final LinkedHashMap<String, String[]> identifiers = new LinkedHashMap<>();
    // [ identifier, {port, address} ]
    private static final HashMap<String, String[]> sockets = new HashMap<>();
    // stack of labels, last label is the current loop
    private static final Stack<Label> labelStack = new Stack<>();

    // private constructor for preventing instantiation
    private Parser() {
    }

    // parse method dictates the execution flow of the compiler
    public static void parse(String path) {
        identifierIndex = 0;
        final Scanner scanner = new Scanner(SourceReader.readSource(Path.of(path)));
        final String className = WriterClass.pathToClassName(path);
        BytecodeGenerator.createClass(className);
        while (scanner.hasNextLine()) {
            index = 0;
            line = Tokenizer.tokenize(scanner.nextLine());
            statement();
        }
        WriterClass.writeClass(className, BytecodeGenerator.closeClass(labelStack));
    }

    // return the symbol of next token
    private static Symbol nextSymbol() {
        String token;
        if (index < line.length) {
            token = line[index];
        } else {
            token = null;
        }
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
            return Symbol.EOL;
        }
    }

    // validates the grammar of the statement
    private static void statement() {
        if (check(Symbol.SOCKET)) {
            match(Symbol.SOCKET);
            match(Symbol.IDENTIFIER);
            identifiers.put(line[1], new String[]{Type.getInternalName(DatagramSocket.class), (++identifierIndex).toString()});
            match(Symbol.EQUALS);
            expression(1);
        } else if (check(Symbol.TYPE_INT)) {
            match(Symbol.TYPE_INT);
            match(Symbol.IDENTIFIER);
            match(Symbol.EQUALS);
            identifiers.put(line[1], new String[]{Type.getInternalName(int.class), (++identifierIndex).toString()});
            expression(2);
        } else if (check(Symbol.TYPE_FLOAT)) {
            match(Symbol.TYPE_FLOAT);
            match(Symbol.IDENTIFIER);
            match(Symbol.EQUALS);
            identifiers.put(line[1], new String[]{Type.getInternalName(float.class), (++identifierIndex).toString()});
            expression(6);
        } else if (check(Symbol.TYPE_STRING)) {
            match(Symbol.TYPE_STRING);
            match(Symbol.IDENTIFIER);
            match(Symbol.EQUALS);
            identifiers.put(line[1], new String[]{Type.getInternalName(String.class), (++identifierIndex).toString()});
            expression(8);
        } else if (check(Symbol.INT)) {
            expression(3);
        } else if (check(Symbol.IDENTIFIER)) {
            expression(4);
        } else if (check(Symbol.PRINT)) {
            match(Symbol.PRINT);
            if (check(Symbol.INT)) {
                // TODO
                BytecodeGenerator.printGetStatic();
                expression(3);
                BytecodeGenerator.printInvokeVirtualInt();
            } else if (check(Symbol.FLOAT)) {
                BytecodeGenerator.printGetStatic();
                expression(7);
                BytecodeGenerator.printInvokeVirtualFloat();
            } else if (check(Symbol.STRING)) {
                BytecodeGenerator.printGetStatic();
                expression(9);
                BytecodeGenerator.printInvokeVirtualString();
            } else if (check(Symbol.IDENTIFIER)) {
                expression(5);
            }
        } else if(check(Symbol.LOOP)) {
            match(Symbol.LOOP);
            match(Symbol.L_BRACKET);
            ArrayList<Object> variablesTypes = new ArrayList<>();
            for (Map.Entry<String, String[]> variable : identifiers.entrySet()) {
                if (Objects.equals(variable.getValue()[0], "int")) {
                    variablesTypes.add(Opcodes.INTEGER);
                } else if (Objects.equals(variable.getValue()[0], "float")) {
                    variablesTypes.add(Opcodes.FLOAT);
                } else {
                    variablesTypes.add(variable.getValue()[0]);
                }
            }
            System.out.println(variablesTypes.size());
//            Collections.reverse(variablesTypes);
            System.out.println(Arrays.toString(variablesTypes.toArray()));
//            System.out.println(Type.getInternalName(int.class));
//            System.out.println(Opcodes.INTEGER);
            labelStack.add(BytecodeGenerator.visitLabel(variablesTypes.size(), variablesTypes));
        } else if (check(Symbol.R_BRACKET)) {
            match(Symbol.R_BRACKET);
            BytecodeGenerator.gotoLabel(labelStack.pop());
        } else {
            throw new IllegalArgumentException("Unexpected token received. This is not a statement.");
        }
    }

    // validates the grammar of the provided expression
    private static void expression(int expressionPath) {
        if (expressionPath == 1) {
            // socket variable initialisation
            if (check(Symbol.UDP_SERVER)) {
                match(Symbol.UDP_SERVER);
                match(Symbol.AT);
                match(Symbol.INT);
                if (!check(Symbol.EOL)) {
                    match(Symbol.STRING);
                    sockets.put(line[1], new String[]{line[index - 2], line[index - 1]});
                    BytecodeGenerator.createServerSocket(Integer.parseInt(line[index - 2]), line[index -1].substring(1, line[index - 1].length() - 1), identifierIndex);
                } else {
                    sockets.put(line[1], new String[]{line[index - 1]});
                    BytecodeGenerator.createServerSocket(Integer.parseInt(line[index - 1]), null, identifierIndex);
                }
            } else if (check(Symbol.UDP_CLIENT)) {
                match(Symbol.UDP_CLIENT);
                match(Symbol.AT);
                match(Symbol.INT);
                if (!check(Symbol.EOL)) {
                    match(Symbol.STRING);
                    sockets.put(line[1], new String[]{line[index - 2], line[index - 1]});
                    BytecodeGenerator.createClientSocket(null, line[index -1].substring(1, line[index - 1].length() - 1), identifierIndex);
                } else {
                    sockets.put(line[1], new String[]{line[index - 1]});
                    BytecodeGenerator.createClientSocket(null, null, identifierIndex);
                }
            } else {
                throw new IllegalArgumentException("Unexpected token received. Expected: UDPServer / UDPClient" + ", received token: " + line[index] + ".");
            }
        } else if (expressionPath == 2) {
            // int value or int operation with storing the result into a variable
            match(Symbol.INT);
            if (line.length == index) {
                BytecodeGenerator.pushShort(Integer.parseInt(line[index - 1]));
                BytecodeGenerator.storeInt(identifierIndex);
            } else if (check(Symbol.INT)) {
                match(Symbol.INT);
                if (check(Symbol.ADD)) {
                    match(Symbol.ADD);
                    BytecodeGenerator.addIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                    BytecodeGenerator.storeInt(identifierIndex);
                } else if (check(Symbol.SUB)) {
                    match(Symbol.SUB);
                    BytecodeGenerator.subtractIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                    BytecodeGenerator.storeInt(identifierIndex);
                } else if (check(Symbol.MUL)) {
                    match(Symbol.MUL);
                    BytecodeGenerator.multiplyIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                    BytecodeGenerator.storeInt(identifierIndex);
                } else if (check(Symbol.DIV)) {
                    match((Symbol.DIV));
                    BytecodeGenerator.divideIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                    BytecodeGenerator.storeFloat(identifierIndex);
                } else {
                    match(nextSymbol());
                }
            }
        } else if (expressionPath == 3) {
            // int value or int operation without storing the result into a variable
            match(Symbol.INT);
            if (line.length == index) {
                BytecodeGenerator.pushShort(Integer.parseInt(line[index - 1]));
            } else if (check(Symbol.INT)) {
                match(Symbol.INT);
                if (check(Symbol.ADD)) {
                    match(Symbol.ADD);
                    BytecodeGenerator.addIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                } else if (check(Symbol.SUB)) {
                    match(Symbol.SUB);
                    BytecodeGenerator.subtractIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                } else if (check(Symbol.MUL)) {
                    match(Symbol.MUL);
                    BytecodeGenerator.multiplyIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                } else if (check(Symbol.DIV)) {
                    match((Symbol.DIV));
                    BytecodeGenerator.divideIntegers(Integer.parseInt(line[index - 3]), Integer.parseInt(line[index - 2]));
                } else {
                    match(nextSymbol());
                }
            }
        } else if (expressionPath == 4) {
            // checking identifier methods
            if (check(Symbol.IDENTIFIER)) {
                String[] identifier = identifiers.getOrDefault(line[index], null);
                match(Symbol.IDENTIFIER);
                if (identifier != null) {
                    if (identifier[0].equals(DatagramSocket.class.toString())) {
                        if (check(Symbol.DOT)) {
                            match(Symbol.DOT);
                            if (check(Symbol.SEND)) {
                                if (identifier[0].equals(DatagramSocket.class.toString())) {
                                    match(Symbol.SEND);
                                    if (check(Symbol.STRING)) {
                                        match(Symbol.STRING);
                                        identifierIndex++;
                                        if (sockets.get(line[index - 4]).length > 1) {
                                            BytecodeGenerator.sendUDP(identifierIndex, Integer.parseInt(identifier[1]), line[index - 1].substring(1, line[index - 1].length() - 1), Integer.parseInt(sockets.get(line[index - 4])[0]), sockets.get(line[index - 4])[1].substring(1, sockets.get(line[index - 4])[1].length() - 1));
                                        } else {
                                            BytecodeGenerator.sendUDP(identifierIndex, Integer.parseInt(identifier[1]), line[index - 1].substring(1, line[index - 1].length() - 1), Integer.parseInt(sockets.get(line[index - 4])[0]), null);
                                        }
                                    } else if (check(Symbol.IDENTIFIER)) {
                                        match(Symbol.IDENTIFIER);
                                        String[] stringIdentifier = identifiers.getOrDefault(line[index - 1], null);
                                        if (stringIdentifier != null && stringIdentifier[0].equals(String.class.toString())) {
                                            identifierIndex++;
                                            if (sockets.get(line[index - 4]).length > 1) {
                                                BytecodeGenerator.sendIdentifierUDP(identifierIndex, Integer.parseInt(identifier[1]), Integer.parseInt(stringIdentifier[1]), Integer.parseInt(sockets.get(line[index - 4])[0]), sockets.get(line[index - 4])[1].substring(1, sockets.get(line[index - 4])[1].length() - 1));
                                            } else {
                                                BytecodeGenerator.sendIdentifierUDP(identifierIndex, Integer.parseInt(identifier[1]), Integer.parseInt(stringIdentifier[1]), Integer.parseInt(sockets.get(line[index - 4])[0]), null);
                                            }
                                        } else {
                                            throw new IllegalArgumentException("Expected string / string identifier after 'send' method");
                                        }
                                    }
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
                        } else if (check(Symbol.EQUALS)) {
                            // TODO
                            match(Symbol.EQUALS);
                        }
                    } else if (identifier[0].equals(int.class.toString())) {
                        if (check(Symbol.EQUALS)) {
                            match(Symbol.EQUALS);
                            match(Symbol.INT);
                            BytecodeGenerator.pushShort(Integer.parseInt(line[index - 1]));
                            BytecodeGenerator.storeInt(Integer.parseInt(identifier[1]));
                        } else {
                            throw new PatternSyntaxException("Expected a statement. Received an identifier.", "identifier + symbol", 1);
                        }
                    } else if (identifier[0].equals(float.class.toString())) {
                        if (check(Symbol.EQUALS)) {
                            match(Symbol.EQUALS);
                            match(Symbol.FLOAT);
                            BytecodeGenerator.pushConstantLdc(Float.parseFloat(line[index - 1]));
                            BytecodeGenerator.storeFloat(Integer.parseInt(identifier[1]));
                        } else {
                            throw new PatternSyntaxException("Expected a statement. Received an identifier.", "identifier + symbol", 1);
                        }
                    } else if (identifier[0].equals(String.class.toString())) {
                        if (check(Symbol.EQUALS)) {
                            match(Symbol.EQUALS);
                            match(Symbol.STRING);
                            BytecodeGenerator.pushConstantLdc(line[index - 1].substring(1, line[index -1].length() - 1));
                            BytecodeGenerator.storeString(Integer.parseInt(identifier[1]));
                        } else {
                            throw new PatternSyntaxException("Expected a statement. Received an identifier.", "identifier + symbol", 1);
                        }
                    }
                } else {
                    throw new RuntimeException("Provided identifier does not exist or it has not been declared.");
                }
            }
        } else if(expressionPath == 5) {
            // for printing variables
            expression(10);
            if (line[index - 1].equals("receive")) {
                BytecodeGenerator.printGetStatic();
                BytecodeGenerator.packetToString(identifierIndex);
                BytecodeGenerator.printInvokeVirtualString();
            }
        } else if (expressionPath == 6) {
            // float value or float operation with storing the result into a variable
            match(Symbol.FLOAT);
            if (line.length == index) {
                BytecodeGenerator.pushConstantLdc(Float.parseFloat(line[index - 1]));
                BytecodeGenerator.storeFloat(identifierIndex);
            } else if (check(Symbol.FLOAT)) {
                match(Symbol.FLOAT);
                if (check(Symbol.ADD)) {
                    match(Symbol.ADD);
                    BytecodeGenerator.addFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                    BytecodeGenerator.storeFloat(identifierIndex);
                } else if (check(Symbol.SUB)) {
                    match(Symbol.SUB);
                    BytecodeGenerator.subtractFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                    BytecodeGenerator.storeFloat(identifierIndex);
                } else if (check(Symbol.MUL)) {
                    match(Symbol.MUL);
                    BytecodeGenerator.multiplyFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                    BytecodeGenerator.storeFloat(identifierIndex);
                } else if (check(Symbol.DIV)) {
                    match((Symbol.DIV));
                    BytecodeGenerator.divideFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                    BytecodeGenerator.storeFloat(identifierIndex);
                } else {
                    match(nextSymbol());
                }
            }
        } else if (expressionPath == 7) {
            // float value or float operation without storing the result into a variable
            match(Symbol.FLOAT);
            if (line.length == index) {
                BytecodeGenerator.pushConstantLdc(Float.parseFloat(line[index - 1]));
            } else if (check(Symbol.FLOAT)) {
                match(Symbol.FLOAT);
                if (check(Symbol.ADD)) {
                    match(Symbol.ADD);
                    BytecodeGenerator.addFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                } else if (check(Symbol.SUB)) {
                    match(Symbol.SUB);
                    BytecodeGenerator.subtractFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                } else if (check(Symbol.MUL)) {
                    match(Symbol.MUL);
                    BytecodeGenerator.multiplyFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                } else if (check(Symbol.DIV)) {
                    match((Symbol.DIV));
                    BytecodeGenerator.divideFloats(Float.parseFloat(line[index - 3]), Float.parseFloat(line[index - 2]));
                } else {
                    match(nextSymbol());
                }
            }
        } else if (expressionPath == 8) {
            // string value with storing the value
            match(Symbol.STRING);
            BytecodeGenerator.pushConstantLdc(line[index - 1].substring(1, line[index -1].length() - 1));
            BytecodeGenerator.storeString(identifierIndex);
        } else if (expressionPath == 9) {
            // string value without storing the value
            match(Symbol.STRING);
            BytecodeGenerator.pushConstantLdc(line[index - 1].substring(1, line[index -1].length() - 1));
        } else if (expressionPath == 10) {
            // called by printing
            if (check(Symbol.IDENTIFIER)) {
                String[] identifier = identifiers.getOrDefault(line[index], null);
                match(Symbol.IDENTIFIER);
                if (identifier != null) {
                    if (identifier[0].equals(DatagramSocket.class.toString())) {
                        match(Symbol.DOT);
                        if (check(Symbol.SEND)) {
                            if (identifier[0].equals(DatagramSocket.class.toString())) {
                                match(Symbol.SEND);
                                if (check(Symbol.STRING)) {
                                    match(Symbol.STRING);
                                    identifierIndex++;
                                    if (sockets.get(line[index - 4]).length > 1) {
                                        BytecodeGenerator.sendUDP(identifierIndex, Integer.parseInt(identifier[1]), line[index - 1].substring(1, line[index - 1].length() - 1), Integer.parseInt(sockets.get(line[index - 4])[0]), sockets.get(line[index - 4])[1].substring(1, line[index - 4].length() - 1));
                                    } else {
                                        BytecodeGenerator.sendUDP(identifierIndex, Integer.parseInt(identifier[1]), line[index - 1].substring(1, line[index - 1].length() - 1), Integer.parseInt(sockets.get(line[index - 4])[0]), null);
                                    }
                                } else if (check(Symbol.IDENTIFIER)) {
                                    match(Symbol.IDENTIFIER);
                                    String[] stringIdentifier = identifiers.getOrDefault(line[index - 1], null);
                                    if (stringIdentifier != null && stringIdentifier[0].equals(String.class.toString())) {
                                        identifierIndex++;
                                        if (sockets.get(line[index - 4]).length > 1) {
                                            BytecodeGenerator.sendIdentifierUDP(identifierIndex, Integer.parseInt(identifier[1]), Integer.parseInt(stringIdentifier[1]), Integer.parseInt(sockets.get(line[index - 4])[0]), sockets.get(line[index - 4])[1].substring(1, sockets.get(line[index - 4])[1].length() - 1));
                                        } else {
                                            BytecodeGenerator.sendIdentifierUDP(identifierIndex, Integer.parseInt(identifier[1]), Integer.parseInt(stringIdentifier[1]), Integer.parseInt(sockets.get(line[index - 4])[0]), null);
                                        }
                                    } else {
                                        throw new IllegalArgumentException("Expected string / string identifier after 'send' method");
                                    }
                                }
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
                    } else if (identifier[0].equals(int.class.toString())) {
                        BytecodeGenerator.printGetStatic();
                        BytecodeGenerator.loadInteger(Integer.parseInt(identifier[1]));
                        BytecodeGenerator.printInvokeVirtualInt();
                    } else if (identifier[0].equals(float.class.toString())) {
                        BytecodeGenerator.printGetStatic();
                        BytecodeGenerator.loadFloat(Integer.parseInt(identifier[1]));
                        BytecodeGenerator.printInvokeVirtualFloat();
                    } else if (identifier[0].equals(String.class.toString())) {
                        BytecodeGenerator.printGetStatic();
                        BytecodeGenerator.loadReference(Integer.parseInt(identifier[1]));
                        BytecodeGenerator.printInvokeVirtualString();
                    }
                } else {
                    throw new RuntimeException("Provided identifier does not exist or it has not been declared.");
                }
            }
        } else {
            throw new IllegalArgumentException("Unexpected tokens. Invalid expression.");
        }
    }

    // checks the type of the next symbol
    private static boolean check(Symbol expected) {
        Symbol current = nextSymbol();
        return current.equals(expected);
    }

    // matches the type of the next symbol
    private static void match(Symbol expected) {
        Symbol current = nextSymbol();
        if (current.equals(expected)) {
            index++;
        } else {
            throw new IllegalArgumentException("Unexpected token received. Expected token: " + expected + ", received token: " + line[index] + " (" + current + ").");
        }
    }
}
