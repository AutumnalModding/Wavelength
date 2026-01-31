package xyz.lilyflower.wavelength.item.bauble;

import baubles.api.BaubleType;
import baubles.common.BaubleItemBase;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import xyz.lilyflower.solaris.util.SolarisExtensions.TriPair;
import xyz.lilyflower.wavelength.api.IGravityModifier;
import xyz.lilyflower.wavelength.util.ChainedArrayList;
import xyz.lilyflower.wavelength.util.WavelengthTab;

public class ItemRingGrace extends BaubleItemBase implements IGravityModifier {
    public ItemRingGrace() {
        super();
        this.setCreativeTab(WavelengthTab.EQUIPMENT);
    }

    @Override // why did azanor do this lmao
    public boolean hasEffect(ItemStack stack, int pass) {
        return false;
    }

    @Override
    public boolean valid(ContainerType type) {
        return type == ContainerType.BAUBLES;
    }

    @Override
    public List<TriPair<Action, EnumFacing, Float>> modifiers() {
        return new ChainedArrayList<TriPair<Action, EnumFacing, Float>>()
                .chain(new TriPair<>(Action.MULTIPLY, EnumFacing.UP, 2F))
                .chain(new TriPair<>(Action.DIVIDE, EnumFacing.DOWN, 2F))
                .chain(new TriPair<>(Action.ADD, EnumFacing.UP, 0.05F));
    }

    @Override
    public String[] getBaubleTypes(ItemStack itemstack) {
        return new String[]{"ring"};
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }
}
