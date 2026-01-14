package xyz.lilyflower.wavelength.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import xyz.lilyflower.wavelength.content.entity.EntityGravityBlock;

public class RenderGravityBlock extends Render {
    private final RenderBlocks render = new RenderBlocks();

    public RenderGravityBlock() {
        this.shadowSize = 0.5F;
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public void doRender(Entity entity, double posX, double posY, double posZ, float p_76986_8_, float p_76986_9_) {
        if (entity instanceof EntityGravityBlock gravity) {
            World world = gravity.worldObj;
            Block block = gravity.block;
            int x = MathHelper.floor_double(gravity.posX);
            int y = MathHelper.floor_double(gravity.posY);
            int z = MathHelper.floor_double(gravity.posZ);

            if (block != null && block != world.getBlock(x, y, z)) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) posX, (float) posY, (float) posZ);
                this.bindEntityTexture(gravity);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.render.setRenderBoundsFromBlock(block);
                this.render.renderBlockSandFalling(block, world, x, y, z, gravity.metadata);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
            }
        }
    }
}
