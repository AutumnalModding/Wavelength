package xyz.lilyflower.wavelength.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xyz.lilyflower.wavelength.inventory.ContainerPedestal;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.lilyflower.wavelength.util.PedestalRecipe;
import xyz.lilyflower.wavelength.util.recipe.PedestalRecipeManager;

public class GuiPedestal extends GuiContainer {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation("wavelength:textures/gui/container/pedestal_tier1.png"),
            new ResourceLocation("wavelength:textures/gui/container/pedestal_tier2.png"),
            new ResourceLocation("wavelength:textures/gui/container/pedestal_tier3.png"),
            new ResourceLocation("wavelength:textures/gui/container/pedestal_tier4.png")
    };

    private final TileEntityPedestal entity;

    public GuiPedestal(InventoryPlayer inventory, TileEntityPedestal entity) {
        super(new ContainerPedestal(inventory, entity));
        this.entity = entity;

        this.xSize = 176;
        this.ySize = 222;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = entity.hasCustomInventoryName() ?
                entity.getInventoryName() :
                I18n.format(entity.getInventoryName());

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack[] grid = new ItemStack[9];
        System.arraycopy(this.entity.inventory, 0, grid, 0, 9);
        PedestalRecipe recipe = PedestalRecipeManager.instance().find(this.entity, grid, player);
        if (recipe != null) {
            ItemStack output = recipe.output().apply(this.entity, player);
            float level = itemRender.zLevel;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRender.zLevel = 100.0F;
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), output, 127, 64);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), output, 127, 64, null);
            itemRender.zLevel = level;
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        this.fontRendererObj.drawString(name, 8, 34, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, 128, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        ResourceLocation texture = TEXTURES[entity.tier.ordinal()];
        this.mc.getTextureManager().bindTexture(texture);

        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;

        this.drawTexturedModalRect(xPos, yPos + 27, 0, 0, xSize, ySize);
    }
}