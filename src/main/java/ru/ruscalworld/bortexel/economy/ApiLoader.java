package ru.ruscalworld.bortexel.economy;

import org.bukkit.Bukkit;
import ru.ruscalworld.bortexel4j.Bortexel4J;

import java.util.logging.Level;

public class ApiLoader extends Thread {

    private final BortexelEconomy plugin;

    public ApiLoader(BortexelEconomy plugin) {
        this.plugin = plugin;
        this.setName("Bortexel4J loader thread");
    }

    @Override
    public void run() {
        String token = plugin.getConfig().getString("apiToken");

        if (token == null) {
            Bukkit.getLogger().log(Level.WARNING, "Token is not specified or is not correct");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        Bortexel4J bortexel4j = Bortexel4J.login(token);

        if (bortexel4j.getLevel() < 10) {
            Bukkit.getLogger().log(Level.WARNING, "Given token has not enough permissions");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        plugin.client = bortexel4j;
    }
}
