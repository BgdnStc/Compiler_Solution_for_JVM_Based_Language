package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        // Java version: JDK 21
        // Object ASM library version: asm-9.6

        for (String path : args) {
            System.out.println(path);
            Parser.parse(path);
        }
    }
}
