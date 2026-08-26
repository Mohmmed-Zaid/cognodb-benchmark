package com.cognodb;

import com.cognodb.loader.*;
import com.cognodb.util.Config;

/**
 * Quick test to verify database connections work
 * Tests only the 2 databases we're using
 */
public class QuickConnectionTest {
    public static void main(String[] args) {
        System.out.println("\n🧪 Testing database connections...\n");

        // Debug: Show loaded config
        System.out.println("📍 Checking configuration...");
        try {
            String cognodbUri = Config.getCognoDBUri();
            System.out.println("✅ COGNODB_URI found: " + cognodbUri.substring(0, Math.min(30, cognodbUri.length())) + "...");
        } catch (Exception e) {
            System.out.println("❌ COGNODB_URI not found");
        }

        try {
            String neo4jUri = Config.getNeo4jAuraUri();
            System.out.println("✅ NEO4J_AURA_URI found: " + neo4jUri.substring(0, Math.min(30, neo4jUri.length())) + "...");
        } catch (Exception e) {
            System.out.println("❌ NEO4J_AURA_URI not found");
        }

        System.out.println("\n🔗 Testing connections...\n");

        testDatabase(new CognoDBLoader());
        testDatabase(new Neo4jAuraLoader());

        System.out.println("\n✅ Test complete!");
    }

    private static void testDatabase(DatabaseConnection db) {
        try {
            System.out.print("Testing " + db.getName() + "... ");
            db.connect();
            System.out.println("✅ OK");
            db.close();
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
}