package ru.bortexel.economy.config;

import net.fabricmc.loader.api.FabricLoader;
import ru.bortexel.economy.Economy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EconomyConfig {
    private final String apiToken;
    private final String apiUrl;

    public EconomyConfig() throws IOException {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("bortexel/api.properties");
        Properties properties = new Properties();
        properties.load(Files.newInputStream(path));

        this.apiToken = properties.getProperty("api-token", "");
        this.apiUrl = properties.getProperty("api-url", "https://api.bortexel.ru/v3");
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
