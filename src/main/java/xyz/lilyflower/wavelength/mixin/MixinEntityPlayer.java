package xyz.lilyflower.wavelength.mixin;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
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
    @SuppressWarnings("unchecked")
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    public void gravify(CallbackInfo ci) {
        EntityPlayer entity = (EntityPlayer) (Object) this;

        Map<EnumFacing, Float> result = new HashMap<>();
        List<SolarisExtensions.Pair<EnumFacing, Float>>[] sets = new ArrayList[0];


        List<ItemStack> inventory = new ArrayList<>(Arrays.asList(entity.inventory.mainInventory));
        ItemStack held = entity.getHeldItem(); inventory.remove(held);
        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(entity);

        sets = IGravityModifier.populate(entity.inventory.armorInventory, IGravityModifier.ContainerType.ARMOUR, sets);
        sets = IGravityModifier.populate(inventory.toArray(new ItemStack[0]), IGravityModifier.ContainerType.INVENTORY, sets);
        if (held != null) sets = IGravityModifier.populate(new ItemStack[]{held}, IGravityModifier.ContainerType.HELD, sets);
        sets = IGravityModifier.populate(baubles.stackList, IGravityModifier.ContainerType.BAUBLES, sets);
        for (List<SolarisExtensions.Pair<EnumFacing, Float>> set : sets) IGravityModifier.process(result, set);
        IGravityModifier.apply(result, entity);
    }
}
