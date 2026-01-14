package xyz.lilyflower.wavelength.util;

import com.github.bsideup.jabel.Desugar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

@Desugar
public record PedestalRecipe(List<ItemStack> input, ItemStack output, Map<PastelType, Integer> catalysts, int time, Function<EntityPlayerMP, Boolean> unlocked, int tier) { }
