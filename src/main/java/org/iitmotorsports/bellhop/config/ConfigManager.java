package org.iitmotorsports.bellhop.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH = Path.of("config.json");

    private static ConfigManager instance = null;

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }

        return instance;
    }

    public BellhopConfig load() throws IOException {
        return load(CONFIG_PATH);
    }

    public BellhopConfig load(Path path) throws IOException {
        if (Files.notExists(path)) {
            createDefault(path);
        }

        BellhopConfig config;

        try (var reader = Files.newBufferedReader(path)) {
            config = GSON.fromJson(reader, BellhopConfig.class);
        }

        return config;
    }

    private void createDefault(Path path) throws IOException {
        BellhopConfig defaultConfig = new BellhopConfig(
                25000,
                List.of(
                        new BellhopConfig.EndpointConfig(
                                "discord",
                                false,
                                Map.of("url", "")
                        ),
                        new BellhopConfig.EndpointConfig(
                                "slack",
                                false,
                                Map.of("url", "")
                        )
                )

        );

        Path parent = path.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, GSON.toJson(defaultConfig));
        log.info("No existing config file. Created default config at: " + path.toAbsolutePath());
    }
}
