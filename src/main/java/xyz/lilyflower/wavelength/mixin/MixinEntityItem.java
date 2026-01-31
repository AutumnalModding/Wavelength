package xyz.lilyflower.wavelength.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.api.IGravityModifier;

@Mixin(EntityItem.class)
public class MixinEntityItem {
    @SuppressWarnings("unchecked")
    @Inject(method = "onUpdate", at = @At("HEAD"))
    public void gravify(CallbackInfo ci) {
        EntityItem entity = (EntityItem) (Object) this;
        ItemStack stack = entity.getEntityItem();

        Map<EnumFacing, Float> result = new HashMap<>();
        List<SolarisExtensions.Pair<EnumFacing, Float>>[] sets = new ArrayList[0];

        if (stack != null) sets = IGravityModifier.populate(new ItemStack[]{stack}, IGravityModifier.ContainerType.INVENTORY, sets);
        for (List<SolarisExtensions.Pair<EnumFacing, Float>> set : sets) IGravityModifier.process(result, set);
        IGravityModifier.apply(result, entity);
    }
}
