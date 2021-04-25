package ru.bortexel.economy.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import ru.bortexel.economy.Economy;

import java.util.concurrent.CompletableFuture;

public class ItemSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    private final Economy mod;

    public ItemSuggestionProvider(Economy mod) {
        this.mod = mod;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        CommandSource.suggestMatching(this.getMod().getItemCache().keySet(), builder);
        return builder.buildFuture();
    }

    public Economy getMod() {
        return mod;
    }
}
