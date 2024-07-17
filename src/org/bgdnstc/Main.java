package org.bgdnstc;

public class Main {
    public static void main(String[] args) {
        // JDK 21
        // asm-9.6

        for (String path : args) {
            System.out.println(path);
            Parser.parse(path);
        }
    }
}
