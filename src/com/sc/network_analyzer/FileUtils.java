package com.sc.network_analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static boolean waitForFileToBeStable(Path filePath, int timeoutSeconds) {

        long previousSize = -1;
        int stableChecks = 0;
        int maxChecks = timeoutSeconds * 2;
        int checkIntervalMillis = 500;

        try {
            for (int i = 0; i < maxChecks; i++) {
                long currentSize = Files.size(filePath);

                if (currentSize == previousSize) {
                    stableChecks++;
                    if (stableChecks >= 3) {
                        return true;
                    }
                } else {
                    stableChecks = 0;
                }

                previousSize = currentSize;
                Thread.sleep(checkIntervalMillis);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error while checking file stability: " + e.getMessage());
        }

        return false;

    }

}
