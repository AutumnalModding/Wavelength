package xyz.lilyflower.wavelength.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderBlockCluster implements ISimpleBlockRenderingHandler {

    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        IIcon icon = block.getIcon(0, 0);

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        draw(tessellator, icon, 0.5, 0.5, 0.5, 1);
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int model, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        int dir = meta & 7;

        // noone will get this joke lmao
        Tessellator katayama = Tessellator.instance;
        katayama.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        katayama.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        draw(katayama, block.getIcon(0, meta), x + 0.5, y + 0.5, z + 0.5, dir);
        return true;
    }

    private void draw(Tessellator tesselator, IIcon icon, double x, double y, double z, int dir) {
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        double radius = 0.45;
        double height = 0.85;
        double offset = radius * 0.7071;

        double[][] vertices = new double[8][3];

        vertices[0] = new double[]{-offset, -0.5, offset};
        vertices[1] = new double[]{offset, -0.5, -offset};
        vertices[2] = new double[]{offset, -0.5, offset};
        vertices[3] = new double[]{-offset, -0.5, -offset};

        vertices[4] = new double[]{-offset, height-0.5, offset};
        vertices[5] = new double[]{offset, height-0.5, -offset};
        vertices[6] = new double[]{offset, height-0.5, offset};
        vertices[7] = new double[]{-offset, height-0.5, -offset};

        for (int i = 0; i < 8; i++) {
            transform(vertices[i], dir);
        }

        draw(tesselator, x, y, z, vertices[0], vertices[1], vertices[5], vertices[4], minU, maxU, maxV, minV);
        draw(tesselator, x, y, z, vertices[1], vertices[0], vertices[4], vertices[5], minU, maxU, maxV, minV);

        draw(tesselator, x, y, z, vertices[2], vertices[3], vertices[7], vertices[6], minU, maxU, maxV, minV);
        draw(tesselator, x, y, z, vertices[3], vertices[2], vertices[6], vertices[7], minU, maxU, maxV, minV);
    }

    private void draw(Tessellator tesselator, double x, double y, double z,
                      double[] first, double[] second, double[] third, double[] fourth,
                      double minimum, double maximum, double base, double tip) {
        tesselator.addVertexWithUV(x + first[0], y + first[1], z + first[2], minimum, base);
        tesselator.addVertexWithUV(x + second[0], y + second[1], z + second[2], maximum, base);
        tesselator.addVertexWithUV(x + third[0], y + third[1], z + third[2], maximum, tip);
        tesselator.addVertexWithUV(x + fourth[0], y + fourth[1], z + fourth[2], minimum, tip);
    }

    private void transform(double[] positions, int direction) {
        double x = positions[0];
        double y = positions[1];
        double z = positions[2];

        switch (direction) {
            case 0:
                positions[0] = x; positions[1] = -y; positions[2] = z;
                break;
            case 1:
                break;
            case 2:
                positions[0] = x; positions[1] = z; positions[2] = -y;
                break;
            case 3:
                positions[0] = x; positions[1] = -z; positions[2] = y;
                break;
            case 4:
                positions[0] = -y; positions[1] = x; positions[2] = z;
                break;
            case 5:
                positions[0] = y; positions[1] = x; positions[2] = z;
                break;
        }
    }

    @Override public boolean shouldRender3DInInventory(int model) { return true; }
    @Override public int getRenderId() { return RENDER_ID; }
}