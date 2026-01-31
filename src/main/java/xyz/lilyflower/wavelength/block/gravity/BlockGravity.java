package xyz.lilyflower.wavelength.block.gravity;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.api.IGravityModifier;
import xyz.lilyflower.wavelength.util.WavelengthTab;
import xyz.lilyflower.wavelength.entity.EntityGravityBlock;
import xyz.lilyflower.wavelength.util.ChainedArrayList;

public class BlockGravity extends Block implements IGravityModifier {
    private final List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers;
    private final EnumFacing direction;

    public BlockGravity(Material material, List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers, EnumFacing direction) {
        super(material);
        this.modifiers = modifiers;
        this.direction = direction;
        this.setCreativeTab(WavelengthTab.BLOCKS);
    }

    public BlockGravity(Material material, EnumFacing direction, float amount) {
        this(material,
                new ChainedArrayList<SolarisExtensions.TriPair<Action, EnumFacing, Float>>().chain(
                new SolarisExtensions.TriPair<>(Action.ADD, direction, amount)
        ), direction);
    }

    public BlockGravity(Material material, EnumFacing direction, float amount, String type, int level) {
        this(material, direction, amount);
        this.setHarvestLevel(type, level);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) this.fall(worldIn, x, y, z);
    }

    protected void fall(World world, int x, int y, int z) {
        boolean valid = switch (this.direction) {
            case DOWN -> valid(world, x, y - 1, z);
            case UP -> valid(world, x, y + 1, z);
            case NORTH -> valid(world, x, y, z + 1);
            case SOUTH -> valid(world, x, y, z - 1);
            case EAST -> valid(world, x + 1, y, z);
            case WEST -> valid(world, x -1, y, z);
        };

        if (valid && y >= 0) {
            byte offset = 32;
            if (world.checkChunksExist(x - offset, y - offset, z - offset, x + offset, y + offset, z + offset)) {
                if (!world.isRemote) {
                    EntityGravityBlock entity = new EntityGravityBlock(world, (float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, this, world.getBlockMetadata(x, y, z));
                    entity.direction = this.direction;
                    world.spawnEntityInWorld(entity);
                }
            } else {
                world.setBlockToAir(x, y, z);
                while (valid(world, x, y - 1, z) && y > 0) --y;
                if (y > 0) world.setBlock(x, y, z, this);
            }
        }
    }

    public static boolean valid(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Material material = block.getMaterial();
        return material == Material.water || material == Material.lava || block.isAir(world, x, y, z) || block == Blocks.fire;
    }

    @Override
    public List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers() {
        return this.modifiers;
    }
}
