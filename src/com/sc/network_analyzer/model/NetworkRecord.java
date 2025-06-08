package com.sc.network_analyzer.model;

import java.util.Objects;

public class NetworkRecord {
    private long timestamp;
    private String srcIp;
    private int srcPort;
    private String dstIp;
    private int dstPort;
    private String domain;

    public NetworkRecord(long timestamp, String srcIp, int srcPort, String dstIp, int dstPort, String domain) {
        this.timestamp = timestamp;
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkRecord)) return false;
        NetworkRecord that = (NetworkRecord) o;
        return timestamp == that.timestamp &&
                srcPort == that.srcPort &&
                dstPort == that.dstPort &&
                Objects.equals(srcIp, that.srcIp) &&
                Objects.equals(dstIp, that.dstIp) &&
                Objects.equals(domain, that.domain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, srcIp, srcPort, dstIp, dstPort, domain);
    }
}
