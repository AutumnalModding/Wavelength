package xyz.lilyflower.wavelength.include;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

public final class BlockMeta2ObjectOpenHashMap<V> extends ObjMeta2ObjectOpenHashMap<BlockMetaPair, Block, V> {
    /// @param wildcardFallback
    /// If searching for a metadata value that's not found, should we return the entry at {@link OreDictionary#WILDCARD_VALUE} if there is one?
    public BlockMeta2ObjectOpenHashMap(boolean wildcardFallback) {
        super(wildcardFallback);
    }
}