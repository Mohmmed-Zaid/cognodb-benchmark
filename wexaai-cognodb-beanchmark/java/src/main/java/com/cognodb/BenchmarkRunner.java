package com.cognodb;

import com.cognodb.loader.*;
import com.cognodb.metrics.ResultsExporter;
import com.cognodb.util.Config;
import java.io.IOException;
import java.util.*;

/**
 * Main entry point for the benchmark suite
 * Orchestrates benchmarking across 2 databases:
 * 1. CognoDB
 * 2. Neo4j Aura
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        try {
            System.out.println("\n" + "█".repeat(70));
            System.out.println("█" + " ".repeat(68) + "█");
            System.out.println("█  🚀 CognoDB Cloud Benchmark Suite" + " ".repeat(33) + "█");
            System.out.println("█  Benchmarking CognoDB vs Neo4j Aura" + " ".repeat(31) + "█");
            System.out.println("█" + " ".repeat(68) + "█");
            System.out.println("█".repeat(70));

            String csvPath = "data/pokec_subset.csv";

            // Only 2 databases
            List<DatabaseConnection> databases = Arrays.asList(
                    new CognoDBLoader(),
                    new Neo4jAuraLoader()
            );

            // Results storage
            Map<String, Object> allResults = new LinkedHashMap<>();
            allResults.put("timestamp", System.currentTimeMillis());
            allResults.put("dataset", "SNAP Pokec (200k edges, 45k nodes)");
            allResults.put("iterations_per_query", 100);
            allResults.put("databases_tested", 2);
            Map<String, Object> databaseResults = new LinkedHashMap<>();

            // Run benchmarks on each database
            int completed = 0;
            int failed = 0;

            for (DatabaseConnection db : databases) {
                try {
                    System.out.println("\n✅ Attempting to benchmark: " + db.getName());
                    BenchmarkSuite suite = new BenchmarkSuite(db, csvPath);
                    Map<String, Object> results = suite.run();
                    databaseResults.put(db.getName(), results);
                    completed++;
                } catch (Exception e) {
                    System.err.println("\n❌ " + db.getName() + " failed: " + e.getMessage());
                    e.printStackTrace();
                    databaseResults.put(db.getName(), Map.of("error", e.getMessage()));
                    failed++;
                }
            }

            allResults.put("databases", databaseResults);

            // Summary
            System.out.println("\n\n" + "=".repeat(70));
            System.out.println("📊 BENCHMARK SUMMARY");
            System.out.println("=".repeat(70));
            System.out.println("✅ Completed: " + completed + "/2");
            System.out.println("❌ Failed: " + failed + "/2");
            System.out.println("=".repeat(70));

            // Export results to JSON
            try {
                ResultsExporter.export("benchmark_results.json", allResults);
                System.out.println("\n✅ All done! Check results/benchmark_results.json for detailed results.");
            } catch (IOException e) {
                System.err.println("❌ Failed to export results: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}