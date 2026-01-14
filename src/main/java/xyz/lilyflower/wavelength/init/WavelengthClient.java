package xyz.lilyflower.wavelength.init;

import cpw.mods.fml.client.registry.RenderingRegistry;
import xyz.lilyflower.wavelength.client.RenderBlockCluster;
import xyz.lilyflower.wavelength.client.RenderGravityBlock;
import xyz.lilyflower.wavelength.content.entity.EntityGravityBlock;

@SuppressWarnings("unused")
public class WavelengthClient extends Wavelength {
    @Override
    public void registerRenderers() {
        RenderingRegistry.registerBlockHandler(new RenderBlockCluster());
        RenderingRegistry.registerEntityRenderingHandler(
                EntityGravityBlock.class,
                new RenderGravityBlock()
        );
    }
}