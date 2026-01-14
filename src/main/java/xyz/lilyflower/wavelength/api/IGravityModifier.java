package xyz.lilyflower.wavelength.api;

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

    static void populate(
            ItemStack stack,
            List<SolarisExtensions.Pair<EnumFacing, Float>> increase,
            List<SolarisExtensions.Pair<EnumFacing, Float>> subtract,
            List<SolarisExtensions.Pair<EnumFacing, Float>> multiply,
            List<SolarisExtensions.Pair<EnumFacing, Float>> division
    ) {
        IGravityModifier gravity = null;
        Item item = stack.getItem();
        if (item instanceof IGravityModifier gravmod) gravity = gravmod;
        else if (
            item instanceof ItemBlock block &&
            Block.getBlockFromItem(block) instanceof IGravityModifier gravmod
        ) gravity = gravmod;
        if (gravity != null) gravity.modifiers().forEach(modifier -> {
            SolarisExtensions.Pair<EnumFacing, Float> entry = new SolarisExtensions.Pair<>(modifier.middle(), modifier.right() * stack.stackSize);
            switch (modifier.left()) {
                case ADD -> increase.add(entry);
                case SUBTRACT -> subtract.add(entry);
                case MULTIPLY -> multiply.add(entry);
                case DIVIDE -> division.add(entry);
            }
        });
    }
}
