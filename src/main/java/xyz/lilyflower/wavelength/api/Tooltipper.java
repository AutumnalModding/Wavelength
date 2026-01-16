package xyz.lilyflower.wavelength.api;

import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;

public class Tooltipper implements Function<EntityPlayer, List<String>> {
    private final Function<EntityPlayer, List<String>> delegate;

    public Tooltipper(Function<EntityPlayer, List<String>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<String> apply(EntityPlayer player) {
        return this.delegate.apply(player);
    }
}
