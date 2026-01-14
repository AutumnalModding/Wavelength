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
import xyz.lilyflower.wavelength.content.block.basic.BlockBasic;
import xyz.lilyflower.wavelength.content.block.gravity.BlockGravity;
import xyz.lilyflower.wavelength.content.block.BlockSided;
import xyz.lilyflower.wavelength.content.block.basic.BasicOre;
import xyz.lilyflower.wavelength.content.block.basic.BasicPlank;

@SuppressWarnings("unused")
public class WavelengthBlockRegistry implements ContentRegistry<Block> {
    private static final Class<?>[] BASIC = new Class<?>[]{Material.class};
    private static final Class<?>[] SIDED = new Class<?>[]{String.class, Material.class};
    private static final Class<?>[] PEDESTAL = new Class<?>[]{TileEntityPedestal.PedestalTier.class};
    private static final Class<?>[] GRAVITY = new Class<?>[]{Material.class, EnumFacing.class, float.class};

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
    };

    static final ArrayList<SolarisExtensions.Pair<Block, String>> BLOCKS = new ArrayList<>();

    @Override
    public ArrayList<SolarisExtensions.Pair<Block, String>> contents() {
        return BLOCKS;
    }

    // Misc blocks
    public static final Block BASALT;
    public static final Block BASALT_SMOOTH;
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

    private static Block rock(String name) {
        return ContentRegistry.create(name, BlockBasic.class, BASIC, BLOCKS, Material.rock);
    }

    static {
        ContentRegistry.create("pedestal_amethyst", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.BASIC);
        ContentRegistry.create("pedestal_citrine", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.BASIC);
        ContentRegistry.create("pedestal_topaz", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.BASIC);
        ContentRegistry.create("pedestal_all", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.UPGRADED);
        ContentRegistry.create("pedestal_onyx", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.ONYX);
        ContentRegistry.create("pedestal_moonstone", BlockPedestal.class, PEDESTAL, BLOCKS, TileEntityPedestal.PedestalTier.MOONSTONE);

        BASALT = ContentRegistry.create("basalt", BlockSided.class, SIDED, BLOCKS, "basalt", Material.rock);
        BASALT_SMOOTH = rock("basalt_smooth");
        BASALT_POLISHED = rock("basalt_polished");
        CALCITE_POLISHED = rock("calcite_polished");
        CALCITE = rock("calcite");
        ContentRegistry.create("floatblock_stratine", BlockGravity.class, GRAVITY, BLOCKS, Material.rock, EnumFacing.DOWN,0.5F);
        ContentRegistry.create("floatblock_paltaeria", BlockGravity.class, GRAVITY, BLOCKS, Material.rock, EnumFacing.UP, 0.5F);
        for (String gem : gems) ContentRegistry.create(gem + "_polished", BlockBasic.class, BASIC, BLOCKS, Material.rock);
        for (String gem : gems) ContentRegistry.create(gem + "_block", BlockAmethyst.class, ContentRegistry.EMPTY, BLOCKS);

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
            BasicOre ore = new BasicOre().drops(shard);
            BLOCKS.add(new SolarisExtensions.Pair<>(ore, gem + "_ore"));
        }

        ((BasicOre) ContentRegistry.create("ore_shimmerstone", BasicOre.class, ContentRegistry.EMPTY, BLOCKS)).drops(WavelengthItemRegistry.GEM_SHIMMERSTONE);
        for (String ore : ores) {
            try {
                Class<WavelengthItemRegistry> clazz = WavelengthItemRegistry.class;
                Field field = clazz.getField("RAW_" + ore.toUpperCase());
                Item raw = (Item) field.get(null);
                BasicOre block = new BasicOre().drops(raw);
                BLOCKS.add(new SolarisExtensions.Pair<>(block, "ore_" + ore));
            } catch (ReflectiveOperationException ignored) {}
        }

        Block stratines = new BlockGravity(Material.rock,EnumFacing.DOWN,0.05F).setCreativeTab(WavelengthTab.RESOURCES);
        Block paltaeria = new BlockGravity(Material.rock, EnumFacing.UP, 0.05F).setCreativeTab(WavelengthTab.RESOURCES);

        BLOCKS.add(new SolarisExtensions.Pair<>(paltaeria, "orePaltaeria"));
        BLOCKS.add(new SolarisExtensions.Pair<>(stratines, "ore_stratine"));

        ContentRegistry.create("log_spirit_sallow", BlockSided.class, SIDED, BLOCKS, "spirit_sallow_log", Material.wood);
        for (String wood : woods) ContentRegistry.create("log_" + wood, BlockSided.class, SIDED, BLOCKS, wood + "_log", Material.wood);
        for (String wood : woods) ContentRegistry.create("plank_" + wood, BasicPlank.class, new Class<?>[]{String.class}, BLOCKS, wood + "_planks");
    }
}
