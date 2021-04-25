package ru.bortexel.economy.commands;

import com.mojang.brigadier.Command;
import net.minecraft.server.command.ServerCommandSource;
import ru.bortexel.economy.Economy;

public abstract class BortexelCommand implements Command<ServerCommandSource> {
    private final Economy mod;

    protected BortexelCommand(Economy mod) {
        this.mod = mod;
    }

    public Economy getMod() {
        return mod;
    }
}
