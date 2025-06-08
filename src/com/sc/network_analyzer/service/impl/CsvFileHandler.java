package com.sc.network_analyzer.service.impl;

import com.sc.network_analyzer.FileUtils;
import com.sc.network_analyzer.model.NetworkRecord;
import com.sc.network_analyzer.service.Aggregator;
import com.sc.network_analyzer.service.FileHandler;
import com.sc.network_analyzer.service.Parser;

import java.nio.file.Path;
import java.util.Set;

public class CsvFileHandler implements FileHandler {

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
