package xyz.lilyflower.wavelength.include;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPos extends Vec3i {

	public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

	private static final int NUM_X_BITS = 26;
	private static final int NUM_Z_BITS = NUM_X_BITS;
	private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
	private static final int Y_SHIFT = NUM_Z_BITS;
	private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
	private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
	private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
	private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

	public BlockPos(int x, int y, int z) {
		super(x, y, z);
	}

	public BlockPos(double x, double y, double z) {
		super(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
	}

	public BlockPos(Entity source) {
		this(source.posX, source.posY, source.posZ);
	}

	public BlockPos(Vec3 source) {
		this(source.xCoord, source.yCoord, source.zCoord);
	}

	public BlockPos(Vec3i source) {
		this(source.getX(), source.getY(), source.getZ());
	}

	public BlockPos(ChunkCoordinates coords) {
		this(coords.posX, coords.posY, coords.posZ);
	}

	public BlockPos add(double x, double y, double z) {
		return new BlockPos(getX() + x, getY() + y, getZ() + z);
	}

	public BlockPos add(int x, int y, int z) {
		return new BlockPos(getX() + x, getY() + y, getZ() + z);
	}

	public BlockPos add(Vec3i vec) {
		return new BlockPos(getX() + vec.getX(), getY() + vec.getY(), getZ() + vec.getZ());
	}

	public BlockPos multiply(int factor) {
		return new BlockPos(getX() * factor, getY() * factor, getZ() * factor);
	}

	public BlockPos subtract(Vec3i vec) {
		return new BlockPos(getX() - vec.getX(), getY() - vec.getY(), getZ() - vec.getZ());
	}

	public BlockPos offset(EnumFacing facing) {
		return this.offset(facing, 1);
	}

	public BlockPos offset(EnumFacing facing, int n) {
		return new BlockPos(getX() + facing.getFrontOffsetX() * n, getY() + facing.getFrontOffsetY() * n,
				getZ() + facing.getFrontOffsetZ() * n);
	}

	public BlockPos offset(ForgeDirection facing) {
		return this.offset(facing, 1);
	}

	public BlockPos offset(ForgeDirection facing, int n) {
		return new BlockPos(getX() + facing.offsetX * n, getY() + facing.offsetY * n, getZ() + facing.offsetZ * n);
	}

	public BlockPos crossProductBP(Vec3i vec) {
		return new BlockPos(getY() * vec.getZ() - getZ() * vec.getY(), getZ() * vec.getX() - getX() * vec.getZ(),
				getX() * vec.getY() - getY() * vec.getX());
	}

	public long toLong() {
		return (getX() & X_MASK) << X_SHIFT | (getY() & Y_MASK) << Y_SHIFT | (getZ() & Z_MASK);
	}

	public static BlockPos fromLong(long serialized) {
		int j = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
		int k = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
		int l = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
		return new BlockPos(j, k, l);
	}

	@Override
	public Vec3i crossProduct(Vec3i vec) {
		return crossProductBP(vec);
	}

	// Roadhog360 start
	public static AxisAlignedBB getBB(BlockPos pos1, BlockPos pos2) {
		return AxisAlignedBB.getBoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(),
				pos2.getX(), pos2.getY(), pos2.getZ());
	}

	public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
		return iterate(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()),
				Math.min(start.getZ(), end.getZ()), Math.max(start.getX(), end.getX()),
				Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
	}

	public static BlockPos readFromNBT(NBTTagCompound tag) {
		return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
	}

	public static NBTTagCompound writeToNBT(BlockPos pos) {
		NBTTagCompound compoundtag = new NBTTagCompound();
		compoundtag.setInteger("X", pos.getX());
		compoundtag.setInteger("Y", pos.getY());
		compoundtag.setInteger("Z", pos.getZ());
		return compoundtag;
	}

	public TileEntity getTileEntity(World world) {
		return world.getTileEntity(getX(), getY(), getZ());
	}

	public Block getBlock(World world) {
		return world.getBlock(getX(), getY(), getZ());
	}

	public int getBlockMetadata(World world) {
		return world.getBlockMetadata(getX(), getY(), getZ());
	}

	public static Iterable<BlockPos> iterate(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		return new BlockPosIterator(startX, startY, startZ, endX, endY, endZ);
	}

	public BlockPos toImmutable() {
		return this;
	}

	public Vec3 newVec3() {
		return Vec3.createVectorHelper(getX(), getY(), getZ());
	}
}