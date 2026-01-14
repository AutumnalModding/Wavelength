package xyz.lilyflower.wavelength.util;

import com.github.bsideup.jabel.Desugar;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;

@Desugar
public record PedestalRecipe(
        List<ItemStack> input,
        ItemStack output,
        Map<PastelType, Integer> catalysts,
        int time,
        Function<EntityPlayer, Boolean> unlocked,
        int tier,
        Consumer<TileEntityPedestal> completion
) {

}
