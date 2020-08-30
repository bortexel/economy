package ru.ruscalworld.bortexel.economy;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ruscalworld.bortexel.economy.commands.EconomyCommand;
import ru.ruscalworld.bortexel.economy.commands.EconomyTabCompleter;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.economy.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BortexelEconomy extends JavaPlugin {

    Bortexel4J client;
    List<Item> items;
    List<String> itemSuggestions = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginCommand economy = Objects.requireNonNull(getCommand("economy"));
        economy.setExecutor(new EconomyCommand(this));
        economy.setTabCompleter(new EconomyTabCompleter(this));

        if (CommodoreProvider.isSupported()) {
            Commodore commodore = CommodoreProvider.getCommodore(this);

            try {
                // Get command nodes from files
                LiteralCommandNode<Object> economyCommandNode = CommodoreFileFormat.parse(
                        Objects.requireNonNull(getResource("commands/economy.commodore")));

                // Register command suggestions
                commodore.register(economy, economyCommandNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new ApiLoader(this).start();
        new ItemCacher(this).start();
    }

    public Bortexel4J getClient() {
        return this.client;
    }

    public List<String> getItemSuggestions() {
        return this.itemSuggestions;
    }
}
