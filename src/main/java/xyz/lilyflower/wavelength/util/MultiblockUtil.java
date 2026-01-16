package xyz.lilyflower.wavelength.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import xyz.lilyflower.wavelength.include.BlockMetaPair;

public class MultiblockUtil {
    public static boolean check(BlockPos[] blocks, BlockMetaPair[] targets, World world, BlockPos origin) {
        boolean valid = true;
        for (int index = 0; index < blocks.length; index++) {
            BlockMetaPair pair = targets[index];
            Block block = pair.get();
            int meta = pair.getMeta();

            BlockPos target = blocks[index];
            valid &= validate(target, origin, world, block, meta);
        }

        return valid;
    }

    public static boolean validate(BlockPos target, BlockPos origin, World world, Block block, int meta) {
        int x = origin.x + target.x;
        int y = origin.y + target.y;
        int z = origin.z + target.z;
        if (!world.blockExists(x, y, z)) {
            return true;
        }

        Block there = world.getBlock(x, y, z);
        if (there == block) {
            if (meta == -1) return true;
            int that = world.getBlockMetadata(x, y, z);
            return meta == that;
        }

        return false;
    }
}
