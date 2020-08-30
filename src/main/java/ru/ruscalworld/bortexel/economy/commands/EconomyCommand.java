package ru.ruscalworld.bortexel.economy.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.bortexel.economy.BortexelEconomy;
import ru.ruscalworld.bortexel.economy.ItemCacher;
import ru.ruscalworld.bortexel.economy.ReportPusher;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.economy.Report;

public class EconomyCommand implements CommandExecutor {

    private final BortexelEconomy plugin;

    public EconomyCommand(BortexelEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {

        if (!command.getName().equals("economy")) return false;
        if (args.length == 0) {
            commandSender.sendMessage("§8§l=====================\n" +
                    " §9BortexelEconomy §fis a plugin that adds ability to work with Economy API in game.\n" +
                    " §fWritten by §9RuscalWorld §ffor §6Bortexel §fMinecraft server.\n" +
                    " §fIP: §9vanilla.bortexel.ru\n" +
                    " §fPlugin version: §9" + plugin.getDescription().getVersion() + "\n" +
                    " §f© RuscalWorld, 2020\n" +
                    "§8§l=====================");
            return true;
        }

        switch (args[0]) {
            case "report":
                if (args.length < 3) {
                    commandSender.sendMessage("§fИспользование: §9/eco report <предмет> <стоимость>");
                    return true;
                }

                if (!commandSender.hasPermission("bortexel.eco.report")) return false;

                if (!(commandSender instanceof Player)) return false;
                Player player = (Player) commandSender;

                String item = args[1];
                float price = Float.parseFloat(args[2]);
                Location location = player.getLocation();
                if (location.getWorld() == null) return false;

                Bortexel4J client = plugin.getClient();
                Report report = Report.create();

                report.setPrice(price);
                report.setItem(item);
                report.setAuthor(commandSender.getName());
                report.setX((int) location.getX());
                report.setY((int) location.getY());
                report.setZ((int) location.getZ());
                report.setWorld(location.getWorld().getName());

                new ReportPusher(client, report).start();
                player.sendMessage("Данные отправлены.");
                break;
            case "reload":
                if (commandSender.hasPermission("bortexel.eco.reload")) {
                    switch (args[1]) {
                        case "config":
                            commandSender.sendMessage("§aConfiguration reloaded");
                            plugin.reloadConfig();
                            break;
                        case "cache":
                            commandSender.sendMessage("Reloading cache...");
                            new ItemCacher(plugin).start();
                            break;
                        default:
                            commandSender.sendMessage("§c§l[!] §fUnknown action: §7" + args[1]);
                    }
                }
                break;
        }

        return true;
    }
}
