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
import ru.bortexel.economy.Economy;
import ru.bortexel.economy.suggestions.ItemSuggestionProvider;
import ru.bortexel.economy.util.TextUtil;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.models.economy.Item;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.literal;

public class PriceCommand extends BortexelCommand {
    public PriceCommand(Economy mod) {
        super(mod);
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        Bortexel4J client = this.getMod().getClient();
        String itemID = StringArgumentType.getString(context, "item");
        if (!this.getMod().getItemCache().containsKey(itemID))
            throw new CommandException(new LiteralText("Предмет \"" + itemID + "\" не найден"));

        Item.getByID(itemID, client).executeAsync(item -> item.getPrices(client).executeAsync(prices -> {
            List<Item.ItemPrice> priceList = prices.getPrices();
            if (priceList.size() == 0) throw new CommandException(new LiteralText("Стоимость на данный предмет не установлена"));
            Item.ItemPrice price = priceList.get(priceList.size() - 1);

            int amount = 1;
            try {
                amount = IntegerArgumentType.getInteger(context, "amount");
            } catch (Exception ignored) { }

            MutableText text = new LiteralText("");
            text.append(new LiteralText(item.getName() + "\n").formatted(Formatting.BOLD));
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
        }, error -> TextUtil.sendCommandError(context.getSource(), "Предмет с заданным идентификатором не существует")));
        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, Economy mod) {
        LiteralCommandNode<ServerCommandSource> command = dispatcher.register(
                CommandManager.literal("price").then(
                        CommandManager.argument("item", StringArgumentType.string()).suggests(new ItemSuggestionProvider(mod)).then(
                                CommandManager.argument("amount", IntegerArgumentType.integer(0)).executes(new PriceCommand(mod))
                        ).executes(new PriceCommand(mod))));
        dispatcher.register(literal("стоимость").redirect(command));
    }
}
