package xyz.lilyflower.wavelength.util.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import xyz.lilyflower.wavelength.util.PedestalRecipe;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class PedestalRecipeManager {
    
    private static final PedestalRecipeManager INSTANCE = new PedestalRecipeManager();
    
    private final List<PedestalRecipe> recipes = new ArrayList<>();
    private final Map<RecipeKey, List<PedestalRecipe>> cache = new HashMap<>();
    
    private PedestalRecipeManager() {}
    
    public static PedestalRecipeManager instance() {
        return INSTANCE;
    }
    
    public void register(PedestalRecipe recipe) {
        recipes.add(recipe);
        this.cache.clear();
    }
    
    public void clear() {
        this.recipes.clear();
        this.cache.clear();
    }
    
    public PedestalRecipe find(TileEntityPedestal pedestal, ItemStack[] grid, EntityPlayer player) {
        if (grid.length != 9) {
            return null;
        }
        
        RecipeKey key = createKey(grid);
        List<PedestalRecipe> cached = this.cache.get(key);
        
        if (cached == null) {
            cached = find(grid);
            this.cache.put(key, cached);
        }
        
        for (PedestalRecipe recipe : cached) {
            if (recipe.unlocked().apply(player) && pedestal.tier.ordinal() >= recipe.tier()) {
                return recipe;
            }
        }
        
        return null;
    }
    
    private List<PedestalRecipe> find(ItemStack[] grid) {
        List<PedestalRecipe> matching = new ArrayList<>();
        
        for (PedestalRecipe recipe : this.recipes) {
            if (matchesRecipe(grid, recipe)) {
                matching.add(recipe);
            }
        }
        
        return matching;
    }
    
    private boolean matchesRecipe(ItemStack[] grid, PedestalRecipe recipe) {
        List<ItemStack> input = recipe.input();

        boolean matches = true;
        for (int index = 0; index < input.size(); index++) if (!matches(grid[index], input.get(index))) matches = false;
        return matches;
    }
    
    private boolean matches(ItemStack actual, ItemStack required) {
        if (required == null) {
            return actual == null;
        }
        
        if (actual == null) {
            return false;
        }
        
        if (actual.getItem() != required.getItem()) {
            return false;
        }
        
        if (required.getItemDamage() != 32767 && actual.getItemDamage() != required.getItemDamage()) {
            return false;
        }

        return actual.stackSize >= required.stackSize;
    }
    
    private RecipeKey createKey(ItemStack[] grid) {
        int[] items = new int[9];
        int[] metadata = new int[9];
        
        for (int i = 0; i < 9; i++) {
            if (grid[i] != null) {
                items[i] = Item.getIdFromItem(grid[i].getItem());
                metadata[i] = grid[i].getItemDamage();
            } else {
                items[i] = -1;
                metadata[i] = -1;
            }
        }
        
        return new RecipeKey(items, metadata);
    }
    
    private static class RecipeKey {
        private final int[] items;
        private final int[] metadata;
        private final int hash;
        
        RecipeKey(int[] items, int[] metadata) {
            this.items = items;
            this.metadata = metadata;
            this.hash = Arrays.hashCode(items) * 31 + Arrays.hashCode(metadata);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj instanceof RecipeKey other) {
                return Arrays.equals(this.items, other.items) && Arrays.equals(metadata, other.metadata);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return hash;
        }
    }
}