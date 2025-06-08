package com.sc.network_analyzer;

import com.sc.network_analyzer.service.Aggregator;
import com.sc.network_analyzer.service.FileHandler;
import com.sc.network_analyzer.service.Parser;
import com.sc.network_analyzer.service.impl.AggregatorImpl;
import com.sc.network_analyzer.service.impl.CSVParser;
import com.sc.network_analyzer.service.impl.CsvFileHandler;

public class Main {
    public static void main(String[] args) {

        ConfigLoader configLoader = new ConfigLoader("config/config.properties");
        String watchDir = configLoader.getWatchDirectory();
        String outputDir = configLoader.getOutputDirectory();

        Parser parser = new CSVParser();
        FileHandler fileHandler = new CsvFileHandler();
        Aggregator aggregator = new AggregatorImpl(outputDir);

        DirectoryWatcher watcher = new DirectoryWatcher(watchDir, fileHandler, parser, aggregator);
        new Thread(watcher).start();
    }
}
