package ru.bortexel.economy.commands.report;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import ru.bortexel.economy.commands.BortexelCommand;
import ru.ruscalworld.bortexel4j.Bortexel4J;

public class CreateReportCommand extends BortexelCommand {
    public CreateReportCommand(Bortexel4J client) {
        super(client);
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return 0;
    }
}
