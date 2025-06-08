package com.sc.network_analyzer.service.impl;

import com.sc.network_analyzer.model.NetworkRecord;
import com.sc.network_analyzer.service.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * CSVParser is responsible for parsing a CSV file containing network connection logs.
 * It reads the file, converts each valid line into a NetworkRecord,
 * and eliminates duplicate records using a HashSet.
 */
public class CSVParser implements Parser {
    @Override
    public Set<NetworkRecord> parseAndDeduplicate(Path filePath) {
        Set<NetworkRecord> records = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header
                }

                String[] parts = line.trim().split(",");
                if (parts.length != 6) {
                    System.err.println("Invalid CSV line: " + line);
                    continue;
                }

                try {
                    long timestamp = Long.parseLong(parts[0]);
                    String srcIp = parts[1];
                    int srcPort = Integer.parseInt(parts[2]);
                    String dstIp = parts[3];
                    int dstPort = Integer.parseInt(parts[4]);
                    String domain = parts[5];

                    records.add(new NetworkRecord(timestamp, srcIp, srcPort, dstIp, dstPort, domain));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number in CSV line: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to read file: " + filePath);
        }

        return records;
    }
}
