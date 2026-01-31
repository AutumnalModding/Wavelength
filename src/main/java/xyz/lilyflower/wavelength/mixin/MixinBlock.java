package xyz.lilyflower.wavelength.mixin;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lilyflower.wavelength.registry.WavelengthItemRegistry;

@Mixin(Block.class)
public class MixinBlock {
//    @Shadow protected double minX;
//    @Shadow protected double minY;
//    @Shadow protected double minZ;
//    @Shadow protected double maxX;
//    @Shadow protected double maxY;
//    @Shadow protected double maxZ;
//
//    @Inject(method = "addCollisionBoxesToList", at = @At("HEAD"), cancellable = true)
//    public void solidify(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider, CallbackInfo ci) {
//        Block block = (Block) (Object) this;
//        if (block instanceof BlockLiquid && collider instanceof EntityPlayer player) {
//            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
//            for (ItemStack stack : baubles.stackList) {
//                if (stack != null && stack.getItem() == WavelengthItemRegistry.RING_GRACE) {
//                    AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0, y + this.maxY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
//                    list.add(box);
//                    collider.motionY = 0;
//                    ci.cancel();
//                }
//            }
//        }
//    }
}
