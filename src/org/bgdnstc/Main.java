package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        Parser.parse("src/org/bgdnstc/source.bynt");
        Parser.parse("src/org/bgdnstc/UDPServer.bynt");
        Parser.parse("src/org/bgdnstc/UDPClient.bynt");

//        TODO:
//        handle storing float after integer division
//        allow resigning a variable
//        fix case in which integer variables are saved as objects


//        Parser.parse(args[0]);
//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
