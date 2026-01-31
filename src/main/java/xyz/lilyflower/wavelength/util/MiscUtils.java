package xyz.lilyflower.wavelength.util;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;

public class MiscUtils {
    public static boolean achieved(EntityPlayer player, Achievement achievement) {
        if (player instanceof EntityPlayerMP server) return server.func_147099_x().hasAchievementUnlocked(achievement);
        if (player instanceof EntityClientPlayerMP client) return client.getStatFileWriter().hasAchievementUnlocked(achievement);
        return false;
    }

    public static String properify(String input) { return (char) (input.charAt(0) - 32) + input.substring(1); }
}
