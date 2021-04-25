package ru.bortexel.economy.commands;

import com.mojang.brigadier.Command;
import net.minecraft.server.command.ServerCommandSource;
import ru.ruscalworld.bortexel4j.Bortexel4J;

public abstract class BortexelCommand implements Command<ServerCommandSource> {
    private final Bortexel4J client;

    protected BortexelCommand(Bortexel4J client) {
        this.client = client;
    }

    public Bortexel4J getClient() {
        return client;
    }
}
