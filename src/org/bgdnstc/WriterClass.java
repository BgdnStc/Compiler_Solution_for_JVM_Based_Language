package org.bgdnstc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WriterClass {

    private WriterClass() {
    }

    protected static String pathToClassName(String path) {
        String[] pathArray = path.split("\\\\");
        return pathArray[pathArray.length - 1].split("\\.")[0];
    }

    protected static void writeClass(String className, byte[] bytecode) {
        try {
            Files.write(Path.of("C:\\Users\\Bogdan\\IdeaProjects\\Compiler_Solution_for_JVM_Based_Language\\CompilerOutput\\" + className + ".class"), bytecode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
