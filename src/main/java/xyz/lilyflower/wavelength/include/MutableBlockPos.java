package xyz.lilyflower.wavelength.include;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class MutableBlockPos extends BlockPos {
		protected int x;
		protected int y;
		protected int z;

		public MutableBlockPos() {
			this(0, 0, 0);
		}

		public MutableBlockPos(BlockPos pos) {
			this(pos.getX(), pos.getY(), pos.getZ());
		}

		public MutableBlockPos(int x_, int y_, int z_) {
			super(0, 0, 0);
			this.x = x_;
			this.y = y_;
			this.z = z_;
		}

		@Override
		public BlockPos add(double x, double y, double z) {
			return super.add(x, y, z).toImmutable();
		}

		@Override
		public BlockPos add(int x, int y, int z) {
			return super.add(x, y, z).toImmutable();
		}

		@Override
		public BlockPos offset(EnumFacing facing, int n) {
			return super.offset(facing, n).toImmutable();
		}

		@Override
		public BlockPos offset(EnumFacing p_190942_1_) {
			return super.offset(p_190942_1_).toImmutable();
		}

		@Override
		public int getX() {
			return this.x;
		}

		@Override
		public int getY() {
			return this.y;
		}

		@Override
		public int getZ() {
			return this.z;
		}

		public MutableBlockPos setPos(int xIn, int yIn, int zIn) {
			this.x = xIn;
			this.y = yIn;
			this.z = zIn;
			return this;
		}

		public MutableBlockPos setPos(Entity entityIn) {
			return this.setPos(entityIn.posX, entityIn.posY, entityIn.posZ);
		}

		public MutableBlockPos setPos(double xIn, double yIn, double zIn) {
			return this.setPos(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
		}

		public MutableBlockPos setWithOffset(Vec3i p_122160_, ForgeDirection p_122161_) {
			return setPos(p_122160_.getX() + p_122161_.offsetX, p_122160_.getY() + p_122161_.offsetY, p_122160_.getZ() + p_122161_.offsetZ);
		}

		public MutableBlockPos setWithOffset(Vec3i p_122155_, int p_122156_, int p_122157_, int p_122158_) {
			return setPos(p_122155_.getX() + p_122156_, p_122155_.getY() + p_122157_, p_122155_.getZ() + p_122158_);
		}

		public MutableBlockPos setWithOffset(Vec3i p_175307_, Vec3i p_175308_) {
			return setPos(p_175307_.getX() + p_175308_.getX(), p_175307_.getY() + p_175308_.getY(), p_175307_.getZ() + p_175308_.getZ());
		}

		public MutableBlockPos setPos(Vec3i vec) {
			return this.setPos(vec.getX(), vec.getY(), vec.getZ());
		}

		public MutableBlockPos move(EnumFacing facing) {
			return this.move(facing, 1);
		}

		public MutableBlockPos move(EnumFacing facing, int p_189534_2_) {
			return this.setPos(this.x + facing.getFrontOffsetX() * p_189534_2_, this.y + facing.getFrontOffsetY() * p_189534_2_, this.z + facing.getFrontOffsetZ() * p_189534_2_);
		}

		public void setY(int yIn) {
			this.y = yIn;
		}

		@Override
		public BlockPos toImmutable() {
			return new BlockPos(this);
		}
	}