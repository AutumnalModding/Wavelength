package xyz.lilyflower.wavelength.util;

import com.github.bsideup.jabel.Desugar;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;

@Desugar
public record PedestalRecipe(
        List<ItemStack> input,
        BiFunction<TileEntityPedestal, EntityPlayer, ItemStack> output,
        BiFunction<TileEntityPedestal, EntityPlayer, Map<PastelType, Integer>> catalysts,
        int time,
        Function<EntityPlayer, Boolean> unlocked,
        int tier,
        BiFunction<TileEntityPedestal, EntityItem, Boolean> completion
) {
    private static PedestalRecipe __basic(
            ItemStack output,
            int amethyst,
            int citrine,
            int topaz,
            int onyx,
            int moonstone,
            int time,
            int tier,
            Function<EntityPlayer, Boolean> available,
            ItemStack... input
    ) {
        return new PedestalRecipe(Arrays.asList(input), (pedestal, player) -> output, (pedestal, player) -> {
            Map<PastelType, Integer> map = new HashMap<>();
            map.put(PastelType.ONYX, onyx);
            map.put(PastelType.TOPAZ, topaz);
            map.put(PastelType.CITRINE, citrine);
            map.put(PastelType.AMETHYST, amethyst);
            map.put(PastelType.MOONSTONE, moonstone);
            return map;
        }, time, available, tier, (pedestal, item) -> true);
    }

    public static PedestalRecipe BasicT1(ItemStack output, int amethyst, int citrine, int topaz, int time, Function<EntityPlayer, Boolean> available, ItemStack... input) {
        return __basic(output, amethyst, citrine, topaz, 0, 0, time, 0, available, input);
    }

    public static PedestalRecipe BasicT2(ItemStack output, int amethyst, int citrine, int topaz, int onyx, int time, Function<EntityPlayer, Boolean> available, ItemStack... input) {
        return __basic(output, amethyst, citrine, topaz, onyx, 0, time, 3, available, input);
    }

    public static PedestalRecipe BasicT3(ItemStack output, int amethyst, int citrine, int topaz, int onyx, int time, Function<EntityPlayer, Boolean> available, ItemStack... input) {
        return __basic(output, amethyst, citrine, topaz, onyx, 0, time, 4, available, input);
    }

    public static PedestalRecipe BasicT4(ItemStack output, int amethyst, int citrine, int topaz, int onyx, int moonstone, int time, Function<EntityPlayer, Boolean> available, ItemStack... input) {
        return __basic(output, amethyst, citrine, topaz, onyx, moonstone, time, 5, available, input);
    }

    public static Function<EntityPlayer, Boolean> RequiresAchievement(Achievement achievement) {
        return player -> MiscUtils.HasAchievement(player, achievement);
    }
}
