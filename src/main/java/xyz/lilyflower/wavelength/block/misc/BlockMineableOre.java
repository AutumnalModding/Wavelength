package xyz.lilyflower.wavelength.block.misc;

import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;
import xyz.lilyflower.wavelength.util.WavelengthTab;

public class BlockMineableOre extends BlockOre {
    private Item drop = Item.getItemFromBlock(this);

    public BlockMineableOre() {
        this(2, 2);
    }

    public BlockMineableOre(float hardness, int level) {
        this.setCreativeTab(WavelengthTab.RESOURCES);
        this.setHardness(hardness);
        this.setHarvestLevel("pickaxe", level);
    }

    public BlockMineableOre drops(Item drop) {
        this.drop = drop;
        return this;
    }

    @Override public Item getItemDropped(int meta, Random random, int fortune) { return this.drop; }
}
