package xyz.lilyflower.wavelength.inventory;

import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPedestal extends Container {
    private static final int[] positions = new int[]{
            9, // grid size
            9, // output slot ID
            10, // inventory start
            30, // grid X pos
            46, // grid Y pos
            93, // output X pos 
            46, // output Y pos
            104, // pastel catalysts Y pos
            139, // invent Y pos
            197, // hotbar Y pos
            18, // slot size
    };

    private final TileEntityPedestal entity;

    public ContainerPedestal(InventoryPlayer inventory, TileEntityPedestal entity) {
        this.entity = entity;
        entity.openInventory();

        int count = entity.getSizeInventory() - positions[0] - 1;

        this.addCraftingGridSlots();
        this.addOutputSlot();
        this.addPedestalInventorySlots(count);
        this.addPlayerInventorySlots(inventory);
    }

    private void addCraftingGridSlots() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new Slot(entity, col + row * 3, positions[3] + col * positions[10], positions[4] + row * positions[10]));
            }
        }
    }

    private void addOutputSlot() {
        this.addSlotToContainer(new Slot(entity, positions[1], positions[5], positions[6]));
    }

    private void addPedestalInventorySlots(int count) {
        int startX = 89 - (count * positions[10] / 2);
        for (int i = 0; i < count; i++) {
            this.addSlotToContainer(new Slot(entity, positions[2] + i, startX + i * positions[10], positions[7]));
        }
    }

    private void addPlayerInventorySlots(InventoryPlayer inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(inventory, col + row * 9 + 9, 8 + col * positions[10], positions[8] + row * positions[10]));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(inventory, col, 8 + col * positions[10], positions[9]));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.entity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack original = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            original = stack.copy();

            int count = this.entity.getSizeInventory();

            if (index < count) {
                if (!mergeItemStack(stack, count, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else {
                if (!mergeItemStack(stack, 0, positions[0], false)) {
                    if (!mergeItemStack(stack, positions[2], count, false)) {
                        return null;
                    }
                }
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stack.stackSize == original.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stack);
        }

        return original;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        entity.closeInventory();
    }
}