package xyz.lilyflower.wavelength.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xyz.lilyflower.wavelength.block.gravity.BlockGravity;

public class EntityGravityBlock extends Entity implements IEntityAdditionalSpawnData {
    public Block block;
    public int metadata;
    public int age;
    public boolean droppable;
    private boolean hurt;
    private int max;
    private float damage;
    public NBTTagCompound data;
    public EnumFacing direction = EnumFacing.DOWN;

    public EntityGravityBlock(World world) {
        super(world);
        this.droppable = true;
        this.max = 40;
        this.damage = 2.0F;
    }

    public EntityGravityBlock(World world, double x, double y, double z, Block block) {
        this(world, x, y, z, block, 0);
    }

    public EntityGravityBlock(World world, double x, double y, double z, Block block, int metadata) {
        super(world);
        this.droppable = true;
        this.max = 40;
        this.damage = 2.0F;
        this.block = block;
        this.metadata = metadata;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(x, y, z);
        this.motionX = 0.0D; this.motionY = 0.0D; this.motionZ = 0.0D;
        this.prevPosX = x; this.prevPosY = y; this.prevPosZ = z;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {}

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void onUpdate() {
        if (this.block.getMaterial() == Material.air) {
            this.setDead();
            return;
        }

        ++this.age;
        this.prevPosX = this.posX; this.prevPosY = this.posY; this.prevPosZ = this.posZ;
        switch (this.direction) {
            case DOWN -> this.motionY -= 0.03D;
            case UP -> this.motionY += 0.03D;
            case NORTH -> this.motionZ += 0.03D;
            case SOUTH -> this.motionZ -= 0.03D;
            case EAST -> this.motionX += 0.03D;
            case WEST -> this.motionX -= 0.03D;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.7D; this.motionY *= 0.7D; this.motionZ *= 0.7D;

        if (!this.worldObj.isRemote) {
            int x = MathHelper.floor_double(this.posX); int y = MathHelper.floor_double(this.posY); int z = MathHelper.floor_double(this.posZ);

            if (this.age == 1) {
                if (this.worldObj.getBlock(x, y, z) != this.block) {
                    this.setDead();
                    return;
                }

                this.worldObj.setBlockToAir(x, y, z);
            }

            if (switch (this.direction) {
                case DOWN -> this.onGround;
                case UP -> this.motionY > -0.001D && this.motionY < 0.001D;
                case NORTH, SOUTH -> Math.abs(this.motionZ) < 0.001D;
                case EAST, WEST -> Math.abs(this.motionX) < 0.001D;
            }) {
                this.setDead();

                if (this.worldObj.canPlaceEntityOnSide(this.block, x, y, z, true, 1, null, null) && !switch (this.direction) {
                    case DOWN -> BlockGravity.valid(this.worldObj, x, y - 1, z);
                    case UP -> BlockGravity.valid(this.worldObj, x, y + 1, z);
                    case NORTH -> BlockGravity.valid(this.worldObj, x, y, z + 1);
                    case SOUTH -> BlockGravity.valid(this.worldObj, x, y, z - 1);
                    case EAST -> BlockGravity.valid(this.worldObj, x + 1, y, z);
                    case WEST -> BlockGravity.valid(this.worldObj, x -1, y, z);
                } && this.worldObj.setBlock(x, y, z, this.block, this.metadata, 3)) {
                    if (this.data != null && this.block instanceof ITileEntityProvider) {
                        TileEntity entity = this.worldObj.getTileEntity(x, y, z);

                        if (entity != null) {
                            NBTTagCompound compound = new NBTTagCompound();
                            entity.writeToNBT(compound);

                            for (Object object : this.data.func_150296_c()) {
                                String key = (String) object;
                                NBTBase tag = this.data.getTag(key);

                                if (!key.equals("x") && !key.equals("y") && !key.equals("z")) {
                                    compound.setTag(key, tag.copy());
                                }
                            }

                            entity.readFromNBT(compound);
                            entity.markDirty();
                        }
                    }
                } else if (this.droppable) {
                    this.entityDropItem(new ItemStack(this.block, 1, this.block.damageDropped(this.metadata)), 0.0F);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void fall(float distance) {
        if (this.hurt) {
            int ceiling = MathHelper.ceiling_float_int(distance - 1.0F);

            if (ceiling > 0) {
                ArrayList<Entity> list = new ArrayList<Entity>(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
                DamageSource source = DamageSource.fallingBlock;

                for (Entity entity : list) {
                    entity.attackEntityFrom(source, (float) Math.min(MathHelper.floor_float((float) ceiling * this.damage), this.max));
                }
            }
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("Block", Block.getIdFromBlock(this.block));
        tag.setInteger("Data", this.metadata);
        tag.setInteger("Time", this.age);
        tag.setBoolean("DropItem", this.droppable);
        tag.setBoolean("HurtEntities", this.hurt);
        tag.setFloat("FallHurtAmount", this.damage);
        tag.setInteger("FallHurtMax", this.max);

        if (this.data != null) tag.setTag("TileEntityData", this.data);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.block = Block.getBlockById(tag.getInteger("Block"));

        this.metadata = tag.getInteger("Data");
        this.age = tag.getInteger("Time");

        if (tag.hasKey("HurtEntities", 99)) {
            this.hurt = tag.getBoolean("HurtEntities");
            this.damage = tag.getFloat("FallHurtAmount");
            this.max = tag.getInteger("FallHurtMax");
        }

        if (tag.hasKey("DropItem", 99)) this.droppable = tag.getBoolean("DropItem");
        if (tag.hasKey("TileEntityData", 10)) this.data = tag.getCompoundTag("TileEntityData");
    }

    @Override
    public void addEntityCrashInfo(CrashReportCategory category) {
        super.addEntityCrashInfo(category);
        category.addCrashSection("Imitating block ID", Block.getIdFromBlock(this.block));
        category.addCrashSection("Imitating block data", this.metadata);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(Block.getIdFromBlock(this.block));
        buffer.writeInt(this.metadata);
        buffer.writeInt(this.direction.ordinal());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.block = Block.getBlockById(buffer.readInt());
        this.metadata = buffer.readInt();
        this.direction = EnumFacing.values()[buffer.readInt()];
    }
}
