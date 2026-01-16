package xyz.lilyflower.wavelength.content.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import xyz.lilyflower.wavelength.api.Tooltipper;

public class ItemBlockTooltippable extends ItemBlock {
    private final Tooltipper tooltipper;

    public ItemBlockTooltippable(Block block, Tooltipper tooltipper) {
        super(block);
        this.tooltipper = tooltipper;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"}) // "advanced" is F3+H I think? if that even exists
    public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean advanced) {
        super.addInformation(stack, player, text, advanced);
        text.addAll(this.tooltipper.apply(player));
    }
}
