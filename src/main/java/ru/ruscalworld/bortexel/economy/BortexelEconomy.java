package ru.ruscalworld.bortexel.economy;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.bortexel.hooks.Bortexel;
import ru.ruscalworld.bortexel.economy.commands.EconomyCommand;
import ru.ruscalworld.bortexel.economy.commands.EconomyTabCompleter;
import ru.ruscalworld.bortexel.economy.commands.PriceCommand;
import ru.ruscalworld.bortexel.economy.commands.RuslanCommand;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.economy.Item;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class BortexelEconomy extends JavaPlugin {
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

        PluginCommand ruslan = Objects.requireNonNull(getCommand("ruslan"));
        ruslan.setExecutor(new RuslanCommand());
        ruslan.setTabCompleter((sender, command, alias, args) -> new ArrayList<>());

        if (CommodoreProvider.isSupported()) {
            Commodore commodore = CommodoreProvider.getCommodore(this);

            try {
                // Get command nodes from files
                LiteralCommandNode<Object> economyCommandNode = CommodoreFileReader.INSTANCE.parse(
                        Objects.requireNonNull(getResource("commands/economy.commodore")));
                LiteralCommandNode<Object> priceCommandNode = CommodoreFileReader.INSTANCE.parse(
                        Objects.requireNonNull(getResource("commands/price.commodore")));

                // Register command suggestions
                commodore.register(economy, economyCommandNode);
                commodore.register(price, priceCommandNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.updateItemCache();
    }

    public void updateItemCache() {
        Item.getAll().executeAsync(items -> {
            for (Item.Category category : items) this.itemCache.addAll(category.getItems());
            for (Item item : this.itemCache) itemSuggestions.add(item.getId());
        });
    }

    public void getPlayerID(Player player, Consumer<Integer> callback) {
        if (playerCache.containsKey(player.getName())) callback.accept(playerCache.get(player.getName()));
        else User.getByUsername(player.getName(), this.getClient()).executeAsync(user -> {
            int id = user.getAccountID();
            this.playerCache.put(player.getName(), id);
            callback.accept(id);
        });
    }

    public List<String> getItemSuggestions() {
        return itemSuggestions;
    }

    public Bortexel4J getClient() {
        return Bortexel.getApiClient();
    }
}
