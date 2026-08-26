package com.cognodb.loader;

import com.cognodb.metrics.Metrics;
import com.cognodb.util.Config;
import org.neo4j.driver.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CognoDB benchmark loader and workload runner
 * Uses Neo4j driver (compatible with CognoDB)
 */
public class CognoDBLoader implements DatabaseConnection {
    private Driver driver;
    private Session session;
    private final String uri;
    private final String user;
    private final String password;
    private static final String DB_NAME = "CognoDB";

    public CognoDBLoader() {
        this.uri = Config.getCognoDBUri();
        this.user = Config.getCognoDBUser();
        this.password = Config.getCognoDBPassword();
    }

    @Override
    public void connect() {
        System.out.println("🔗 Connecting to CognoDB...");
        try {
            this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
            this.session = driver.session();

            // Test connection
            session.run("RETURN 1");
            System.out.println("✅ Connected to CognoDB");
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to CognoDB: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> loadPokec(String csvPath) {
        System.out.println("📥 Loading Pokec dataset into CognoDB...");
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // Clear existing data
            System.out.println("   Clearing existing data...");
            session.run("MATCH (n) DETACH DELETE n");

            // Load CSV in batches
            List<String[]> batch = new ArrayList<>();
            int nodeCount = 0;
            int relCount = 0;
            int batchSize = 1000;

            System.out.println("   Reading CSV and loading in batches...");
            String actualPath = com.cognodb.util.FilePath.findFile(csvPath);
            try (BufferedReader br = new BufferedReader(new FileReader(actualPath))) {
                String line;
                br.readLine();  // Skip header

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        batch.add(parts);
                    }

                    if (batch.size() >= batchSize) {
                        relCount += insertBatch(batch);
                        batch.clear();
                    }
                }

                // Final batch
                if (!batch.isEmpty()) {
                    relCount += insertBatch(batch);
                }
            }

            // Get node count
            nodeCount = getNodeCount();

            long elapsed = System.currentTimeMillis() - startTime;

            results.put("load_time_ms", elapsed);
            results.put("total_nodes", nodeCount);
            results.put("total_rels", relCount);
            results.put("nodes_per_sec", nodeCount * 1000.0 / elapsed);
            results.put("rels_per_sec", relCount * 1000.0 / elapsed);

            System.out.printf("✅ Loaded: %d nodes, %d edges in %.2f seconds%n",
                    nodeCount, relCount, elapsed / 1000.0);

        } catch (IOException e) {
            System.err.println("❌ Load error: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Insert batch of edges
     */
    private int insertBatch(List<String[]> batch) {
        List<Map<String, Object>> edges = batch.stream()
                .map(parts -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("src", Integer.parseInt(parts[0]));
                    m.put("tgt", Integer.parseInt(parts[1]));
                    return m;
                })
                .collect(Collectors.toList());

        String query = "UNWIND $edges AS edge " +
                "MERGE (a:User {id: edge.src}) " +
                "MERGE (b:User {id: edge.tgt}) " +
                "MERGE (a)-[:FOLLOWS]->(b) " +
                "RETURN count(*)";

        Result result = session.run(query, Values.parameters("edges", edges));
        return result.single().get(0).asInt();
    }

    /**
     * Get total node count
     */
    private int getNodeCount() {
        Result result = session.run("MATCH (n:User) RETURN count(n) as count");
        return result.single().get("count").asInt();
    }

    @Override
    public Metrics traversal1Hop(int iterations) {
        return runBenchmark(
                "MATCH (u:User {id: $id})-[:FOLLOWS]->(f) RETURN count(f)",
                "1-hop traversal",
                iterations
        );
    }

    @Override
    public Metrics traversal2Hop(int iterations) {
        return runBenchmark(
                "MATCH (u:User {id: $id})-[:FOLLOWS]->()-[:FOLLOWS]->(fof) " +
                        "RETURN count(DISTINCT fof)",
                "2-hop traversal",
                iterations
        );
    }

    @Override
    public Metrics traversal3Hop(int iterations) {
        return runBenchmark(
                "MATCH (u:User {id: $id})-[:FOLLOWS]->()-[:FOLLOWS]->()-[:FOLLOWS]->(f3) " +
                        "RETURN count(DISTINCT f3)",
                "3-hop traversal",
                iterations
        );
    }

    @Override
    public Metrics pointLookup(int iterations) {
        return runBenchmark(
                "MATCH (u:User {id: $id}) RETURN u",
                "Point lookup",
                iterations
        );
    }

    @Override
    public Metrics aggregation(int iterations) {
        return runBenchmark(
                "MATCH (u:User)-[:FOLLOWS]->(f) " +
                        "RETURN u.id, count(f) as followers " +
                        "ORDER BY followers DESC LIMIT 100",
                "Aggregation",
                iterations
        );
    }

    /**
     * Run a benchmark query N times and collect latencies
     */
    private Metrics runBenchmark(String query, String name, int iterations) {
        System.out.printf("  Running %s (%d iterations)...%n", name, iterations);
        List<Long> latencies = new ArrayList<>();

        // Warm-up (10 iterations, not counted)
        for (int i = 0; i < 10; i++) {
            session.run(query, Values.parameters("id", 12345));
        }

        // Measured iterations
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            session.run(query, Values.parameters("id", 12345));
            long elapsed = System.nanoTime() - start;
            latencies.add(elapsed / 1_000_000);  // Convert nanoseconds to milliseconds
        }

        return Metrics.from(latencies);
    }

    @Override
    public void close() {
        if (session != null) {
            session.close();
        }
        if (driver != null) {
            driver.close();
        }
        System.out.println("🔌 Disconnected from CognoDB");
    }

    @Override
    public String getName() {
        return DB_NAME;
    }
}