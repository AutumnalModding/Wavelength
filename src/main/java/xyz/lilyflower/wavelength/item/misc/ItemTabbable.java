package xyz.lilyflower.wavelength.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemTabbable extends Item {
    public ItemTabbable(CreativeTabs tab) {
        super();
        this.setCreativeTab(tab);
    }
}
