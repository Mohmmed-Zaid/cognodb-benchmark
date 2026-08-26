package com.cognodb;

import com.cognodb.loader.DatabaseConnection;
import com.cognodb.metrics.Metrics;
import java.util.*;

/**
 * Runs complete benchmark suite on a single database
 * - Data loading
 * - Warm-up phase
 * - All 5 workloads (traversals, lookups, aggregations)
 */
public class BenchmarkSuite {
    private final DatabaseConnection db;
    private final String csvPath;
    private static final int ITERATIONS = 100;  // Number of times to run each query

    public BenchmarkSuite(DatabaseConnection db, String csvPath) {
        this.db = db;
        this.csvPath = csvPath;
    }

    /**
     * Run complete benchmark suite on this database
     */
    public Map<String, Object> run() {
        Map<String, Object> results = new HashMap<>();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 Benchmarking: " + db.getName());
        System.out.println("=".repeat(60));

        try {
            // Phase 1: Connect
            System.out.println("\n📍 Phase 1: Connection");
            long connectStart = System.currentTimeMillis();
            db.connect();
            long connectTime = System.currentTimeMillis() - connectStart;
            results.put("connect_time_ms", connectTime);

            // Phase 2: Data Loading
            System.out.println("\n📍 Phase 2: Data Loading");
            Map<String, Object> loadMetrics = db.loadPokec(csvPath);
            results.put("loading", loadMetrics);

            // Phase 3: Workloads
            System.out.println("\n📍 Phase 3: Workload Benchmarks");
            System.out.println("   Running " + ITERATIONS + " iterations per workload...\n");

            Map<String, Object> workloads = new HashMap<>();

            // 1-hop traversal
            System.out.println("📊 1-Hop Traversal:");
            Metrics m1hop = db.traversal1Hop(ITERATIONS);
            if (m1hop != null) {
                workloads.put("traversal_1hop", metricsToMap(m1hop));
                System.out.println("   " + m1hop);
            }

            // 2-hop traversal
            System.out.println("📊 2-Hop Traversal:");
            Metrics m2hop = db.traversal2Hop(ITERATIONS);
            if (m2hop != null) {
                workloads.put("traversal_2hop", metricsToMap(m2hop));
                System.out.println("   " + m2hop);
            }

            // 3-hop traversal
            System.out.println("📊 3-Hop Traversal:");
            Metrics m3hop = db.traversal3Hop(ITERATIONS);
            if (m3hop != null) {
                workloads.put("traversal_3hop", metricsToMap(m3hop));
                System.out.println("   " + m3hop);
            }

            // Point lookup
            System.out.println("📊 Point Lookup:");
            Metrics pointLookup = db.pointLookup(ITERATIONS);
            if (pointLookup != null) {
                workloads.put("point_lookup", metricsToMap(pointLookup));
                System.out.println("   " + pointLookup);
            }

            // Aggregation
            System.out.println("📊 Aggregation:");
            Metrics agg = db.aggregation(ITERATIONS);
            if (agg != null) {
                workloads.put("aggregation", metricsToMap(agg));
                System.out.println("   " + agg);
            }

            results.put("workloads", workloads);

            // Phase 4: Cleanup
            System.out.println("\n📍 Phase 4: Cleanup");
            db.close();

            System.out.println("\n✅ " + db.getName() + " benchmark complete!");

        } catch (Exception e) {
            System.err.println("\n❌ Error during benchmark: " + e.getMessage());
            e.printStackTrace();
            results.put("error", e.getMessage());
        }

        return results;
    }

    /**
     * Convert Metrics object to Map for JSON serialization
     */
    private Map<String, Object> metricsToMap(Metrics m) {
        Map<String, Object> map = new HashMap<>();
        map.put("p50_ms", m.p50);
        map.put("p95_ms", m.p95);
        map.put("p99_ms", m.p99);
        map.put("mean_ms", m.mean);
        map.put("min_ms", m.min);
        map.put("max_ms", m.max);
        map.put("iterations", m.iterations);
        return map;
    }
}