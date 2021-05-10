package ru.bortexel.economy.commands.report;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import ru.bortexel.economy.Economy;
import ru.bortexel.economy.commands.BortexelCommand;
import ru.bortexel.economy.util.LocationUtil;
import ru.bortexel.economy.util.TextUtil;
import ru.ruscalworld.bortexel4j.core.Action;
import ru.ruscalworld.bortexel4j.models.economy.Report;

public class CreateReportCommand extends BortexelCommand {
    public CreateReportCommand(Economy mod) {
        super(mod);
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Integer shopID = this.getMod().getShopSelectionMap().get(player.getUuid());
        if (shopID == null) throw new CommandException(new LiteralText("Идентификатор магазина не задан"));

        String item = StringArgumentType.getString(context, "item");
        int quantity = IntegerArgumentType.getInteger(context, "quantity");
        double price = DoubleArgumentType.getDouble(context, "price");
        int amount = 1;
        try { amount = IntegerArgumentType.getInteger(context, "amount"); } catch (Exception ignored) { }

        Report.Builder builder = new Report.Builder();
        builder.setItemID(item);
        builder.setQuantity(quantity);
        builder.setPrice(price / amount);
        builder.setLocation(LocationUtil.getPlayerLocation(player));
        builder.setShopID(shopID);

        this.getMod().getPlayerID(player, playerID -> {
            Action<Report> action = builder.build().create(this.getMod().getClient());
            action.setExecutorID(playerID);
            action.executeAsync(report -> {
                LiteralText response = new LiteralText("Для " + report.getItemID() + " успешно записана стоимость " + report.getPrice());
                context.getSource().sendFeedback(response, true);
            }, error -> TextUtil.sendCommandError(context.getSource(), "Не удалось записать стоимость: " + error.getMessage()));
        });

        return 0;
    }
}
