package ru.bortexel.economy.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import ru.ruscalworld.bortexel4j.util.Location;

public class LocationUtil {
    public static Location getPlayerLocation(ServerPlayerEntity player) {
        Vec3d pos = player.getPos();
        int x = (int) Math.round(pos.getX());
        int y = (int) Math.round(pos.getY());
        int z = (int) Math.round(pos.getZ());
        String world = player.getServerWorld().getRegistryKey().getValue().getPath();

        return new Location(x, y, z, world);
    }
}
