package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        Parser.parse("src/org/bgdnstc/source.bynt");
        Parser.parse("src/org/bgdnstc/UDPServer.bynt");
        Parser.parse("src/org/bgdnstc/UDPClient.bynt");

//        TODO:
//        while loops
//        allow resigning a variable
//        fix case in which integer variables are saved as objects (maybe fixed ?)


//        Parser.parse(args[0]);
//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
