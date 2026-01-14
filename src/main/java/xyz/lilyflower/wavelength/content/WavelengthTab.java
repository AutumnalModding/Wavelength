package xyz.lilyflower.wavelength.content;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WavelengthTab extends CreativeTabs {
    private Item icon;
    private ItemStack stack;

    public WavelengthTab(String label, Item icon) {
        super(label);
        this.icon = icon;
    }

    @Override
    public Item getTabIconItem() {
        return this.icon;
    }

    @Override
    public ItemStack getIconItemStack() {
        if (this.stack == null) this.stack = new ItemStack(this.icon);
        return this.stack;
    }

    public void setIcon(Item icon) {
        this.icon = icon;
        this.stack = new ItemStack(this.icon);
    }

    public static final WavelengthTab BLOCKS = new WavelengthTab("wavelength_blocks", Item.getItemFromBlock(Blocks.stone));
    public static final WavelengthTab MATERIALS = new WavelengthTab("wavelength_materials", Items.iron_ingot);
}
