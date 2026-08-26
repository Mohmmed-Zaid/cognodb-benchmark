package com.cognodb.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * Holds benchmark result percentiles for a single metric
 * Automatically calculates p50, p95, p99 from latency list
 */
public class Metrics {
    @JsonProperty("p50_ms")
    public long p50;  // 50th percentile in milliseconds

    @JsonProperty("p95_ms")
    public long p95;  // 95th percentile

    @JsonProperty("p99_ms")
    public long p99;  // 99th percentile

    @JsonProperty("mean_ms")
    public double mean;

    @JsonProperty("min_ms")
    public long min;

    @JsonProperty("max_ms")
    public long max;

    @JsonProperty("iterations")
    public int iterations;

    /**
     * Create Metrics from list of latencies (in milliseconds)
     */
    public static Metrics from(List<Long> latencies) {
        if (latencies.isEmpty()) {
            throw new IllegalArgumentException("No latencies to process");
        }

        Metrics m = new Metrics();
        Collections.sort(latencies);

        m.iterations = latencies.size();
        m.min = latencies.get(0);
        m.max = latencies.get(latencies.size() - 1);
        m.mean = latencies.stream().mapToLong(Long::longValue).average().orElse(0);

        m.p50 = percentile(latencies, 50);
        m.p95 = percentile(latencies, 95);
        m.p99 = percentile(latencies, 99);

        return m;
    }

    /**
     * Calculate percentile from sorted list
     */
    private static long percentile(List<Long> sorted, int p) {
        int index = (int) Math.ceil((p / 100.0) * sorted.size()) - 1;
        int safeIndex = Math.max(0, Math.min(index, sorted.size() - 1));
        return sorted.get(safeIndex);
    }

    @Override
    public String toString() {
        return String.format("p50=%dms, p95=%dms, p99=%dms, mean=%.2fms",
                p50, p95, p99, mean);
    }
}