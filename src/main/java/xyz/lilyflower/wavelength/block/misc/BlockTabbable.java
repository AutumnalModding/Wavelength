package xyz.lilyflower.wavelength.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import xyz.lilyflower.wavelength.util.WavelengthTab;

public class BlockTabbable extends Block {
    public BlockTabbable(Material material) {
        this(material, WavelengthTab.BLOCKS);
    }

    public BlockTabbable(Material material, CreativeTabs tab) {
        super(material);
        this.setCreativeTab(tab);
    }
}
