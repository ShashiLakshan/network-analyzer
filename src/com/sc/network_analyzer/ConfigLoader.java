package com.sc.network_analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    public static final String WATCH_DIRECTORY = "watch.directory";
    public static final String OUTPUT_DIRECTORY = "output.directory";

    private final Properties properties = new Properties();

    public ConfigLoader(String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + filePath, e);
        }
    }

    public String getWatchDirectory() {
        return properties.getProperty(WATCH_DIRECTORY);
    }

    public String getOutputDirectory() {
        return properties.getProperty(OUTPUT_DIRECTORY);
    }

}
