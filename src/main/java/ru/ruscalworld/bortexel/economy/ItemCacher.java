package ru.ruscalworld.bortexel.economy;

import ru.ruscalworld.bortexel4j.economy.Category;
import ru.ruscalworld.bortexel4j.economy.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemCacher extends Thread {

    private final BortexelEconomy plugin;

    public ItemCacher(BortexelEconomy plugin) {
        this.plugin = plugin;
        this.setName("Item Cacher Thread");
    }

    @Override
    public void run() {
        List<Category> categories = Category.getAll();
        List<Item> result = new ArrayList<>();
        categories.forEach(category -> result.addAll(category.getItems()));
        plugin.items = result;
        plugin.items.forEach(item -> plugin.itemSuggestions.add(item.getId()));
    }
}
