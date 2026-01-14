package xyz.lilyflower.wavelength.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.api.IGravityModifier;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    public void gravify(CallbackInfo ci) {
        EntityPlayer entity = (EntityPlayer) (Object) this;

        Map<EnumFacing, Float> result = new HashMap<>();
        List<SolarisExtensions.Pair<EnumFacing, Float>> increase = new ArrayList<>();
        List<SolarisExtensions.Pair<EnumFacing, Float>> subtract = new ArrayList<>();
        List<SolarisExtensions.Pair<EnumFacing, Float>> multiply = new ArrayList<>();
        List<SolarisExtensions.Pair<EnumFacing, Float>> division = new ArrayList<>();

        ItemStack[] main = entity.inventory.mainInventory;
        ItemStack[] armour = entity.inventory.armorInventory;
        ItemStack[] inventory = new ItemStack[256]; // All the items you could ever want.

        System.arraycopy(main, 0, inventory, 0, main.length);
        System.arraycopy(armour, 0, inventory, main.length, armour.length);

        for (ItemStack stack : inventory) {
            if (stack != null) {
                Item that = stack.getItem();
                IGravityModifier.populate(that, increase, subtract, multiply, division);
            }
        }

        IGravityModifier.process(result, increase);
        IGravityModifier.process(result, subtract);
        IGravityModifier.process(result, multiply);
        IGravityModifier.process(result, division);
        IGravityModifier.apply(result, entity);
    }
}
