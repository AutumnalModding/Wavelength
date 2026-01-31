package xyz.lilyflower.wavelength.block.gem;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import xyz.lilyflower.wavelength.util.WavelengthTab;

public class BlockAmethyst extends Block {
    public BlockAmethyst() {
        super(Material.rock);
        this.setHardness(1.5F);
        this.setResistance(1.5F);
        this.setCreativeTab(WavelengthTab.BLOCKS);
    }
}
