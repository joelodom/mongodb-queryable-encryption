package com.joelodom;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileExtractor {

    /**
     * Extracts a resource from the JAR to a specified destination path.
     *
     * @param resourcePath   the path to the resource inside the JAR (e.g., "/data.csv")
     * @param destinationStr the destination file path in the file system (e.g., "/tmp/data.csv")
     * @throws IOException if an I/O error occurs during extraction
     */
    public static void extractResource(String resourcePath, String destinationStr) throws IOException {
        // Get an input stream for the resource from the JAR.
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            // Define the destination path.
            Path destination = Paths.get(destinationStr);

            // Copy the resource's contents to the destination file.
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
