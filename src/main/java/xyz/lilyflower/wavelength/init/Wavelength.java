package xyz.lilyflower.wavelength.init;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.wavelength.content.WavelengthItemRegistry;
import xyz.lilyflower.wavelength.content.WavelengthTab;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import xyz.lilyflower.wavelength.content.entity.EntityGravityBlock;
import xyz.lilyflower.wavelength.handler.GuiHandler;
import xyz.lilyflower.wavelength.util.ChainedArrayList;
import xyz.lilyflower.wavelength.util.PedestalRecipe;
import xyz.lilyflower.wavelength.util.recipe.PedestalRecipeManager;

@Mod(modid = "wavelength", version = "1.0", dependencies = "after:solaris", name = "Wavelength", acceptedMinecraftVersions = "1.7.10")
public class Wavelength {
    public static final Logger LOGGER = LogManager.getLogger("Wavelength");

    @SidedProxy(
            clientSide = "xyz.lilyflower.wavelength.init.WavelengthClient",
            serverSide = "xyz.lilyflower.wavelength.init.Wavelength"
    )
    public static Wavelength loader;
    public static Wavelength INSTANCE;

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        INSTANCE = this;
    }

    @Mod.EventHandler
    public void pre(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(WavelengthWorldgen.INSTANCE, 0);
        GameRegistry.registerTileEntity(TileEntityPedestal.class, "wavelength:pedestal");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        loader.registerRenderers();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());

        PedestalRecipeManager.instance().register(new PedestalRecipe
            (
                new ChainedArrayList<ItemStack>()
                        .chainedAdd(new ItemStack(Items.apple))
                        .chainedAdd(new ItemStack(Item.getItemFromBlock(Blocks.gold_block))),
                new ItemStack(Items.golden_apple),
                new HashMap<>(),
                60,
                player -> true,
                0,
                pedestal -> {}
            )
        );

        EntityRegistry.registerModEntity(
                EntityGravityBlock.class,
                "gravity_block",
                0,
                INSTANCE,
                64,
                20,
                true
        );
    }

    @Mod.EventHandler
    public void post(FMLPostInitializationEvent event) {
        WavelengthTab.BLOCKS.setIcon(
                Item.getItemFromBlock(
                        (Block) Block.blockRegistry.getObject("wavelength:pedestal_all")
                )
        );

        WavelengthTab.RESOURCES.setIcon(WavelengthItemRegistry.SHARD_AMETHYST);
    }

    @Mod.EventHandler
    public void completion(FMLLoadCompleteEvent event) {
        WavelengthWorldgen.INSTANCE.geodes();
    }

    public void registerRenderers() {}
}
