package ru.bortexel.economy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.bortexel.economy.commands.PriceCommand;
import ru.bortexel.economy.commands.ReportCommand;
import ru.bortexel.economy.commands.RuslanCommand;
import ru.bortexel.economy.config.EconomyConfig;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.core.Callback;
import ru.ruscalworld.bortexel4j.models.economy.Item;
import ru.ruscalworld.bortexel4j.models.user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Economy implements ModInitializer {
    private Bortexel4J client;
    private EconomyConfig config;

    private final HashMap<UUID, Integer> shopSelectionMap = new HashMap<>();
    private final HashMap<UUID, Integer> playerCache = new HashMap<>();
    private final HashMap<String, Item> itemCache = new HashMap<>();

    @Override
    public void onInitialize() {
        this.loadConfig();
        this.setClient(Bortexel4J.login(this.getConfig().getApiToken(), this.getConfig().getApiUrl()));
        this.updateItemCache();

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            if (!dedicated) return;
            PriceCommand.register(dispatcher, this);
            ReportCommand.register(dispatcher, this);
            RuslanCommand.register(dispatcher, this);
        }));
    }

    protected void loadConfig() {
        try {
            this.config = new EconomyConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateItemCache() {
        this.getItemCache().clear();
        Item.getAll(this.getClient()).executeAsync(categories -> {
            for (Item.Category category : categories) for (Item item : category.getItems()) {
                this.getItemCache().put(item.getId(), item);
            }
        });
    }

    public void getPlayerID(ServerPlayerEntity player, Callback<Integer> callback) {
        if (playerCache.containsKey(player.getUuid())) callback.handle(playerCache.get(player.getUuid()));
        else User.getByUsername(player.getName().asString(), this.getClient()).executeAsync(user -> {
            this.playerCache.put(player.getUuid(), user.getID());
            callback.handle(user.getID());
        });
    }

    public void setClient(Bortexel4J client) {
        this.client = client;
    }

    public Bortexel4J getClient() {
        return client;
    }

    public EconomyConfig getConfig() {
        return config;
    }

    public HashMap<UUID, Integer> getShopSelectionMap() {
        return shopSelectionMap;
    }

    public HashMap<String, Item> getItemCache() {
        return itemCache;
    }
}
