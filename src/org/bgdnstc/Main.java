package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        Parser.parse("source.bynt");
        Parser.parse("UDPServer.bynt");
        Parser.parse("UDPClient.bynt");

//        TODO:
//        while loops
//        fix case in which integer variables are saved as objects (maybe fixed ?)

//        Parser.parse(args[0]);
//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
