package xyz.lilyflower.wavelength.content.block.generic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockGenericPlank extends BlockGeneric {
    private final String texture;

    public BlockGenericPlank(String texture) {
        super(Material.wood);
        this.texture = "wavelength:" + texture;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.texture);
    }
}
