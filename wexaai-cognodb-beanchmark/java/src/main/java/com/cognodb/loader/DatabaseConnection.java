package com.cognodb.loader;

import com.cognodb.metrics.Metrics;
import java.util.Map;

/**
 * Interface for all database loaders
 * Defines contract that each database must implement
 */
public interface DatabaseConnection {

    /**
     * Establish connection to database
     */
    void connect();

    /**
     * Load Pokec CSV dataset into database
     * @param csvPath path to pokec_subset.csv
     * @return map with load_time_ms, total_nodes, total_rels, nodes_per_sec, rels_per_sec
     */
    Map<String, Object> loadPokec(String csvPath);

    /**
     * 1-hop traversal: direct followers
     * MATCH (u:User {id: X})-[:FOLLOWS]->(f) RETURN COUNT(f)
     */
    Metrics traversal1Hop(int iterations);

    /**
     * 2-hop traversal: friend-of-friend
     * MATCH (u)-[:FOLLOWS]->()-[:FOLLOWS]->(fof) RETURN COUNT(DISTINCT fof)
     */
    Metrics traversal2Hop(int iterations);

    /**
     * 3-hop traversal
     */
    Metrics traversal3Hop(int iterations);

    /**
     * Point lookup: find user by ID
     * MATCH (u:User {id: X}) RETURN u
     */
    Metrics pointLookup(int iterations);

    /**
     * Aggregation: count followers per user
     * MATCH (u:User)-[:FOLLOWS]->(f) RETURN u.id, COUNT(f) as followers
     */
    Metrics aggregation(int iterations);

    /**
     * Get database name (for logging)
     */
    String getName();

    /**
     * Close connection
     */
    void close();
}