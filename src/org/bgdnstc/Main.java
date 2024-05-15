package org.bgdnstc;

import org.objectweb.asm.Type;

public class Main {
    public static void main(String[] args) {
        Parser.parse("src/source.bynt");
        Parser.parse("src/UDPServer.bynt");
        Parser.parse("src/UDPClient.bynt");

//        System.out.println(Type.getInternalName(String.class));

//        TODO:
//        IMPORTANT: fix frame size and test
//        test frame creation + objects order in locals stack
//        test file path format (/ or \\) and its destination
//        while loops
//        (maybe fixed ?) fix case in which integer variables are saved as objects

//        Parser.parse(args[0]);
//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
