package org.bgdnstc;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
//        Parser.parse("src\\source.bynt");
//        Parser.parse("src\\source2.bynt");

//        Parser.parse("src\\test.bynt");

//        Parser.parse("src/UDPServer.bynt");
//        Parser.parse("src/UDPClient.bynt");

//        Parser.parse("C:\\Users\\Bogdan\\IdeaProjects\\Compiler_Solution_for_JVM_Based_Language\\src\\test.bynt");

//TODO:
//        code clean up
//        expression path 11 (done, needs testing - statements after the loop)
//        integer incrementation operation (done, needs testing)
//        if structure inside loops
//        sequential compiling

//TODO
//        writer class destination directory

//        IMPORTANT: fix frame size and test
//        (Frame stack might be solved) test frame creation + objects order in locals stack
//        ordered hashmap has been added (needs testing)
//        test file path format (/ or \\) and its destination
//        while loops
//        (maybe fixed ?) fix case in which integer variables are saved as objects

        System.out.println(args[0]);
        Parser.parse(args[0]);

//        for (String path : args) {
//            System.out.println(path);
//            Parser.parse(path);
//        }
    }
}
