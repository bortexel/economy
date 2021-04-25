package ru.bortexel.economy.util;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class TextUtil {
    public static String formatPrice(double price) {
        price = Math.round(price * 100D) / 100D;

        if (price > 64) {
            int stacks = (int) price / 64;
            int remaining = (int) price % 64;

            String res = "";
            if (stacks != 0) res = stacks + " ст.";
            if (remaining != 0) res = res + (stacks != 0 ? " + " : "") + remaining;

            return res;
        }

        return "" + price;
    }

    public static void sendCommandError(ServerCommandSource source, String message) {
        source.sendFeedback(new LiteralText(message).formatted(Formatting.RED), false);
    }
}
