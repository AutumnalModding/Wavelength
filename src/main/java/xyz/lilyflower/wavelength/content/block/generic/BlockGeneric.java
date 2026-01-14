package xyz.lilyflower.wavelength.content.block.generic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import xyz.lilyflower.wavelength.content.WavelengthTab;

public class BlockGeneric extends Block {
    public BlockGeneric(Material material) {
        this(material, WavelengthTab.BLOCKS);
    }

    public BlockGeneric(Material material, CreativeTabs tab) {
        super(material);
        this.setCreativeTab(tab);
    }
}
