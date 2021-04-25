package ru.bortexel.economy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import ru.bortexel.economy.Economy;
import ru.bortexel.economy.util.Ruslan;
import ru.bortexel.economy.util.TextUtil;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class RuslanCommand extends BortexelCommand {
    protected RuslanCommand(Economy mod) {
        super(mod);
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String text = StringArgumentType.getString(context, "text");
        Ruslan.getAnswer(text, response -> {
            if (response == null || response.getResponse().length() < 1) {
                TextUtil.sendCommandError(context.getSource(), "Не удалось получить ответ");
                return;
            }

            context.getSource().sendFeedback(new LiteralText(response.getResponse()), false);
        });
        return 0;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, Economy mod) {
        LiteralCommandNode<ServerCommandSource> command = dispatcher.register(literal("ruslan").then(
                argument("text", StringArgumentType.greedyString()).executes(new RuslanCommand(mod))
        ));

        dispatcher.register(literal("руслан").redirect(command));
    }
}
