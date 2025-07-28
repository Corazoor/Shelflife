package de.superdupermarkt.shelflife.tests;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class Helper {
    public static Path createTempFileFromInputStream(String prefix, String suffix, InputStream inputStream) {
        Path tmpFile = null;
        try {
            tmpFile = Files.createTempFile(prefix, suffix);
            tmpFile.toFile().deleteOnExit();

            assertNotNull(inputStream, "Could not load test stream, got null");
            try {
                Files.write(tmpFile, inputStream.readAllBytes());
            } catch (IOException ex) {
                fail("Could not write test file: "+ex);
            }
        } catch (IOException ex) {
            fail("Could not create temporary file for test: "+ex);
        }

        return tmpFile;
    }
}
