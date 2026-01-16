package xyz.lilyflower.wavelength.content.block;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import xyz.lilyflower.wavelength.api.BlockTooltippable;
import xyz.lilyflower.wavelength.api.Tooltipper;
import xyz.lilyflower.wavelength.content.WavelengthTab;
import xyz.lilyflower.wavelength.init.Wavelength;
import xyz.lilyflower.wavelength.handler.GuiHandler;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;
import xyz.lilyflower.wavelength.util.MiscUtils;

public class BlockPedestal extends BlockContainer implements BlockTooltippable {
    private final Random random = new Random();
    private final TileEntityPedestal.PedestalTier tier;

    public BlockPedestal(TileEntityPedestal.PedestalTier tier) {
        super(Material.rock);
        this.setHardness(3.5F);
        this.setStepSound(soundTypeStone);
        this.setBlockName("pedestal");
        this.setCreativeTab(WavelengthTab.BLOCKS);
        this.tier = tier;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityPedestal(this.tier);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity entity = world.getTileEntity(x, y, z);

            if (entity instanceof TileEntityPedestal pedestal) {
                ItemStack held = player.getHeldItem();

                // todo: unstickify
                if (held != null && held.getItem() == Items.stick && player instanceof EntityPlayerMP multiplayer) {
                    pedestal.craft(multiplayer);
                    return true;
                }

                player.openGui(Wavelength.INSTANCE, GuiHandler.PEDESTAL_GUI_ID, world, x, y, z);
            }
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity entity = world.getTileEntity(x, y, z);

            if (entity instanceof TileEntityPedestal pedestal) {
                pedestal.name = stack.getDisplayName();
            }
        }

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Tier")) {
            TileEntity entity = world.getTileEntity(x, y, z);

            if (entity instanceof TileEntityPedestal pedestal) {
                int tier = stack.getTagCompound().getInteger("Tier");
                pedestal.setTier(TileEntityPedestal.PedestalTier.from(tier));
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        TileEntity entity = world.getTileEntity(x, y, z);

        if (entity instanceof IInventory inventory) {

            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack != null) {
                    float offsetX = this.random.nextFloat() * 0.8F + 0.1F;
                    float offsetY = this.random.nextFloat() * 0.8F + 0.1F;
                    float offsetZ = this.random.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0) {
                        int amount = this.random.nextInt(21) + 10;

                        if (amount > stack.stackSize) {
                            amount = stack.stackSize;
                        }

                        stack.stackSize -= amount;

                        EntityItem ittem = new EntityItem(world,
                                        x + offsetX, y + offsetY, z + offsetZ,
                                        new ItemStack(stack.getItem(), amount, stack.getItemDamage()));

                        if (stack.hasTagCompound()) {
                            ittem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                        }

                        float motion = 0.05F;
                        ittem.motionX = (float) this.random.nextGaussian() * motion;
                        ittem.motionY = (float) this.random.nextGaussian() * motion + 0.2F;
                        ittem.motionZ = (float) this.random.nextGaussian() * motion;
                        world.spawnEntityInWorld(ittem);
                    }
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public Tooltipper tooltipper() {
        return new Tooltipper(player -> {
            List<String> text = new ArrayList<>();
            String prefix = switch(this.tier) {
                case AMETHYST -> "§d";
                case CITRINE -> "§e";
                case TOPAZ -> "§b";
                case ONYX -> "§8";
                case MOONSTONE -> "§f";
                default -> "";
            };
            String name = switch (this.tier) {
                case CMY -> "§bC§dM§eY";
                default -> MiscUtils.CapitalizeFirst(this.tier.name().toLowerCase());
            };
            text.add(prefix + name + "§r Variant");
            return text;
        });
    }
}