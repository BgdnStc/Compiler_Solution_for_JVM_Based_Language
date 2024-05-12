package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        Parser.parse("src/source.bynt");
        Parser.parse("src/UDPServer.bynt");
        Parser.parse("src/UDPClient.bynt");

//        TODO:
//        test file path format (/ or \\) and its destination
//        while loops
//        (maybe fixed ?) fix case in which integer variables are saved as objects

//        Parser.parse(args[0]);
//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
