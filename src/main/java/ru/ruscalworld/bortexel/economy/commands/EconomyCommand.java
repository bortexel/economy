package ru.ruscalworld.bortexel.economy.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.bortexel.economy.BortexelEconomy;
import ru.ruscalworld.bortexel4j.core.Action;
import ru.ruscalworld.bortexel4j.models.economy.Report;
import ru.ruscalworld.bortexel4j.models.shop.Shop;

public class EconomyCommand implements CommandExecutor {

    private final BortexelEconomy plugin;

    public EconomyCommand(BortexelEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {

        if (!command.getName().equals("ecoreport")) return false;
        if (args.length == 0) {
            commandSender.sendMessage("§8§l=====================\n" +
                    " §9BortexelEconomy §fis a plugin that adds ability to work with Economy API in game.\n" +
                    " §fWritten by §9RuscalWorld §ffor §6Bortexel §fMinecraft server.\n" +
                    " §fIP: §9vanilla.bortexel.ru\n" +
                    " §fPlugin version: §9" + plugin.getDescription().getVersion() + "\n" +
                    " §f© RuscalWorld, 2020-2021\n" +
                    "§8§l=====================");
            return true;
        }

        switch (args[0]) {
            case "create":
                if (args.length < 4) {
                    commandSender.sendMessage("§fИспользование: §9/ecoreport create <предмет> <в наличии> <стоимость> [количество]");
                    return true;
                }

                if (!commandSender.hasPermission("economy.report")) return false;

                if (!(commandSender instanceof Player)) return false;
                Player player = (Player) commandSender;

                if (!plugin.shops.containsKey(player.getName())) {
                    player.sendMessage("§c§l[!] §fУкажите название магазина: §9/ecoreport select <id>");
                    return false;
                }

                int amount = 1;
                if (args.length == 5) amount = Integer.parseInt(args[4]);

                String item = args[1].toLowerCase();
                int quantity = Integer.parseInt(args[2]);
                float price = Float.parseFloat(args[3]);
                price = Math.round(price / amount * 1000) / (float) 1000;
                Location location = player.getLocation();
                if (location.getWorld() == null) return false;

                Report.Builder builder = new Report.Builder();
                builder.setPrice(price);
                builder.setItemID(item);
                builder.setQuantity(quantity);
                builder.setLocation(new ru.ruscalworld.bortexel4j.util.Location(location.getBlockX(),
                        location.getBlockY(), location.getBlockZ(), location.getWorld().getName()));
                builder.setShopID(plugin.shops.get(player.getName()));

                plugin.getPlayerID(player, id -> {
                    Report report = builder.build();
                    Action<Report> action = report.create(plugin.getClient());
                    action.setExecutorID(id);
                    action.executeAsync(response -> {
                        player.sendMessage("§fПредмет: §7" + response.getItemID() + "§f, стоимость за 1 шт.: §9" + response.getPrice());
                    }, error -> player.sendMessage("§c§l[!] §fНе удалось записать стоимость: " + error.getMessage()));
                });
                break;
            case "select":
                if (commandSender.hasPermission("economy.select")) {
                    if (args.length < 2) {
                        commandSender.sendMessage("§fИспользование: §9/ecoreport select <id>");
                        return true;
                    }

                    if (!(commandSender instanceof Player)) return false;
                    player = (Player) commandSender;
                    try {
                        int id = Integer.parseInt(args[1]);
                        Shop.getByID(id).executeAsync(shop -> plugin.getPlayerID(player, playerID -> {
                            if (shop.getOwnerID() != playerID && !player.hasPermission("economy.inspector")) {
                                player.sendMessage("§c§l[!] §fЭтот магазин Вам не принадлежит.");
                                return;
                            }

                            if (!shop.isActive()) {
                                player.sendMessage("§c§l[!] §fЭтот магазин неактивен, записать цены в нём невозможно.");
                                return;
                            }

                            if (!shop.isVerified() && !player.hasPermission("economy.inspector")) {
                                player.sendMessage("§c§l[!] §fЭтот магазин не подтверждён, и записать цены в нём может только ревизор.");
                                return;
                            }

                            plugin.shops.put(player.getName(), id);
                            player.sendMessage("§fУспешно выбран магазин §7" + shop.getName());
                        }), error -> player.sendMessage("§c§l[!] §fМагазин не найден."));
                    } catch (Exception ignored) { }
                }
                break;
            case "reload":
                if (commandSender.hasPermission("economy.reload")) {
                    switch (args[1].toLowerCase()) {
                        case "config":
                            commandSender.sendMessage("§aConfiguration reloaded");
                            plugin.reloadConfig();
                            break;
                        case "cache":
                            commandSender.sendMessage("Reloading cache...");
                            plugin.updateItemCache();
                            break;
                        default:
                            commandSender.sendMessage("§c§l[!] §fUnknown action: §7" + args[1]);
                    }
                }
                break;
            default:
                commandSender.sendMessage("§c§l[!] §fНеизвестный аргумент.");
        }

        return true;
    }
}
