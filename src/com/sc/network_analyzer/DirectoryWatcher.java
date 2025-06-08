package com.sc.network_analyzer;

import com.sc.network_analyzer.service.Aggregator;
import com.sc.network_analyzer.service.FileHandler;
import com.sc.network_analyzer.service.Parser;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher implements Runnable {

    private final Path watchDir;
    private final FileHandler fileHandler;
    private final Parser parser;
    private final Aggregator aggregator;

    public DirectoryWatcher(String directoryPath, FileHandler fileHandler, Parser parser, Aggregator aggregator) {
        this.watchDir = Paths.get(directoryPath);
        this.fileHandler = fileHandler;
        this.parser = parser;
        this.aggregator = aggregator;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchDir.register(watchService, ENTRY_CREATE);
            System.out.println("Monitoring directory: " + watchDir);

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    Path fullPath = watchDir.resolve(filename);

                    if (filename.toString().toLowerCase().endsWith(".csv")) {
                        System.out.println("Detected new file: " + fullPath);
                        fileHandler.handleNewFile(fullPath, parser, aggregator);
                    }
                }

                boolean valid = key.reset();
                if (!valid) break;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error occurred while watching file directory : " + e.getMessage());
        }
    }
}
