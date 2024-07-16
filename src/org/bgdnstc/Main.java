package org.bgdnstc;

public class Main {
    public static void main(String[] args) {

//TODO:
//        code clean up
//        expression path 11 (done, needs testing - statements after the loop)
//        integer incrementation operation (done, needs testing)
//        when structure inside loops
//        sequential compiling
//        testing graphical interface communication

//TODO
//        writer class destination directory

//        IMPORTANT: fix frame size and test
//        (Frame stack might be solved) test frame creation + objects order in locals stack
//        ordered hashmap has been added (needs testing)
//        test file path format (/ or \\) and its destination
//        while loops
//        (maybe fixed ?) fix case in which integer variables are saved as objects

        for (String path : args) {
            System.out.println(path);
            Parser.parse(path);
        }
    }
}
