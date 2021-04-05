package ru.ruscalworld.bortexel.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.bortexel.economy.Ruslan;

public class RuslanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equals("ruslan")) return false;
        if (args.length < 1) {
            sender.sendMessage("Использование: §9/ruslan <текст>");
            return true;
        }

        Ruslan.getAnswer(String.join(" ", args), response -> {
            if (response == null || response.getResponse().length() < 1) {
                sender.sendMessage("§c§l[!] §fНе удалось получить ответ.");
                return;
            }

            sender.sendMessage(response.getResponse());
        });

        return false;
    }
}
