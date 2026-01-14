package xyz.lilyflower.wavelength.content.block.gem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockBudding extends BlockAmethyst {
    private final List<BlockCluster> clusters;

    @SuppressWarnings("unused")
    public BlockBudding(BlockCluster small, BlockCluster medium, BlockCluster large, BlockCluster grown) {
        this.clusters = new ArrayList<>();
        this.clusters.add(small);
        this.clusters.add(medium);
        this.clusters.add(large);
        this.clusters.add(grown);
    }

    @SuppressWarnings("unused")
    public BlockBudding(List<BlockCluster> clusters) {
        this.clusters = clusters;
    }

    @Override
    public boolean canSilkHarvest() {
        return false;
    }

    @Override
    public int getMobilityFlag() {
        return 1;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (random.nextInt(5) != 0) return;

        var facing = EnumFacing.getFront(random.nextInt(6));
        int tx = x + facing.getFrontOffsetX();
        int ty = y + facing.getFrontOffsetY();
        int tz = z + facing.getFrontOffsetZ();

        var there = world.getBlock(tx, ty, tz);

        if (there instanceof BlockCluster cluster) {
            int index = clusters.indexOf(cluster);
            if (index < clusters.size() - 1 && index != -1) {
                var target = clusters.get(index + 1);
                world.setBlock(tx, ty, tz, target, facing.ordinal(), 3);
            }
        } else if (there instanceof BlockAir) {
            var target = clusters.get(0);
            world.setBlock(tx, ty, tz, target, facing.ordinal(), 3);
        }
    }

    @Override
    public boolean getTickRandomly() {
        return true;
    }
}