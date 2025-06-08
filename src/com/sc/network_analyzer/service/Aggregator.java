package com.sc.network_analyzer.service;

import com.sc.network_analyzer.model.NetworkRecord;

import java.util.Set;

public interface Aggregator {

    void addBatch(Set<NetworkRecord> records);
}
