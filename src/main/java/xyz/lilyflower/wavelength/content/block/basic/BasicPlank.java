package xyz.lilyflower.wavelength.content.block.basic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BasicPlank extends BlockBasic {
    private final String texture;

    public BasicPlank(String texture) {
        super(Material.wood);
        this.texture = "wavelength:" + texture;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.texture);
    }
}
