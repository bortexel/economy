package ru.bortexel.economy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import ru.bortexel.economy.commands.report.CreateReportCommand;
import ru.bortexel.economy.commands.report.SelectShopCommand;
import ru.bortexel.economy.util.PermissionUtil;
import ru.ruscalworld.bortexel4j.Bortexel4J;

import static net.minecraft.server.command.CommandManager.literal;

public class ReportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, Bortexel4J client) {
        LiteralCommandNode<ServerCommandSource> command = dispatcher.register(
                literal("ecoreport").then(
                        literal("create").then(
                                CommandManager.argument("item", StringArgumentType.word()).then(
                                        CommandManager.argument("quantity", IntegerArgumentType.integer(0, 3)).then(
                                                CommandManager.argument("price", DoubleArgumentType.doubleArg(0)).then(
                                                        CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                                .executes(new CreateReportCommand(client))
                                                ).executes(new CreateReportCommand(client))
                                        )
                                )
                        ).requires(source -> PermissionUtil.hasPermission(source, "economy.report.create"))
                ).then(
                        literal("select").then(
                                CommandManager.argument("id", IntegerArgumentType.integer(0))
                                        .executes(new SelectShopCommand(client))
                        )
                ).requires(source -> PermissionUtil.hasPermission(source, "economy.report"))
        );

        dispatcher.register(literal("er").redirect(command).requires(source -> PermissionUtil.hasPermission(source, "economy.report")));
    }
}
