package com.sc.network_analyzer;

import com.sc.network_analyzer.service.Aggregator;
import com.sc.network_analyzer.service.FileHandler;
import com.sc.network_analyzer.service.Parser;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Monitors a specified directory for new CSV files.
 * When a new file is detected, it delegates the handling
 * to a provided FileHandler using the given Parser and Aggregator.
 */
public class DirectoryWatcher implements Runnable {

    private final Path watchDir;
    private final FileHandler fileHandler;
    private final Parser parser;
    private final Aggregator aggregator;

    /**
     * Constructs a DirectoryWatcher.
     *
     * @param directoryPath path to the directory to watch
     * @param fileHandler handler to process new files
     * @param parser parser to extract records from the file
     * @param aggregator aggregator to compute top domain stats
     */
    public DirectoryWatcher(String directoryPath, FileHandler fileHandler, Parser parser, Aggregator aggregator) {
        this.watchDir = Paths.get(directoryPath);
        this.fileHandler = fileHandler;
        this.parser = parser;
        this.aggregator = aggregator;
    }

    /**
     * Starts watching the directory for newly created CSV files.
     * Runs indefinitely on its own thread until the directory becomes invalid.
     */
    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            // Register the directory to watch for file creation events
            watchDir.register(watchService, ENTRY_CREATE);
            System.out.println("Monitoring directory: " + watchDir);

            while (true) {
                // blocks until a new file event occurs
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // Skip overflow events (system might have dropped some events)
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // Cast event and resolve full path of the created file
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    Path fullPath = watchDir.resolve(filename);

                    if (filename.toString().toLowerCase().endsWith(".csv")) {
                        System.out.println("Detected new file: " + fullPath);
                        fileHandler.handleNewFile(fullPath, parser, aggregator);
                    }
                }

                // Reset the key to continue watching, or break if invalid
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error occurred while watching file directory : " + e.getMessage());
        }
    }
}
