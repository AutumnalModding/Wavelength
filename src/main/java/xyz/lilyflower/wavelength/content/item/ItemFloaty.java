package xyz.lilyflower.wavelength.content.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.api.IGravityModifier;
import xyz.lilyflower.wavelength.util.ChainedArrayList;

public class ItemFloaty extends ItemBasic implements IGravityModifier {
    private final List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers;


    public ItemFloaty(CreativeTabs tab, List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers) {
        super(tab);
        this.modifiers = modifiers;
    }

    public ItemFloaty(CreativeTabs tab, EnumFacing direction, float amount) {
        this(tab,
                new ChainedArrayList<SolarisExtensions.TriPair<Action, EnumFacing, Float>>().chainedAdd(
                new SolarisExtensions.TriPair<>(Action.ADD, direction, amount)
            )
        );
    }

    @Override
    public List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers() {
        return this.modifiers;
    }
}
