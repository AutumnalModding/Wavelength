package xyz.lilyflower.wavelength.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.api.IGravityModifier;

@Mixin(EntityLiving.class)
public class MixinEntityLiving {
    @SuppressWarnings("unchecked")
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    public void gravify(CallbackInfo ci) {
        EntityLiving entity = (EntityLiving) (Object) this;

        Map<EnumFacing, Float> result = new HashMap<>();
        List<SolarisExtensions.Pair<EnumFacing, Float>>[] sets = new ArrayList[]{
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        };

        List<ItemStack> equipment = new ArrayList<>(Arrays.asList(entity.getLastActiveItems()));
        ItemStack held = entity.getHeldItem(); equipment.remove(held);
        IGravityModifier.populate(equipment.toArray(new ItemStack[0]), IGravityModifier.ContainerType.ARMOUR, sets);
        if (held != null) IGravityModifier.populate(new ItemStack[]{held}, IGravityModifier.ContainerType.HELD, sets);

        for (List<SolarisExtensions.Pair<EnumFacing, Float>> set : sets) IGravityModifier.process(result, set);
        IGravityModifier.apply(result, entity);
    }
}
