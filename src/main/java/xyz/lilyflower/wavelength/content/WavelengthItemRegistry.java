package xyz.lilyflower.wavelength.content;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import net.minecraft.item.Item;
import xyz.lilyflower.solaris.api.ContentRegistry;
import xyz.lilyflower.solaris.api.LoadStage;
import xyz.lilyflower.solaris.init.Solaris;
import xyz.lilyflower.solaris.util.SolarisExtensions;

@SuppressWarnings("unused")
public class WavelengthItemRegistry implements ContentRegistry<Item> {
    static final ArrayList<SolarisExtensions.Pair<Item, String>> ITEMS = new ArrayList<>();

    @Override
    public ArrayList<SolarisExtensions.Pair<Item, String>> contents() {
        return ITEMS;
    }
    
    // shards, gems etc
    public static final Item SHARD_AMETHYST = ContentRegistry.create("shard_amethyst", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item SHARD_CITRINE = ContentRegistry.create("shard_citrine", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item SHARD_MOONSTONE = ContentRegistry.create("shard_moonstone", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item SHARD_ONYX = ContentRegistry.create("shard_onyx", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item SHARD_TOPAZ = ContentRegistry.create("shard_topaz", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item GEM_PALTAERIA = ContentRegistry.create("gem_paltaeria", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item GEM_STRATINE = ContentRegistry.create("gem_stratine", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item GEM_SHIMMERSTONE = ContentRegistry.create("gem_shimmerstone", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);

    // raw mats
    public static final Item RAW_STRATINE = ContentRegistry.create("raw_stratine", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item RAW_PALTAERIA = ContentRegistry.create("raw_paltaeria", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item RAW_AZURITE = ContentRegistry.create("raw_azurite", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item RAW_BLOODSTONE = ContentRegistry.create("raw_bloodstone", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item RAW_MALACHITE = ContentRegistry.create("raw_malachite", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);

    // pure mats
    public static final Item PURE_AZURITE = ContentRegistry.create("pure_azurite", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item PURE_BLOODSTONE = ContentRegistry.create("pure_bloodstone", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);
    public static final Item PURE_MALACHITE = ContentRegistry.create("pure_malachite", Item.class, new Class<?>[]{}, ITEMS).setCreativeTab(WavelengthTab.MATERIALS);

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
