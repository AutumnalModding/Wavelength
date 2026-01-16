package xyz.lilyflower.wavelength.client.gui;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xyz.lilyflower.wavelength.container.ContainerPedestal;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.lilyflower.wavelength.util.PastelType;
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

        FontRenderer font = this.fontRendererObj;
        TextureManager manager = this.mc.getTextureManager();

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack[] grid = new ItemStack[9];
        System.arraycopy(this.entity.inventory, 0, grid, 0, 9);
        PedestalRecipe recipe = PedestalRecipeManager.instance().find(this.entity, grid, player);
        if (recipe != null) {
            ItemStack output = recipe.output().apply(this.entity, player);
            float level = itemRender.zLevel;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRender.zLevel = 100.0F;
            itemRender.renderItemAndEffectIntoGUI(font, manager, output, 127, 64);
            itemRender.renderItemOverlayIntoGUI(font, manager, output, 127, 64, null);
            itemRender.zLevel = level;
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        int width = font.getStringWidth(name);
        int center = (176 - width) / 2;
        font.drawString(name, center, 32, 4210752);

        StringBuilder pigment = new StringBuilder();
        Map<PastelType, Integer> catalysts = this.entity.catalysts();
        pigment.append(String.format("%04d", catalysts.get(PastelType.AMETHYST))).append(" | ");
        pigment.append(String.format("%04d", catalysts.get(PastelType.CITRINE))).append(" | ");
        pigment.append(String.format("%04d", catalysts.get(PastelType.TOPAZ)));
        if (this.entity.tier.ordinal() >= 4) pigment.append(" | ").append(String.format("%04d", catalysts.get(PastelType.ONYX)));
        if (this.entity.tier.ordinal() == 5) pigment.append(" | ").append(String.format("%04d", catalysts.get(PastelType.MOONSTONE)));
        String values = pigment.toString();
        width = font.getStringWidth(values);
        center = (176 - width) / 2;
        font.drawString(values, center, 124, 4210752);
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