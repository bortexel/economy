package ru.ruscalworld.bortexel.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.bortexel4j.models.economy.Item;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PriceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equals("price")) return false;

        if (args.length == 0) {
            commandSender.sendMessage("§fИспользование: §9/price <предмет> [количество]");
            return true;
        }

        Item.getByID(args[0]).executeAsync(item -> item.getPrices().executeAsync(prices -> {
            List<Item.ItemPrice> priceList = prices.getPrices();
            if (priceList.size() == 0) commandSender.sendMessage("§c§l[!] §fСтоимость на данный предмет не установлена.");
            Item.ItemPrice price = priceList.get(priceList.size() - 1);

            int amount = 1;
            if (args.length == 2) amount = Integer.parseInt(args[1]);

            String message = "§fАктуальная стоимость на §7" + item.getName() + "§f (§7" + item.getId() + "§f):\n" +
                    "§fЗа 1 шт.: §9" + formatPrice(price.getPrice()) + "§f";

            if (amount == 1) {
                message = message + "; за 32 шт.: §9" + (formatPrice(price.getPrice() * 32)) +
                        "§f; за 64 шт.: §9" + (formatPrice(price.getPrice() * 64));
            } else message = message + "; шт " + amount + " шт.: §9" + formatPrice(price.getPrice() * amount);

            Format formatter = new SimpleDateFormat("dd MMM yyyy", new Locale("ru"));
            message = message + "\n§fПоследнее обновление: §7" + formatter.format(price.getTime().getTime());

            commandSender.sendMessage(message);
        }), error -> commandSender.sendMessage("§c§l[!] §fПредмет не найден."));

        return true;
    }

    private String formatPrice(double price) {
        price = Math.round(price * 100D) / 100D;

        if (price > 64) {
            int stacks = (int) price / 64;
            int remaining = (int) price % 64;

            String res = "";
            if (stacks != 0) res = stacks + " ст.";
            if (remaining != 0) res = res + (stacks != 0 ? " + " : "") + remaining;

            return res;
        }

        return "" + price;
    }
}
