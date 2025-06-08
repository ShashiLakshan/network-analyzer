package com.sc.network_analyzer.service.impl;

import com.sc.network_analyzer.FileUtils;
import com.sc.network_analyzer.model.NetworkRecord;
import com.sc.network_analyzer.service.Aggregator;
import com.sc.network_analyzer.service.FileHandler;
import com.sc.network_analyzer.service.Parser;

import java.nio.file.Path;
import java.util.Set;

/**
 * CsvFileHandler is responsible for coordinating the full processing flow of a newly detected CSV file.
 * It waits until the file is fully copied (stable), then parses it and passes the records to the aggregator.
 */
public class CsvFileHandler implements FileHandler {

    /**
     * Handles a newly detected file by:
     * 1. Waiting for the file to become stable (i.e., fully copied),
     * 2. Parsing it into deduplicated NetworkRecord entries,
     * 3. Passing the parsed records to the aggregator.
     *
     * @param filePath   the path of the newly detected CSV file
     * @param parser     the parser to use for extracting NetworkRecords
     * @param aggregator the aggregator to store records for further analysis
     */
    @Override
    public void handleNewFile(Path filePath, Parser parser, Aggregator aggregator) {

        System.out.println("Detected file: " + filePath);
        boolean isStable = FileUtils.waitForFileToBeStable(filePath, 10);

        if (isStable) {
            System.out.println("File is stable. Parsing...");
            Set<NetworkRecord> records = parser.parseAndDeduplicate(filePath);
            aggregator.addBatch(records);
        } else {
            System.err.println("File not stable in time. Skipping: " + filePath);
        }
    }
}
