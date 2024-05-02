package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
//        Parser.parse("src/org/bgdnstc/source.bynt");

        Parser.parse("src/org/bgdnstc/UDPServer.bynt");
        Parser.parse("src/org/bgdnstc/UDPClient.bynt");

//        TODO
//        fix case in which space in strings is not allowed
//        add more operations
//        handle float operations
//        allow resigning a variable


//        Parser.parse(args[0]);

//        for (String path : args) {
//            Parser.parse(path);
//        }

//        System.out.println("6.3".matches("^\\d*\\.\\d*$"));
    }
}
