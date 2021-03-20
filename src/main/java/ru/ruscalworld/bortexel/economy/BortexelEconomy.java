package ru.ruscalworld.bortexel.economy;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ruscalworld.bortexel.economy.commands.EconomyCommand;
import ru.ruscalworld.bortexel.economy.commands.EconomyTabCompleter;
import ru.ruscalworld.bortexel.economy.commands.PriceCommand;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.core.Callback;
import ru.ruscalworld.bortexel4j.models.economy.Item;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.io.IOException;
import java.util.*;

public class BortexelEconomy extends JavaPlugin {
    private Bortexel4J client;
    private final List<Item> itemCache = new ArrayList<>();
    private final List<String> itemSuggestions = new ArrayList<>();
    public HashMap<String, Integer> shops = new HashMap<>();
    private final HashMap<String, Integer> playerCache = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginCommand economy = Objects.requireNonNull(getCommand("ecoreport"));
        economy.setExecutor(new EconomyCommand(this));
        economy.setTabCompleter(new EconomyTabCompleter(this));

        PluginCommand price = Objects.requireNonNull(getCommand("price"));
        price.setExecutor(new PriceCommand());
        price.setTabCompleter((commandSender, command, s, args) -> {
            if (args.length == 1) return this.getItemSuggestions();
            return new ArrayList<>();
        });

        if (CommodoreProvider.isSupported()) {
            Commodore commodore = CommodoreProvider.getCommodore(this);

            try {
                // Get command nodes from files
                LiteralCommandNode<Object> economyCommandNode = CommodoreFileFormat.parse(
                        Objects.requireNonNull(getResource("commands/economy.commodore")));
                LiteralCommandNode<Object> priceCommandNode = CommodoreFileFormat.parse(
                        Objects.requireNonNull(getResource("commands/price.commodore")));

                // Register command suggestions
                commodore.register(economy, economyCommandNode);
                commodore.register(price, priceCommandNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            client = Bortexel4J.login(getConfig().getString("apiToken"));
        } catch (Exception e) {
            e.printStackTrace();
            this.setEnabled(false);
        }

        this.updateItemCache();
    }

    public void updateItemCache() {
        Item.getAll().executeAsync(items -> {
            for (Item.Category category : items) this.itemCache.addAll(category.getItems());
            for (Item item : this.itemCache) itemSuggestions.add(item.getId());
        });
    }

    public void getPlayerID(Player player, Callback<Integer> callback) {
        if (playerCache.containsKey(player.getName())) callback.handle(playerCache.get(player.getName()));
        else User.getByUsername(player.getName(), this.getClient()).executeAsync(user -> {
            this.playerCache.put(player.getName(), user.getID());
            callback.handle(user.getID());
        });
    }

    public List<String> getItemSuggestions() {
        return itemSuggestions;
    }

    public Bortexel4J getClient() {
        return this.client;
    }
}
