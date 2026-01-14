package xyz.lilyflower.wavelength.content.block.generic;

import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;
import xyz.lilyflower.wavelength.content.WavelengthTab;

public class BlockGenericOre extends BlockOre {
    private Item drop = Item.getItemFromBlock(this);

    public BlockGenericOre() {
        this.setCreativeTab(WavelengthTab.BLOCKS);
        this.setHarvestLevel("pickaxe", 2);
    }

    public BlockGenericOre drops(Item drop) {
        this.drop = drop;
        return this;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return this.drop;
    }
}
