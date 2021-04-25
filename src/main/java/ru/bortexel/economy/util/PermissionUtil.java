package ru.bortexel.economy.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionUtil {
    public static boolean hasPermission(ServerPlayerEntity player, String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        PlayerAdapter<ServerPlayerEntity> playerAdapter = luckPerms.getPlayerAdapter(ServerPlayerEntity.class);
        return playerAdapter.getPermissionData(player).checkPermission(permission).asBoolean();
    }

    public static boolean hasPermission(ServerCommandSource source, String permission) {
        try {
            return hasPermission(source.getPlayer(), permission);
        } catch (CommandSyntaxException ignored) { }
        return false;
    }
}
