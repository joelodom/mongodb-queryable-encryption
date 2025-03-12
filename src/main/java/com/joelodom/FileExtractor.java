package com.joelodom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
        // System.out.println("*****");
        // System.out.println(System.getProperty("java.class.path"));
        // System.out.println("*****");

        // Get an input stream for the resource from the JAR.
        URL url = QEDemonstration.class.getResource(resourcePath);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }

        try (InputStream in = url.openStream()) {
            // Define the destination path.
            Path destination = Paths.get(destinationStr);

            // Copy the resource's contents to the destination file.
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
