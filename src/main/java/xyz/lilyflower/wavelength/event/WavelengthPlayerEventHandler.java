package xyz.lilyflower.wavelength.event;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xyz.lilyflower.wavelength.registry.WavelengthItemRegistry;

public class WavelengthPlayerEventHandler {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(event.player);
        for (ItemStack stack : baubles.stackList) {
            if (stack != null && stack.getItem() == WavelengthItemRegistry.RING_GRACE) {
                World world = event.player.worldObj;
                int x = MathHelper.floor_double(event.player.posX);
                int y = MathHelper.floor_double(event.player.posY);
                int z = MathHelper.floor_double(event.player.posZ);
                Block there = world.getBlock(x, y - 2, z); // stupid
                if (there.getUnlocalizedName().equals("tile.water")) {
                    event.player.motionY = 0.0;
                }
            }
        }
    }
}
