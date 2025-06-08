package com.sc.network_analyzer.service;

import java.nio.file.Path;

public interface FileHandler {

    void handleNewFile(Path filePath, Parser parser, Aggregator aggregator);

}
