package xyz.lilyflower.wavelength.item.bauble;

import baubles.api.BaubleType;
import baubles.common.BaubleItemBase;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.api.IGravityModifier;
import xyz.lilyflower.wavelength.util.ChainedArrayList;
import xyz.lilyflower.wavelength.util.WavelengthTab;

public class ItemRingDensity extends BaubleItemBase implements IGravityModifier {
    public ItemRingDensity() {
        super();
        this.setCreativeTab(WavelengthTab.EQUIPMENT);
    }

    @Override
    public boolean valid(ContainerType type) {
        return type == ContainerType.BAUBLES;
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return false;
    }

    @Override
    public List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers() {
        return new ChainedArrayList<SolarisExtensions.TriPair<Action, EnumFacing, Float>>()
                .chain(new SolarisExtensions.TriPair<>(Action.DIVIDE, EnumFacing.UP, 2F))
                .chain(new SolarisExtensions.TriPair<>(Action.ADD, EnumFacing.DOWN, 0.05F))
                .chain(new SolarisExtensions.TriPair<>(Action.MULTIPLY, EnumFacing.DOWN, 2F));
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
