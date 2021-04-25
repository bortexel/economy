package ru.bortexel.economy.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import ru.bortexel.economy.util.TextUtil;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.economy.Item;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.*;

public class PriceCommand extends BortexelCommand {
    public PriceCommand(Bortexel4J client) {
        super(client);
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        Item.getByID(StringArgumentType.getString(context, "item"), this.getClient()).executeAsync(item -> item.getPrices(this.getClient()).executeAsync(prices -> {
            List<Item.ItemPrice> priceList = prices.getPrices();
            if (priceList.size() == 0) throw new CommandException(new LiteralText("Стоимость на данный предмет не установлена"));
            Item.ItemPrice price = priceList.get(priceList.size() - 1);

            int amount = 1;
            try {
                amount = IntegerArgumentType.getInteger(context, "amount");
            } catch (Exception ignored) { }

            MutableText text = new LiteralText("");
            text.append(new LiteralText("Цена на " + item.getName().toLowerCase() + "\n").formatted(Formatting.BOLD));
            text.append(new LiteralText("За 1 шт.: ").formatted(Formatting.RESET))
                    .append(new LiteralText(TextUtil.formatPrice(price.getPrice())).formatted(Formatting.BLUE));
            text.append("; ");

            if (amount == 1) {
                text.append(new LiteralText("за 32 шт.: "))
                        .append(new LiteralText(TextUtil.formatPrice(price.getPrice() * 32)).formatted(Formatting.BLUE));

                text.append("; ");
                text.append(new LiteralText("за 64 шт.: "))
                        .append(new LiteralText(TextUtil.formatPrice(price.getPrice() * 64)).formatted(Formatting.BLUE));
            } else text.append(new LiteralText("за " + amount + " шт.: "))
                    .append(new LiteralText(TextUtil.formatPrice(price.getPrice() * amount)).formatted(Formatting.BLUE));

            Format formatter = new SimpleDateFormat("dd MMM yyyy", new Locale("ru"));
            text.append("\n").append(new LiteralText("Последнее обновление: "))
                    .append(new LiteralText(formatter.format(price.getTime().getTime())).formatted(Formatting.GRAY));

            context.getSource().sendFeedback(text, false);
        }, error -> {
            throw new CommandException(new LiteralText("Предмет с заданным идентификатором не существует"));
        }));
        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, Bortexel4J client) {
        LiteralCommandNode<ServerCommandSource> command = dispatcher.register(
                CommandManager.literal("price")
                        .then(CommandManager.argument("item", StringArgumentType.word())
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0)))
                                    .executes(new PriceCommand(client))
                                .executes(new PriceCommand(client))));
        dispatcher.register(literal("стоимость").redirect(command));
    }
}