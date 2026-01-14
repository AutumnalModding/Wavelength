package xyz.lilyflower.wavelength.content.block.entity;

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
        BASIC(13),
        UPGRADED(13),
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
                return BASIC;
            }
            return values()[ordinal];
        }
    }

    private static final int[] positions = new int[]{
            0, // first slot
            9, // grid size
            9, // tablet slot
            10 // powders start
    };

    private ItemStack[] inventory;
    private PedestalTier tier;
    private String name;

    private final Map<PastelType, Integer> catalysts;

    private boolean active;
    private int progress;
    private PedestalRecipe recipe;
    private EntityPlayerMP player;

    @SuppressWarnings("unused")
    public TileEntityPedestal() {
        this(PedestalTier.BASIC);
    }

    public TileEntityPedestal(PedestalTier tier) {
        this.tier = tier;
        this.inventory = new ItemStack[tier.size()];
        this.catalysts = new HashMap<>();
        this.initialize();
        this.active = false;
        this.progress = 0;
    }

    private void initialize() {
        for (PastelType type : PastelType.values()) {
            this.catalysts.put(type, 0);
        }
    }

    public PedestalRecipe recipe() { return this.recipe; }

    public PedestalTier tier() {
        return this.tier;
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
        if (recipe == null || !valid(recipe)) {
            return;
        }

        this.recipe = recipe;
        this.player = player;
        this.active = true;
        this.progress = 0;
        markDirty();
    }

    public boolean active() {
        return this.active;
    }

    public int progress() {
        return this.progress;
    }

    public int time() {
        return this.recipe != null ? this.recipe.time() : 0;
    }

    public Map<PastelType, Integer> catalysts() {
        return new HashMap<>(this.catalysts);
    }

    public void add(PastelType type, int amount) {
        this.catalysts.put(type, this.catalysts.getOrDefault(type, 0) + amount);
        markDirty();
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
        ItemStack[] grid = new ItemStack[positions[1]];
        System.arraycopy(inventory, positions[0], grid, 0, positions[1]);

        return PedestalRecipeManager.instance().find(this, grid, player);
    }

    private boolean valid(PedestalRecipe recipe) {
        for (Map.Entry<PastelType, Integer> required : recipe.catalysts().entrySet()) {
            int available = this.catalysts.getOrDefault(required.getKey(), 0);
            if (available < required.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void complete() {
        if (this.recipe == null) {
            return;
        }

        this.consume();
        this.consume(recipe);

        if (this.inventory[positions[2]] != null) {
            if (this.inventory[positions[2]].isItemStackDamageable()) {
                this.inventory[positions[2]].setItemDamage(this.inventory[positions[2]].getItemDamage() + 1);

                if (this.inventory[positions[2]].getItemDamage() >= this.inventory[positions[2]].getMaxDamage()) {
                    this.inventory[positions[2]] = null;
                }
            }
        }

        output(this.recipe.output().copy());
    }

    private void consume() {
        for (int index = 0; index < this.recipe.input().size(); index++) {
            ItemStack required = this.recipe.input().get(index);
            if (required != null && this.inventory[positions[0] + index] != null) {
                this.inventory[positions[0] + index].stackSize -= required.stackSize;

                if (this.inventory[positions[0] + index].stackSize <= 0) {
                    this.inventory[positions[0] + index] = null;
                }
            }
        }
    }

    private void consume(PedestalRecipe recipe) {
        for (Map.Entry<PastelType, Integer> entry : recipe.catalysts().entrySet()) {
            subtract(entry.getKey(), entry.getValue());
        }
    }

    private void output(ItemStack stack) {
        if (stack == null || this.worldObj.isRemote) {
            return;
        }

        double spawnX = this.xCoord + 0.5D;
        double spawnY = this.yCoord + 1.5D;
        double spawnZ = this.zCoord + 0.5D;

        net.minecraft.entity.item.EntityItem item =
                new net.minecraft.entity.item.EntityItem(this.worldObj, spawnX, spawnY, spawnZ, stack);

        item.motionX = 0;
        item.motionY = 0.1D;
        item.motionZ = 0;

        this.worldObj.spawnEntityInWorld(item);
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
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
    public String getInventoryName() {
        return hasCustomInventoryName() ? this.name : "container.pedestal_" + this.tier.name().toLowerCase();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return this.name != null && !this.name.isEmpty();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
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
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        int tier = compound.getInteger("Tier");
        this.tier = PedestalTier.from(tier);

        this.inventory = new ItemStack[this.tier.size()];

        NBTTagList tagList = compound.getTagList("Items", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;

            if (slot < this.inventory.length) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }

        if (compound.hasKey("CustomName", 8)) {
            this.name = compound.getString("CustomName");
        }

        initialize();
        if (compound.hasKey("Catalysts", 10)) {
            NBTTagCompound catalysts = compound.getCompoundTag("Catalysts");
            for (PastelType type : PastelType.values()) {
                if (catalysts.hasKey(type.name())) {
                    this.catalysts.put(type, catalysts.getInteger(type.name()));
                }
            }
        }

        this.active = compound.getBoolean("IsCrafting");
        this.progress = compound.getInteger("CraftingProgress");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("Tier", this.tier.ordinal());

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        compound.setTag("Items", list);

        if (hasCustomInventoryName()) {
            compound.setString("CustomName", this.name);
        }

        NBTTagCompound catalysts = new NBTTagCompound();
        for (Map.Entry<PastelType, Integer> entry : this.catalysts.entrySet()) {
            catalysts.setInteger(entry.getKey().name(), entry.getValue());
        }
        compound.setTag("Catalysts", catalysts);

        compound.setBoolean("IsCrafting", this.active);
        compound.setInteger("CraftingProgress", this.progress);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }
}