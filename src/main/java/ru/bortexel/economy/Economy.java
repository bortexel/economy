package ru.bortexel.economy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import ru.bortexel.economy.commands.PriceCommand;
import ru.ruscalworld.bortexel4j.Bortexel4J;

public class Economy implements ModInitializer {
    @Override
    public void onInitialize() {
        Bortexel4J client = Bortexel4J.anonymous();

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            if (!dedicated) return;
            PriceCommand.register(dispatcher, client);
        }));
    }
}
