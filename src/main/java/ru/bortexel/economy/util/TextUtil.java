package ru.bortexel.economy.util;

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
}
