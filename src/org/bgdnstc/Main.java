package org.bgdnstc;

import org.objectweb.asm.Type;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
        Parser.parse("src/source.bynt");
        Parser.parse("src/UDPServer.bynt");
        Parser.parse("src/UDPClient.bynt");

//        TODO:
//        IMPORTANT: fix frame size and test
//        (Frame stack might be solved) test frame creation + objects order in locals stack
//        ordered hashmap has been added (needs testing)
//        test code
//        test file path format (/ or \\) and its destination
//        while loops
//        (maybe fixed ?) fix case in which integer variables are saved as objects

//        Parser.parse(args[0]);
//        for (String path : args) {
//            Parser.parse(path);
//        }
    }
}
