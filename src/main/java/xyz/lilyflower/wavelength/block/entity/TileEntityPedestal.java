package xyz.lilyflower.wavelength.block.entity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import xyz.lilyflower.wavelength.util.PastelType;
import xyz.lilyflower.wavelength.util.PedestalRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;
import xyz.lilyflower.wavelength.util.recipe.PedestalRecipeManager;

public class TileEntityPedestal extends TileEntity implements ISidedInventory {
    public enum PedestalTier {
        TOPAZ(13),
        CITRINE(13),
        AMETHYST(13),
        CMY(13),
        ONYX(14),
        MOONSTONE(15);

        private final int size;

        PedestalTier(int size) {
            this.size = size;
        }

        public int size() {
            return size;
        }

        public static PedestalTier from(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                return AMETHYST;
            }
            return values()[ordinal];
        }
    }

    public ItemStack[] inventory;
    public PedestalTier tier;
    public String name;
    private final Map<PastelType, Integer> catalysts;

    public boolean active;
    public int progress;
    private PedestalRecipe recipe;
    public EntityPlayerMP player;

    @SuppressWarnings("unused")
    public TileEntityPedestal() {
        this(PedestalTier.AMETHYST);
    }

    public TileEntityPedestal(PedestalTier tier) {
        this.tier = tier;
        this.inventory = new ItemStack[tier.size()];
        this.catalysts = new HashMap<>();
        this.active = false;
        this.progress = 0;
        for (PastelType type : PastelType.values()) {
            this.catalysts.put(type, 0);
        }
    }

    public void setTier(PedestalTier tier) {
        if (this.tier != tier) {
            ItemStack[] old = this.inventory;
            this.tier = tier;
            this.inventory = new ItemStack[tier.size()];

            int size = Math.min(old.length, this.inventory.length);
            System.arraycopy(old, 0, this.inventory, 0, size);

            this.markDirty();
        }
    }

    public void craft(EntityPlayerMP player) {
        if (this.active) {
            return;
        }

        PedestalRecipe recipe = find(player);
        if (recipe == null) {
            return;
        }

        boolean valid = this.valid(recipe, player);
        valid |= this.consume(recipe);
        valid |= this.decatalyze(recipe, player);

        if (valid) {
            this.recipe = recipe;
            this.player = player;
            this.active = true;
            this.progress = 0;
            this.markDirty();
        }
    }

    public Map<PastelType, Integer> catalysts() {
        return this.catalysts;
    }

    public void add(PastelType type, int amount) {
        this.catalysts.put(type, this.catalysts.getOrDefault(type, 0) + amount);
        this.markDirty();
    }

    public boolean subtract(PastelType type, int amount) {
        int current = this.catalysts.getOrDefault(type, 0);
        if (current >= amount) {
            this.catalysts.put(type, current - amount);
            this.markDirty();
            return true;
        }

        return false;
    }

    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote && this.active && this.recipe != null) {
            this.progress++;

            if (this.progress >= this.recipe.time()) {
                this.complete();
                this.progress = 0;
                this.active = false;
                this.recipe = null;
                this.player = null;
            }

            this.markDirty();
        }
    }

    private PedestalRecipe find(EntityPlayerMP player) {
        ItemStack[] grid = new ItemStack[9];
        System.arraycopy(this.inventory, 0, grid, 0, 9);
        return PedestalRecipeManager.instance().find(this, grid, player);
    }

    private boolean valid(PedestalRecipe recipe, EntityPlayer player) {
        AtomicBoolean valid = new AtomicBoolean(true);
        Map<PastelType, Integer> catalysts = recipe.catalysts().apply(this, player);
        catalysts.forEach((type, amount) -> {
            int available = this.catalysts.getOrDefault(type, 0);
            if (available < amount) {
                valid.set(false);
            }
        });

        return valid.get();
    }

    private void complete() {
        if (this.recipe == null) return;
        ItemStack output = this.recipe.output().apply(this, this.player); EntityItem item = this.spawn(output);
        if (this.recipe.completion().apply(this, item)) this.worldObj.spawnEntityInWorld(item);
    }

    private boolean consume(PedestalRecipe recipe) {
        boolean valid = true;
        ItemStack[] old = new ItemStack[9];
        System.arraycopy(this.inventory, 0, old, 0, 9);
        List<ItemStack> input = recipe.input();
        for (int index = 0; index < input.size(); index++) {
            ItemStack required = input.get(index);
            ItemStack there = this.inventory[index];
            if (there != null && there.getItem() == required.getItem() && there.stackSize >= required.stackSize) {
                there.stackSize -= required.stackSize;

                if (there.stackSize <= 0) {
                    there = null;
                }

                this.inventory[index] = there;
            } else valid = false;
        }

        if (!valid) System.arraycopy(old, 0, this.inventory, 0, 0);
        return valid;
    }

    private boolean decatalyze(PedestalRecipe recipe, EntityPlayer player) {
        Map<PastelType, Integer> old = new HashMap<>(this.catalysts);
        AtomicBoolean valid = new AtomicBoolean(true);
        Map<PastelType, Integer> catalysts = recipe.catalysts().apply(this, player);
        catalysts.forEach((type, amount) -> {
            boolean available = this.subtract(type, amount);
            if (!available) valid.set(false);
        });

        if (!valid.get()) {
            this.catalysts.clear();
            this.catalysts.putAll(old);
            return false;
        }

        return true;
    }

    private EntityItem spawn(ItemStack stack) {
        if (stack == null || this.worldObj.isRemote) return null;

        EntityItem item = new EntityItem(
                this.worldObj,
                this.xCoord + 0.5D,
                this.yCoord + 1.5D,
                this.zCoord + 0.5D,
                stack
        );
        item.motionX = 0;item.motionY = 0.1D; item.motionZ = 0;
        return item;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.inventory.length) {
            return null;
        }
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.inventory[slot] != null) {
            ItemStack stack;

            if (this.inventory[slot].stackSize <= amount) {
                stack = this.inventory[slot];
                this.inventory[slot] = null;
            } else {
                stack = this.inventory[slot].splitStack(amount);

                if (this.inventory[slot].stackSize == 0) {
                    this.inventory[slot] = null;
                }

            }
            markDirty();
            return stack;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack = this.inventory[slot];
            this.inventory[slot] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.inventory.length) {
            return;
        }

        this.inventory[slot] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }

        markDirty();
    }



    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
        player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }


    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int[] slots = new int[this.inventory.length];
        for (int i = 0; i < this.inventory.length; i++) {
            slots[i] = i;
        }
        return slots;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.tier = PedestalTier.from(compound.getInteger("tier"));
        this.inventory = new ItemStack[this.tier.size()];
        if (compound.hasKey("name", 8)) { this.name = compound.getString("name"); }

        NBTTagList inventory = compound.getTagList("inventory", 10);
        for (int i = 0; i < inventory.tagCount(); i++) {
            NBTTagCompound itemTag = inventory.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;

            if (slot < this.inventory.length) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }


        if (compound.hasKey("catalysts", 10)) {
            try {
                NBTTagCompound catalysts = compound.getCompoundTag("catalysts");
                Class<?> clazz = catalysts.getClass();
                Field tags = clazz.getDeclaredField("tagMap");
                Map<String, NBTBase> map = (Map<String, NBTBase>) tags.get(catalysts);
                map.forEach((key, value) -> {
                    PastelType type = PastelType.valueOf(key.toUpperCase());
                    if (value instanceof NBTTagInt tag) {
                        int amount = tag.func_150287_d();
                        this.catalysts.put(type, amount);
                    }
                });
            } catch (ReflectiveOperationException ignored) {}
        }

        this.active = compound.getBoolean("active");
        this.progress = compound.getInteger("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("tier", this.tier.ordinal());
        compound.setBoolean("active", this.active);
        compound.setInteger("progress", this.progress);
        if (this.name != null && !this.name.isEmpty()) compound.setString("name", this.name);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        compound.setTag("inventory", list);
        NBTTagCompound catalysts = new NBTTagCompound();
        this.catalysts.forEach((type, amount) -> catalysts.setInteger(type.name(), amount));
        compound.setTag("catalysts", catalysts);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }

    @Override public void openInventory() {}
    @Override public void closeInventory() {}
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public int getSizeInventory() { return this.inventory.length; }
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return true; }
    @Override public boolean canExtractItem(int slot, ItemStack stack, int side) { return true; }
    @Override public boolean hasCustomInventoryName() { return this.name != null && !this.name.isEmpty(); }
    @Override public boolean canInsertItem(int slot, ItemStack stack, int side) { return isItemValidForSlot(slot, stack); }
    @Override public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) { readFromNBT(packet.func_148857_g()); }
    @Override public String getInventoryName() { return hasCustomInventoryName() ? this.name : "container.pedestal_" + this.tier.name().toLowerCase(); }
}