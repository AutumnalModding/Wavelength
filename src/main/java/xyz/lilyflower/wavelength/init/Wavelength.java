package xyz.lilyflower.wavelength.init;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.lilyflower.wavelength.event.WavelengthPlayerEventHandler;
import xyz.lilyflower.wavelength.registry.WavelengthItemRegistry;
import xyz.lilyflower.wavelength.util.WavelengthTab;
import xyz.lilyflower.wavelength.block.entity.TileEntityPedestal;
import xyz.lilyflower.wavelength.entity.EntityGravityBlock;
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

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        loader = this;
    }

    @Mod.EventHandler
    public void pre(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(WavelengthWorldgen.INSTANCE, 0);
        GameRegistry.registerTileEntity(TileEntityPedestal.class, "wavelength:pedestal");

        EventBus bus = FMLCommonHandler.instance().bus();
        bus.register(new WavelengthPlayerEventHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        loader.registerRenderers();
        NetworkRegistry.INSTANCE.registerGuiHandler(loader, new GuiHandler());

        // TODO move this
        PedestalRecipeManager.instance().register(new PedestalRecipe(
                new ChainedArrayList<ItemStack>()
                        .chain(new ItemStack(Items.apple))
                        .chain(new ItemStack(Item.getItemFromBlock(Blocks.gold_block))),
                (pedestal, player) -> new ItemStack(Items.golden_apple),
                (pedestal, player) -> new HashMap<>(),
                60,
                player -> true,
                0,
                (pedestal, stack) -> true
            )
        );

        PedestalRecipeManager.instance().register(PedestalRecipe.BasicT4(
                new ItemStack(Items.blaze_powder, 4),
                0,
                0,
                0,
                0,
                0,
                40,
                PedestalRecipe.RequiresAchievement(AchievementList.blazeRod),
                new ItemStack(Items.blaze_rod)));

        EntityRegistry.registerModEntity(
                EntityGravityBlock.class,
                "gravity_block",
                0,
                loader,
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
