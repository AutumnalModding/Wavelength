package xyz.lilyflower.wavelength.util;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;

public class MiscUtils {
    public static boolean HasAchievement(EntityPlayer player, Achievement achievement) {
        if (player instanceof EntityPlayerMP server) return server.func_147099_x().hasAchievementUnlocked(achievement);
        if (player instanceof EntityClientPlayerMP client) return client.getStatFileWriter().hasAchievementUnlocked(achievement);
        return false;
    }

    public static String CapitalizeFirst(String input) {
        char first = input.charAt(0);
        first -= 32;
        return first + input.substring(1);
    }
}
