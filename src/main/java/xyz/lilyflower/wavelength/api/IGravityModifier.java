package xyz.lilyflower.wavelength.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import xyz.lilyflower.solaris.util.SolarisExtensions;

public interface IGravityModifier {
    List<SolarisExtensions.TriPair<Action, EnumFacing, Float>> modifiers();
    default boolean valid(ContainerType type) { return true; }

    enum ContainerType {
        HELD,
        ARMOUR,
        BAUBLES,
        INVENTORY
    }

    enum Action {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }
    
    static void apply(Map<EnumFacing, Float> result, Entity target) {
        result.forEach((direction, amount) -> {
            switch (direction) {
                case UP -> target.motionY += amount;
                case DOWN -> target.motionY -= amount;
                case NORTH -> target.motionZ += amount;
                case SOUTH -> target.motionZ -= amount;
                case EAST -> target.motionX += amount;
                case WEST -> target.motionX -= amount;
            }
        });
    }

    static void process(Map<EnumFacing, Float> result, List<SolarisExtensions.Pair<EnumFacing, Float>> modifiers) {
        for (SolarisExtensions.Pair<EnumFacing, Float> pair : modifiers) {
            float current = result.getOrDefault(pair.left(), 0.0F);
            current += pair.right();
            result.put(pair.left(), current);
        }
    }

    @SuppressWarnings("unchecked")
    static void populate(ItemStack[] stacks, ContainerType type, List<SolarisExtensions.Pair<EnumFacing, Float>>[] modifiers) {
        if (modifiers.length < 4) {
            List<SolarisExtensions.Pair<EnumFacing, Float>>[] old = modifiers.clone();
            modifiers = new List[]{
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            };
            System.arraycopy(old, 0, modifiers, 0, old.length);
        }

        for (int index = 0; index < modifiers.length; index++) {
            if (modifiers[index] == null) modifiers[index] = new ArrayList<>();
        }

        for (ItemStack stack : stacks) {
            if (stack != null) {
                IGravityModifier gravity = null;
                Item item = stack.getItem();
                if (item instanceof IGravityModifier gravmod) gravity = gravmod;
                else if (
                        item instanceof ItemBlock block &&
                                Block.getBlockFromItem(block) instanceof IGravityModifier gravmod
                ) gravity = gravmod;
                if (gravity != null && gravity.valid(type)) {
                    for (SolarisExtensions.TriPair<Action, EnumFacing, Float> modifier : gravity.modifiers()) {
                        SolarisExtensions.Pair<EnumFacing, Float> entry = new SolarisExtensions.Pair<>(modifier.middle(), modifier.right() * stack.stackSize);
                        switch (modifier.left()) {
                            case ADD -> modifiers[0].add(entry);
                            case SUBTRACT -> modifiers[1].add(entry);
                            case MULTIPLY -> modifiers[2].add(entry);
                            case DIVIDE -> modifiers[3].add(entry);
                        }
                    }
                }
            }
        }
    }
}
