package com.cognodb.util;

import java.io.File;

/**
 * Smart file path finder - handles relative paths from any working directory
 */
public class FilePath {

    /**
     * Find file in project, checking multiple possible locations
     */
    public static String findFile(String relativePath) {
        String[] possiblePaths = {
                relativePath,                                    // Current directory
                "./" + relativePath,                            // ./data/...
                "../" + relativePath,                           // ../data/...
                "../../" + relativePath,                        // ../../data/...
        };

        String userDir = System.getProperty("user.dir");
        System.out.println("📍 Looking for: " + relativePath);
        System.out.println("   From: " + userDir);

        for (String path : possiblePaths) {
            File file = new File(userDir, path);
            System.out.println("   Trying: " + file.getAbsolutePath() + " ... " +
                    (file.exists() ? "✅" : "❌"));

            if (file.exists()) {
                System.out.println("   ✅ Found at: " + file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        }

        throw new RuntimeException("❌ File not found: " + relativePath +
                "\n   Searched in: " + userDir);
    }
}