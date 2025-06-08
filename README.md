# network-analyzer
This Java application is designed to monitor a configurable directory for newly added CSV files, extract network connection data, and continuously analyze the most accessed domains in near real-time.

## Prerequisites
- Java 17 or later

## Features 
- Directory Monitoring: Continuously watches a specified directory for new files added by an external system or utility.
- File Stability Handling: Waits until each incoming file is fully copied before processing to avoid partial reads.
- CSV Parsing: Processes files containing structured records of network connections
- Avoid Deduplication
- Aggregation Logic: Every minute, aggregates all valid records and computes the top 10 most frequently reported domains based on connection count.
- Output Reporting: Writes the list of top domains along with their connection counts to a .txt file, updating it at regular 1-minute intervals.

## Sequence Diagram
```mermaid
sequenceDiagram
    participant Main
    participant ConfigLoader
    participant DirectoryWatcher
    participant FileHandler
    participant FileUtils
    participant Parser
    participant Aggregator
    participant FileSystem

    Main->>ConfigLoader: new ConfigLoader(configPath)
    ConfigLoader-->>Main: ConfigLoader instance

    Main->>ConfigLoader: getWatchDirectory()
    ConfigLoader-->>Main: watchDir

    Main->>ConfigLoader: getOutputDirectory()
    ConfigLoader-->>Main: outputDir

    Main->>Parser: new CSVParser()
    Parser-->>Main: Parser instance

    Main->>FileHandler: new CsvFileHandler()
    FileHandler-->>Main: FileHandler instance

    Main->>Aggregator: new AggregatorImpl(outputDir)
    Aggregator-->>Main: Aggregator instance

    Main->>DirectoryWatcher: new DirectoryWatcher(watchDir, fileHandler, parser, aggregator)
    DirectoryWatcher-->>Main: DirectoryWatcher instance

    Main->>DirectoryWatcher: start()

    DirectoryWatcher->>FileSystem: register(watchDir, ENTRY_CREATE)

    loop Monitor Directory
        FileSystem-->>DirectoryWatcher: fileCreated(filePath)
        DirectoryWatcher->>FileHandler: handleNewFile(filePath, parser, aggregator)
        FileHandler->>FileUtils: waitForFileToBeStable(filePath)
        FileUtils-->>FileHandler: true/false (isStable)

        alt File is stable
            FileHandler->>Parser: parseAndDeduplicate(filePath)
            Parser->>FileSystem: read(filePath)
            FileSystem-->>Parser: fileContent
            Parser-->>FileHandler: List<NetworkRecord>
            FileHandler->>Aggregator: addBatch(records)
        end
    end

    loop Every minute
        Aggregator->>Aggregator: flushTopDomains()
        Aggregator->>FileSystem: write(topDomains.txt)
    end

```
