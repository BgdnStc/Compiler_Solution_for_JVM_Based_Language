package org.bgdnstc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WriterClass {

    private WriterClass() {
    }

    protected String pathToClassName(String path) {
        String[] pathArray = path.split("/");
        return pathArray[pathArray.length - 1].split("\\.")[0];
    }

    protected void writeClass(String className, byte[] bytecode) {
        try {
            Files.write(Path.of(className), bytecode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
