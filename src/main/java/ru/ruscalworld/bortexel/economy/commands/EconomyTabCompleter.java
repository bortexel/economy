package ru.ruscalworld.bortexel.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ruscalworld.bortexel.economy.BortexelEconomy;

import java.util.ArrayList;
import java.util.List;

public class EconomyTabCompleter implements TabCompleter {

    private final BortexelEconomy plugin;

    public EconomyTabCompleter(BortexelEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equals("economy")) return null;
        List<String> suggestions = new ArrayList<>();

        switch (args.length) {
            case 0:
            case 1:
            case 3:
                return suggestions;
            case 2:
                return plugin.getItemSuggestions();
        }

        return suggestions;
    }
}
