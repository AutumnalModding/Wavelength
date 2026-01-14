package xyz.lilyflower.wavelength.content.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import xyz.lilyflower.wavelength.content.WavelengthTab;

public class BlockSided extends BlockLog {
    private final String texture;

    public BlockSided(String texture, Material material) {
        this.texture = "wavelength:" + texture;
        this.setCreativeTab(WavelengthTab.BLOCKS);

        try {
            Field field = Block.class.getDeclaredField("blockMaterial");
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & -17);
            field.setAccessible(true);
            field.set(this, Material.rock);
        } catch (ReflectiveOperationException ignored) {}
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150167_a = new IIcon[1];
        this.field_150166_b = new IIcon[1];
        this.field_150167_a[0] = reg.registerIcon(this.texture);
        this.field_150166_b[0] = reg.registerIcon(this.texture + "_top");
    }
}
