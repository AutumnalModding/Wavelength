package xyz.lilyflower.wavelength.block.gem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.lilyflower.wavelength.client.RenderBlockCluster;

import java.util.Random;

public class BlockCluster extends BlockAmethyst {

    @SideOnly(Side.CLIENT)
    private IIcon icon;
    private final Item drop;
    private final boolean grown;

    public BlockCluster(Item drop, boolean grown) {
        super();
        this.setLightOpacity(0);
        this.drop = drop;
        this.grown = grown;
        this.setCreativeTab(null);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override
    public int getRenderType() {
        return RenderBlockCluster.RENDER_ID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.icon = register.registerIcon(this.getTextureName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icon;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override // hey - at least I left a comment documenting the magic numbers lmao
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int direction = world.getBlockMetadata(x, y, z) & 7;

        // 0.7F: length
        // 0.25F: radius
        switch (direction) {
            case 0 -> this.setBlockBounds(0.5F - 0.25F, 1.0F - 0.7F, 0.5F - 0.25F, 0.5F + 0.25F, 1.0F, 0.5F + 0.25F);
            case 1 -> this.setBlockBounds(0.5F - 0.25F, 0.0F, 0.5F - 0.25F, 0.5F + 0.25F, 0.7F, 0.5F + 0.25F);
            case 2 -> this.setBlockBounds(0.5F - 0.25F, 0.5F - 0.25F, 1.0F - 0.7F, 0.5F + 0.25F, 0.5F + 0.25F, 1.0F);
            case 3 -> this.setBlockBounds(0.5F - 0.25F, 0.5F - 0.25F, 0.0F, 0.5F + 0.25F, 0.5F + 0.25F, 0.7F);
            case 4 -> this.setBlockBounds(1.0F - 0.7F, 0.5F - 0.25F, 0.5F - 0.25F, 1.0F, 0.5F + 0.25F, 0.5F + 0.25F);
            case 5 -> this.setBlockBounds(0.0F, 0.5F - 0.25F, 0.5F - 0.25F, 0.7F, 0.5F + 0.25F, 0.5F + 0.25F);
            default -> this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.8F, 0.8F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return side;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        var opposite = side ^ 1;
        EnumFacing face = EnumFacing.getFront(opposite);
        Block there = world.getBlock(x + face.getFrontOffsetX(), y + face.getFrontOffsetY(), z + face.getFrontOffsetZ());
        return there instanceof BlockBudding;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        int direction = world.getBlockMetadata(x, y, z) & 7;
        int opposite = direction ^ 1;
        EnumFacing face = EnumFacing.getFront(opposite);
        Block there = world.getBlock(x + face.getFrontOffsetX(), y + face.getFrontOffsetY(), z + face.getFrontOffsetZ());

        if (there instanceof BlockBudding) return;
        world.setBlockToAir(x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return this.grown ? this.drop : null;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        if (!this.grown) return 0;

        int count = random.nextInt(4);
        if (fortune > 0) {
            count += random.nextInt(fortune + 1);
        }
        return count;
    }
}