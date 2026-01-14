package xyz.lilyflower.wavelength.content.block.basic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import xyz.lilyflower.wavelength.content.WavelengthTab;

public class BlockBasic extends Block {
    public BlockBasic(Material material) {
        this(material, WavelengthTab.BLOCKS);
    }

    public BlockBasic(Material material, CreativeTabs tab) {
        super(material);
        this.setCreativeTab(tab);
    }
}
