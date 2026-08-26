package com.cognodb.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Export benchmark results to JSON files
 */
public class ResultsExporter {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Export results map to JSON file
     */
    public static void export(String filename, Map<String, Object> results) throws IOException {
        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }

        File file = new File(resultsDir, filename);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, results);
        System.out.println("✅ Results exported to: " + file.getAbsolutePath());
    }

    /**
     * Export any object to JSON (generic method)
     */
    public static <T> void exportObject(String filename, T object) throws IOException {
        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }

        File file = new File(resultsDir, filename);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
        System.out.println("✅ Results exported to: " + file.getAbsolutePath());
    }
}