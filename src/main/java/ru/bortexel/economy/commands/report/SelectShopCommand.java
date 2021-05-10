package ru.bortexel.economy.commands.report;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import ru.bortexel.economy.Economy;
import ru.bortexel.economy.commands.BortexelCommand;
import ru.bortexel.economy.util.PermissionUtil;
import ru.bortexel.economy.util.TextUtil;
import ru.ruscalworld.bortexel4j.models.shop.Shop;

public class SelectShopCommand extends BortexelCommand {
    public SelectShopCommand(Economy mod) {
        super(mod);
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        int id = IntegerArgumentType.getInteger(context, "id");
        this.getMod().getShopSelectionMap().put(player.getUuid(), id);

        Shop.getByID(id, this.getMod().getClient()).executeAsync(shop -> this.getMod().getPlayerID(player, playerID -> {
            if (shop.getOwnerID() != playerID && !PermissionUtil.hasPermission(player, "economy.report.inspector")) {
                TextUtil.sendCommandError(context.getSource(), "Этот магазин Вам не принадлежит");
                return;
            }

            if (!shop.isActive()) {
                TextUtil.sendCommandError(context.getSource(), "Этот магазин неактивен, записать цены в нём невозможно");
                return;
            }

            if (!shop.isVerified() && !PermissionUtil.hasPermission(player, "economy.report.inspector")) {
                TextUtil.sendCommandError(context.getSource(), "Этот магазин не подтверждён, и записать цены в нём может только ревизор");
                return;
            }

            this.getMod().getShopSelectionMap().put(player.getUuid(), id);
            context.getSource().sendFeedback(new LiteralText("Успешно выбран " + shop.getName()), true);
        }), error -> TextUtil.sendCommandError(context.getSource(), "Не удалось выбрать магазин: " + error.getMessage()));

        return 0;
    }
}
