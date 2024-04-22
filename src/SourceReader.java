import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class SourceReader {

    private SourceReader() {
    }

    protected static String readSource(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new InvalidPathException("Invalid provided path:" + path.toString() + ". ", "Path should be absolute path or from current content root.");
        }
    }
}
