package com.sc.network_analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    /**
     * Waits until the specified file has a stable size (i.e., no changes for a few consecutive checks),
     * indicating that it is fully copied or written.
     *
     * @param filePath        the path to the file to monitor
     * @param timeoutSeconds  the maximum number of seconds to wait for the file to stabilize
     * @return true if the file is stable within the timeout, false otherwise
     */
    public static boolean waitForFileToBeStable(Path filePath, int timeoutSeconds) {

        long previousSize = -1; // Tracks the file size from the last check
        int stableChecks = 0; // Counts how many times the size remained unchanged
        int maxChecks = timeoutSeconds * 2; // Total number of times we'll check (2 checks per second)
        int checkIntervalMillis = 500; // Wait time between checks (in milliseconds)

        try {
            for (int i = 0; i < maxChecks; i++) {
                // Get the current size of the file
                long currentSize = Files.size(filePath);

                if (currentSize == previousSize) {
                    stableChecks++;

                    // If the size hasn't changed for 3 checks, we consider it stable
                    if (stableChecks >= 3) {
                        return true;
                    }
                } else {
                    stableChecks = 0;
                }
                // Size changed — reset the counter
                previousSize = currentSize;
                // Wait before the next check
                Thread.sleep(checkIntervalMillis);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error while checking file stability: " + e.getMessage());
        }
        // File never stabilized within the timeout period
        return false;
    }

}
