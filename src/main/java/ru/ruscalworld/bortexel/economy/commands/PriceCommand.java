package ru.ruscalworld.bortexel.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.bortexel.economy.BortexelEconomy;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.economy.ItemPrices;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

public class PriceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equals("price")) return false;

        if (args.length == 0) {
            commandSender.sendMessage("§fИспользование: §9/price <предмет> [количество]");
            return true;
        }

        new Thread(() -> {
            int amount = 1;
            if (args.length == 2) amount = Integer.parseInt(args[1]);

            ItemPrices prices;

            try {
                prices = ItemPrices.getPrices(args[0]);
            } catch (Exception e) {
                commandSender.sendMessage("§c§l[!] §fПредмет не найден");
                return;
            }

            ItemPrices.Item item = prices.item;
            LinkedHashMap<String, Object> priceObj = prices.prices.get(prices.prices.size() - 1);
            float price = Float.parseFloat((String) priceObj.get("price"));

            String message = "§fАктуальная стоимость на §9" + item.name + "§f (§9" + item.id + "§f):\n" +
                    "§fЗа 1 ед.: §9" + formatPrice(price) + "§f";

            if (amount == 1) {
                message = message + "; за 32 ед.: §9" + (formatPrice(price * 32)) +
                        "§f; за 64 ед.: §9" + (formatPrice(price * 64));
            } else {
                message = message + "; за " + amount + " ед.: §9" + formatPrice(price * amount);
            }

            Format formatter = new SimpleDateFormat("dd.MM.yyyy");
            message = message + "\n§fПоследнее обновление: §9" +
                    formatter.format(new Long("" + priceObj.get("time")) * 1000);
            commandSender.sendMessage(message);
        }).start();

        return true;
    }

    private String formatPrice(float price) {
        if (price > 10) price = Math.round(price);

        if (price > 64) {
            int count = (int) Math.floor(price / 64);
            int integer = count * 64;
            float other = price - integer;
            return other == 0 ? count + " ст." : count + " ст. + " + Math.round(other);
        }

        return "" + price;
    }
}
