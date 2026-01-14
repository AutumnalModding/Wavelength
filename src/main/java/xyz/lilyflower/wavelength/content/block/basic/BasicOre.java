package xyz.lilyflower.wavelength.content.block.basic;

import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;
import xyz.lilyflower.wavelength.content.WavelengthTab;

public class BasicOre extends BlockOre {
    private Item drop = Item.getItemFromBlock(this);

    public BasicOre() {
        this.setCreativeTab(WavelengthTab.RESOURCES);
        this.setHarvestLevel("pickaxe", 2);
    }

    public BasicOre drops(Item drop) {
        this.drop = drop;
        return this;
    }

    @Override public Item getItemDropped(int meta, Random random, int fortune) { return this.drop; }
}
