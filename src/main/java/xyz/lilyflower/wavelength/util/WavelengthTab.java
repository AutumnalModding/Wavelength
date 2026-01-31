package xyz.lilyflower.wavelength.util;

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

    public static final WavelengthTab BLOCKS = new WavelengthTab("wavelength_blocks", Items.brick);
    public static final WavelengthTab RESOURCES = new WavelengthTab("wavelength_materials", Items.iron_ingot);
    public static final WavelengthTab EQUIPMENT = new WavelengthTab("wavelength_gear", Items.iron_chestplate);
}
