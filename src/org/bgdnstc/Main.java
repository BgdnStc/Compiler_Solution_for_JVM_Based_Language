package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        Parser.parse("src/org/bgdnstc/source.bynt");
        Parser.parse("src/org/bgdnstc/UDPServer.bynt");
        Parser.parse("src/org/bgdnstc/UDPClient.bynt");

//        TODO:
//        path 1, "index - 1" handle when there is a address after the port
//        while loops
//        fix case in which integer variables are saved as objects (maybe fixed ?)


//        Parser.parse(args[0]);
//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
