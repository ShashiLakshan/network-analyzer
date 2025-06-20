package com.sc.network_analyzer.service.impl;

import com.sc.network_analyzer.model.NetworkRecord;
import com.sc.network_analyzer.service.Aggregator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AggregatorImpl collects incoming network records and periodically (every minute)
 * aggregates the top 10 most frequent domains, writing them to a timestamped text file.
 */
public class AggregatorImpl implements Aggregator {

    // Thread-safe buffer to hold records until aggregation
    private final List<NetworkRecord> buffer = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final String outputDir;

    public AggregatorImpl(String outputDir) {
        this.outputDir = outputDir;
        // Schedule flushTopDomains() to run every 1 minute
        scheduler.scheduleAtFixedRate(this::flushTopDomains, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Adds a new batch of network records to the buffer.
     * Called each time a file is parsed successfully.
     *
     * @param records deduplicated records from the parser
     */
    @Override
    public void addBatch(Set<NetworkRecord> records) {
        buffer.addAll(records);
    }

    private void flushTopDomains() {
        Map<String, Long> domainCounts;

        // Synchronize access to the buffer to avoid race conditions
        synchronized (buffer) {
            domainCounts = buffer.stream()
                    .collect(Collectors.groupingBy(NetworkRecord::getDomain, Collectors.counting()));
            // Clear buffer after aggregation
            buffer.clear();
        }

        List<Map.Entry<String, Long>> top10 = domainCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .toList();

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String filename = outputDir + "/top_domains_" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("# Top 10 Domains - " + timestamp + "\n");
            int rank = 1;
            for (Map.Entry<String, Long> entry : top10) {
                writer.write(rank++ + ". " + entry.getKey() + " - " + entry.getValue() + " connections\n");
            }
            System.out.println("Written top domains to " + filename);
        } catch (IOException e) {
            System.err.println("Failed to write output: " + e.getMessage());
        }
    }
}
