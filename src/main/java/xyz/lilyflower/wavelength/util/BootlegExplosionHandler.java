package xyz.lilyflower.wavelength.util;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BootlegExplosionHandler {
    public static void cube(World world, BlockPos origin, int horizontal, int vertical) {
        int maxX = origin.x + horizontal;
        int minX = origin.x - horizontal;
        int maxZ = origin.z + horizontal;
        int minZ = origin.z - horizontal;

        int radiusY = MathHelper.clamp_int(origin.y - vertical, 0, 255);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = radiusY; y < vertical + origin.y; y++) {
                    Block there = world.getBlock(x, y, z);
                    if (there.getMaterial() != Material.air) {
                        world.setBlock(x, y, z, Blocks.air);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void sphere(World world, BlockPos origin, int horizontal, int vertical) {
        int radiusY = MathHelper.clamp_int(origin.y - vertical, 0, 255);
        int horizontalSquared = horizontal * horizontal;
        int verticalSquared = vertical * vertical;

        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(
                origin.x - horizontal, radiusY, origin.z - horizontal,
                origin.x + horizontal, vertical + origin.y, origin.z + horizontal
        );

        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, bounds);

        for (Entity entity : entities) {
            int dx = (int)entity.posX - origin.x;
            int dy = (int)entity.posY - origin.y;
            int dz = (int)entity.posZ - origin.z;

            int normalized = dx * dx + dz * dz +
                    ((dy * dy * horizontalSquared) / verticalSquared);

            if (normalized <= horizontalSquared) {
                entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                entity.setDead();
            }
        }

        for (int x = origin.x - horizontal; x <= origin.x + horizontal; x++) {
            for (int z = origin.z - horizontal; z <= origin.z + horizontal; z++) {
                for (int y = radiusY; y < vertical + origin.y; y++) {
                    int dx = x - origin.x;
                    int dy = y - origin.y;
                    int dz = z - origin.z;

                    int normalized = dx * dx + dz * dz +
                            ((dy * dy * horizontalSquared) / verticalSquared);

                    if (normalized <= horizontalSquared) {
                        Block there = world.getBlock(x, y, z);
                        if (there.getMaterial() != Material.air) {
                            world.setBlock(x, y, z, Blocks.air);
                        }
                    }
                }
            }
        }
    }
}
