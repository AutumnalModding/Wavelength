package xyz.lilyflower.wavelength.content;

import cpw.mods.fml.common.registry.GameRegistry;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import xyz.lilyflower.solaris.api.ContentRegistry;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.content.block.BlockPedestal;
import xyz.lilyflower.wavelength.content.block.entity.TileEntityPedestal;
import xyz.lilyflower.wavelength.content.block.gem.BlockAmethyst;
import xyz.lilyflower.wavelength.content.block.gem.BlockBudding;
import xyz.lilyflower.wavelength.content.block.gem.BlockCluster;
import xyz.lilyflower.wavelength.content.block.generic.BlockGeneric;
import xyz.lilyflower.wavelength.content.block.generic.BlockGravity;
import xyz.lilyflower.wavelength.content.block.generic.BlockSided;
import xyz.lilyflower.wavelength.content.block.generic.BlockGenericOre;
import xyz.lilyflower.wavelength.content.block.generic.BlockGenericPlank;

@SuppressWarnings("unused")
public class WavelengthBlockRegistry implements ContentRegistry<Block> {
    private static final Class<?>[] PEDESTAL = new Class<?>[]{TileEntityPedestal.PedestalTier.class};

    private static final String[] woods = new String[]{
            "weeping_gala",
            "black",
            "blue",
            "brown",
            "cyan",
            "green",
            "light_blue",
            "gray",
            "lime",
            "magenta",
            "orange",
            "pink",
            "purple",
            "red",
            "white",
            "yellow",
    };

    private static final String[] gems = new String[]{
            "amethyst",
            "topaz",
            "citrine",
            "onyx",
            "moonstone",
    };

    private static final String[] ores = new String[]{
            "malachite",
            "azurite",
            "paltaeria",
            "stratine",
    };

    static final ArrayList<SolarisExtensions.Pair<Block, String>> BLOCKS = new ArrayList<>();

    @Override
    public ArrayList<SolarisExtensions.Pair<Block, String>> contents() {
        return BLOCKS;
    }

    // Misc blocks
    public static final Block BASALT;
    public static final Block BASALT_POLISHED;

    public static final Block CALCITE;
    public static final Block CALCITE_POLISHED;

    @Override
    public void register(SolarisExtensions.Pair<Block, String> pair) {
        Block block = pair.left();
        String unlocalized = "wavelength." + pair.right();
        String texture = "wavelength:" + pair.right();

        block.setBlockName(unlocalized);
        block.setBlockTextureName(texture);
        GameRegistry.registerBlock(block, "$APPLYPREFIX$wavelength:" + pair.right());
    }

    @Override
    public boolean valid(String key) {
        return true;
    }

    @Override
    public boolean runnable() {
        return Solaris.STATE == LoadStage.PRELOADER;
    }

    static {
        ContentRegistry.create("pedestal_amethyst", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.BASIC);
        ContentRegistry.create("pedestal_citrine", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.BASIC);
        ContentRegistry.create("pedestal_topaz", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.BASIC);
        ContentRegistry.create("pedestal_all", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.UPGRADED);
        ContentRegistry.create("pedestal_onyx", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.ONYX);
        ContentRegistry.create("pedestal_moonstone", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.MOONSTONE);

        CALCITE_POLISHED = ContentRegistry.create("calcite_polished", BlockGeneric.class, new Class<?>[]{Material.class}, BLOCKS, Material.rock);
        BASALT_POLISHED = ContentRegistry.create("basalt_polished", BlockGeneric.class, new Class<?>[]{Material.class}, BLOCKS, Material.rock);
        ContentRegistry.create("floatblock_stratine", BlockGravity.class, new Class<?>[]{Material.class, EnumFacing.class, boolean.class}, BLOCKS, Material.rock, EnumFacing.DOWN, false);
        ContentRegistry.create("floatblock_paltaeria", BlockGravity.class, new Class<?>[]{Material.class, EnumFacing.class, boolean.class}, BLOCKS, Material.rock, EnumFacing.UP, false);
        for (String gem : gems) ContentRegistry.create(gem + "_polished", BlockGeneric.class, new Class<?>[]{Material.class}, BLOCKS, Material.rock);
        for (String gem : gems) ContentRegistry.create(gem + "_block", BlockAmethyst.class, new Class<?>[]{}, BLOCKS);
        CALCITE = ContentRegistry.create("calcite", BlockGeneric.class, new Class<?>[]{Material.class}, BLOCKS, Material.rock);
        BASALT = ContentRegistry.create("basalt", BlockSided.class, new Class<?>[]{String.class, Material.class}, BLOCKS, "basalt", Material.rock);

        Item[] shards = new Item[gems.length];
        for (int index = 0; index < gems.length; index++) {
            String gem = gems[index];
            try {
                Class<WavelengthItemRegistry> clazz = WavelengthItemRegistry.class;
                Field field = clazz.getField("SHARD_" + gem.toUpperCase());
                shards[index] = (Item) field.get(null);
            } catch (ReflectiveOperationException ignored) {}
            List<BlockCluster> clusters = new ArrayList<>();
            for (int bud = 0; bud < 4; bud++) {
                String suffix = switch(bud) {
                    case 0 -> "_bud_small";
                    case 1 -> "_bud_medium";
                    case 2 -> "_bud_large";
                    case 3 -> "_cluster";
                    default -> throw new IllegalStateException("Unexpected value: " + bud);
                };
                boolean grown = bud == 3;
                clusters.add((BlockCluster) ContentRegistry.create(gem + suffix, BlockCluster.class, new Class<?>[]{Item.class, boolean.class}, BLOCKS, shards[index], grown));
            }

            ContentRegistry.create(gem + "_budding", BlockBudding.class, new Class<?>[]{List.class}, BLOCKS, clusters);
        }

        for (int index = 0; index < shards.length; index++) {
            Item shard = shards[index];
            String gem = gems[index];
            BlockGenericOre ore = new BlockGenericOre().drops(shard);
            BLOCKS.add(new SolarisExtensions.Pair<>(ore, gem + "_ore"));
        }

        ((BlockGenericOre) ContentRegistry.create("ore_shimmerstone", BlockGenericOre.class, new Class<?>[]{}, BLOCKS)).drops(WavelengthItemRegistry.GEM_SHIMMERSTONE);
        for (String ore : ores) {
            try {
                Class<WavelengthItemRegistry> clazz = WavelengthItemRegistry.class;
                Field field = clazz.getField("RAW_" + ore.toUpperCase());
                Item raw = (Item) field.get(null);
                BlockGenericOre block = new BlockGenericOre().drops(raw);
                BLOCKS.add(new SolarisExtensions.Pair<>(block, "ore_" + ore));
            } catch (ReflectiveOperationException ignored) {}
        }
        
        ContentRegistry.create("log_spirit_sallow", BlockSided.class, new Class<?>[]{String.class, Material.class}, BLOCKS, "spirit_sallow_log", Material.wood);
        for (String wood : woods) ContentRegistry.create("log_" + wood, BlockSided.class, new Class<?>[]{String.class, Material.class}, BLOCKS, wood + "_log", Material.wood);
        for (String wood : woods) ContentRegistry.create("plank_" + wood, BlockGenericPlank.class, new Class<?>[]{String.class}, BLOCKS, wood + "_planks");
    }
}
