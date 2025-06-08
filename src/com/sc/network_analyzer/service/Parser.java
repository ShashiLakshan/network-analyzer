package com.sc.network_analyzer.service;

import com.sc.network_analyzer.model.NetworkRecord;

import java.nio.file.Path;
import java.util.Set;

public interface Parser {

    Set<NetworkRecord> parseAndDeduplicate(Path filePath);
}
