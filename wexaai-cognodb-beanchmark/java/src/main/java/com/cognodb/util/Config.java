package com.cognodb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration loader - finds .env file in project root
 * Smart path detection: looks in multiple locations
 */
public class Config {
    private static final Map<String, String> ENV = new HashMap<>();
    private static boolean envLoaded = false;

    static {
        loadConfig();
    }

    /**
     * Load configuration from .env file
     */
    private static void loadConfig() {
        if (envLoaded) return;

        // First, load from system environment
        ENV.putAll(System.getenv());
        System.out.println("✅ Loaded system environment variables");

        // Find and load from .env file
        File envFile = findEnvFile();
        if (envFile != null && envFile.exists()) {
            loadFromEnvFile(envFile);
            System.out.println("✅ Loaded credentials from: " + envFile.getAbsolutePath());
        } else {
            System.out.println("⚠️  config/.env not found, using system environment variables only");
            System.out.println("   💡 Tip: Create config/.env with your database credentials");
        }

        envLoaded = true;
    }

    /**
     * Smart search for .env file in project structure
     * Checks:
     * 1. ../config/.env (if running from java/ folder)
     * 2. ../../config/.env (if running from nested folder)
     * 3. config/.env (if running from project root)
     * 4. ./config/.env (current directory)
     */
    private static File findEnvFile() {
        String[] possiblePaths = {
                "config/.env",           // Project root
                "./config/.env",         // Current directory
                "../config/.env",        // Up one level
                "../../config/.env",     // Up two levels
                "../../../config/.env",  // Up three levels
        };

        // Also try from user.dir (working directory)
        String userDir = System.getProperty("user.dir");
        System.out.println("📍 Current working directory: " + userDir);

        for (String path : possiblePaths) {
            File file = new File(userDir, path);
            System.out.println("   Checking: " + file.getAbsolutePath() + " ... " + (file.exists() ? "✅" : "❌"));
            if (file.exists()) {
                return file;
            }
        }

        // Try absolute path
        File absolutePath = new File(userDir + "/config/.env");
        if (absolutePath.exists()) {
            return absolutePath;
        }

        return null;
    }

    /**
     * Load .env file and add to ENV map
     */
    private static void loadFromEnvFile(File envFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip comments and empty lines
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                // Parse KEY=VALUE
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();

                    // Remove quotes if present
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (value.startsWith("'") && value.endsWith("'")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    ENV.put(key, value);
                    lineCount++;

                    // Log (without passwords)
                    if (key.contains("PASSWORD")) {
                        System.out.println("   ✓ Loaded " + key + " = ***");
                    } else {
                        String displayValue = value.length() > 40 ?
                                value.substring(0, 40) + "..." : value;
                        System.out.println("   ✓ Loaded " + key + " = " + displayValue);
                    }
                }
            }

            System.out.println("   📊 Total credentials loaded: " + lineCount);

        } catch (IOException e) {
            System.err.println("❌ Error reading .env file: " + e.getMessage());
        }
    }

    /**
     * Get configuration value (required - throws if missing)
     */
    public static String get(String key) {
        String value = ENV.get(key);

        if (value == null || value.trim().isEmpty()) {
            System.err.println("\n❌ ERROR: Missing configuration variable: " + key);
            System.err.println("\n   Add this to config/.env:");
            System.err.println("   " + key + "=YOUR_VALUE_HERE");
            System.err.println("\n   Current .env path being searched:");

            String userDir = System.getProperty("user.dir");
            String[] paths = {
                    "config/.env",
                    userDir + "/config/.env",
                    userDir + "/../config/.env",
            };
            for (String p : paths) {
                System.err.println("   - " + p);
            }

            throw new RuntimeException("Missing configuration: " + key);
        }

        return value.trim();
    }

    /**
     * Get configuration value with default
     */
    public static String get(String key, String defaultValue) {
        String value = ENV.get(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }

    // ===== CognoDB =====
    public static String getCognoDBUri() {
        return get("COGNODB_URI");
    }

    public static String getCognoDBUser() {
        return get("COGNODB_USER", "cognodb");
    }

    public static String getCognoDBPassword() {
        return get("COGNODB_PASSWORD");
    }

    // ===== Neo4j Aura =====
    public static String getNeo4jAuraUri() {
        return get("NEO4J_AURA_URI");
    }

    public static String getNeo4jAuraUser() {
        return get("NEO4J_AURA_USER", "neo4j");
    }

    public static String getNeo4jAuraPassword() {
        return get("NEO4J_AURA_PASSWORD");
    }

    /**
     * Debug: Show all environment variables
     */
    public static void printDebug() {
        System.out.println("\n=== Configuration Debug ===");
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("\nLoaded Variables:");
        ENV.forEach((key, value) -> {
            if (key.contains("PASSWORD") || key.contains("PASSWORD")) {
                System.out.println("  " + key + " = ***");
            } else if (key.contains("URI")) {
                String display = value.length() > 50 ? value.substring(0, 50) + "..." : value;
                System.out.println("  " + key + " = " + display);
            }
        });
        System.out.println("============================\n");
    }
}