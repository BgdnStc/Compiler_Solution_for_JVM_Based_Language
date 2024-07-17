package org.bgdnstc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class SourceReader {
    private static final Pattern byteNetFile = Pattern.compile(".*\\.bynt$");

    private SourceReader() {
    }

    protected static String readSource(Path path) {
        try {
            if (byteNetFile.matcher(path.toString()).matches()) {
                return Files.readString(path);
            } else {
                throw new InvalidPathException(path.toString().substring(path.toString().lastIndexOf("\\")), "Invalid file format provided. ByteNet code is stored exclusively in \".bynt\" files. Provided file");
            }
        } catch (IOException e) {
            throw new InvalidPathException(path.toString(), "Invalid path provided. The path should have the root the current content directory or to be an absolute path. Provided path");
        }
    }
}
