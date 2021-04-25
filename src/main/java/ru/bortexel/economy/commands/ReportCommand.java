package ru.bortexel.economy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import ru.bortexel.economy.Economy;
import ru.bortexel.economy.commands.report.CreateReportCommand;
import ru.bortexel.economy.commands.report.SelectShopCommand;
import ru.bortexel.economy.suggestions.ItemSuggestionProvider;
import ru.bortexel.economy.util.PermissionUtil;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class ReportCommand {
    public static final String ER_CREATE_PERMISSION = "economy.report.create";
    public static final String ER_USE_PERMISSION = "economy.report.use";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, Economy mod) {
        LiteralCommandNode<ServerCommandSource> command = dispatcher.register(
                literal("ecoreport").then(
                        literal("create").then(
                                argument("item", StringArgumentType.word()).suggests(new ItemSuggestionProvider(mod)).then(
                                        argument("quantity", IntegerArgumentType.integer(0, 3)).then(
                                                argument("price", DoubleArgumentType.doubleArg(0)).then(
                                                        argument("amount", IntegerArgumentType.integer(0))
                                                                .executes(new CreateReportCommand(mod))
                                                ).executes(new CreateReportCommand(mod))
                                        )
                                )
                        ).requires(source -> PermissionUtil.hasPermission(source, ER_CREATE_PERMISSION))
                ).then(
                        literal("select").then(
                                argument("id", IntegerArgumentType.integer(0)).executes(new SelectShopCommand(mod))
                        )
                ).requires(source -> PermissionUtil.hasPermission(source, ER_USE_PERMISSION))
        );

        dispatcher.register(literal("er").redirect(command).requires(source -> PermissionUtil.hasPermission(source, ER_USE_PERMISSION)));
    }
}
