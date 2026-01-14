package xyz.lilyflower.wavelength.handler;

import xyz.lilyflower.wavelength.client.gui.GuiPedestal;
import xyz.lilyflower.wavelength.inventory.ContainerPedestal;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public static final int PEDESTAL_GUI_ID = 0;
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(x, y, z);

        return switch(ID) {
            case PEDESTAL_GUI_ID -> entity instanceof TileEntityPedestal pedestal ? new ContainerPedestal(player.inventory, pedestal) : null;
            default -> null;
        };
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(x, y, z);

        return switch(ID) {
            case PEDESTAL_GUI_ID -> entity instanceof TileEntityPedestal pedestal ? new GuiPedestal(player.inventory, pedestal) : null;
            default -> null;
        };
    }
}