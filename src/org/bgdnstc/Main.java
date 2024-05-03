package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        Parser.parse("src/org/bgdnstc/source.bynt");

        Parser.parse("src/org/bgdnstc/UDPServer.bynt");
        Parser.parse("src/org/bgdnstc/UDPClient.bynt");

//        TODO:
//        handle float operations
//        allow resigning a variable


//        Parser.parse(args[0]);

//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
