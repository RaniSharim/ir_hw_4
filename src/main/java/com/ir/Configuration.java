package main.java.com.ir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Configuration {
    String trainFile;
    String testFile;
    String outputFile;
    int k;

    static Configuration readConfiguration(String filename) throws IOException {
        List<String> configurationLines = Files.readAllLines(Paths.get(filename));

        Configuration config = new Configuration();
        config.trainFile = configurationLines.get(0).split("=")[1].trim();
        config.testFile = configurationLines.get(1).split("=")[1].trim();
        config.outputFile = configurationLines.get(2).split("=")[1].trim();
        config.k = Integer.parseInt(configurationLines.get(3).split("=")[1].trim());

        return config;
    }
}
