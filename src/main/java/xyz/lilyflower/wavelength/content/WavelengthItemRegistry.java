package xyz.lilyflower.wavelength.content;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import xyz.lilyflower.solaris.api.ContentRegistry;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.content.item.ItemBasic;
import xyz.lilyflower.wavelength.content.item.ItemFloaty;

@SuppressWarnings("unused")
public class WavelengthItemRegistry implements ContentRegistry<Item> {
    private static final Class<?>[] GENERIC = new Class<?>[]{CreativeTabs.class};
    private static final Class<?>[] FLOATY = new Class<?>[]{CreativeTabs.class, EnumFacing.class, float.class};
    static final List<SolarisExtensions.Pair<Item, String>> ITEMS = new ArrayList<>();

    @Override
    public List<SolarisExtensions.Pair<Item, String>> contents() {
        return ITEMS;
    }
    
    // shards, gems etc
    public static final Item SHARD_AMETHYST = ContentRegistry.create("shard_amethyst", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item SHARD_CITRINE = ContentRegistry.create("shard_citrine", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item SHARD_MOONSTONE = ContentRegistry.create("shard_moonstone", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item SHARD_ONYX = ContentRegistry.create("shard_onyx", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item SHARD_TOPAZ = ContentRegistry.create("shard_topaz", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item GEM_PALTAERIA = ContentRegistry.create("gem_paltaeria", ItemFloaty.class, FLOATY, ITEMS, WavelengthTab.RESOURCES, EnumFacing.UP, 0.1F).setMaxStackSize(16);
    public static final Item GEM_STRATINE = ContentRegistry.create("gem_stratine", ItemFloaty.class, FLOATY, ITEMS, WavelengthTab.RESOURCES, EnumFacing.DOWN, 0.1F).setMaxStackSize(16);
    public static final Item GEM_SHIMMERSTONE = ContentRegistry.create("gem_shimmerstone", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);

    // raw mats
    public static final Item RAW_STRATINE = ContentRegistry.create("raw_stratine", ItemFloaty.class, FLOATY, ITEMS, WavelengthTab.RESOURCES, EnumFacing.DOWN, 0.01F);
    public static final Item RAW_PALTAERIA = ContentRegistry.create("raw_paltaeria", ItemFloaty.class, FLOATY, ITEMS, WavelengthTab.RESOURCES, EnumFacing.UP, 0.01F);
    public static final Item RAW_AZURITE = ContentRegistry.create("raw_azurite", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item RAW_BLOODSTONE = ContentRegistry.create("raw_bloodstone", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item RAW_MALACHITE = ContentRegistry.create("raw_malachite", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);

    // pure mats
    public static final Item PURE_AZURITE = ContentRegistry.create("pure_azurite", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item PURE_BLOODSTONE = ContentRegistry.create("pure_bloodstone", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);
    public static final Item PURE_MALACHITE = ContentRegistry.create("pure_malachite", ItemBasic.class, GENERIC, ITEMS, WavelengthTab.RESOURCES);

    @Override
    public void register(SolarisExtensions.Pair<Item, String> pair) {
        Item item = pair.left();

        item.setUnlocalizedName("wavelength." + pair.right());
        item.setTextureName("wavelength:" + pair.right());

        GameRegistry.registerItem(item, "$APPLYPREFIX$wavelength:" + pair.right());
    }

    @Override
    public boolean valid(String key) {
        return true;
    }

    @Override
    public boolean runnable() {
        return Solaris.STATE == LoadStage.PRELOADER;
    }
}
