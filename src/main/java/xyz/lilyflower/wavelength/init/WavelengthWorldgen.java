package xyz.lilyflower.wavelength.init;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import xyz.lilyflower.wavelength.content.world.WorldGenGeode;
import xyz.lilyflower.wavelength.redist.BlockMetaPair;

public class WavelengthWorldgen implements IWorldGenerator {
    public static final WavelengthWorldgen INSTANCE = new WavelengthWorldgen();

    static WorldGenGeode GEODE_TOPAZ;
    static WorldGenGeode GEODE_CITRINE;
    static WorldGenGeode GEODE_AMETHYST;

    private WavelengthWorldgen() {}

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        int x = (chunkX << 4) + random.nextInt(16) + 8;
        int z = (chunkZ << 4) + random.nextInt(16) + 8;
        WorldGenGeode geode = switch (random.nextInt(55)) {
            case 0 -> GEODE_TOPAZ;
            case 18 -> GEODE_CITRINE;
            case 36 -> GEODE_AMETHYST;
            default -> null;
        };

        if (geode != null) { // TODO: proper topaz/citrine heights
            int minimum = switch (geode.type) {
                case TOPAZ -> 75;
                case CITRINE -> 30;
                case AMETHYST -> 5;
            };

            int maximum = switch (geode.type) {
                case TOPAZ -> 255;
                case CITRINE -> 60;
                case AMETHYST -> 39;
            };

            geode.generate(world, random, x, MathHelper.getRandomIntegerInRange(random, minimum, maximum), z);
        }
    }

    public void geodes() {
        Block basalt = (Block) Block.blockRegistry.getObject("wavelength:basalt");
        Block calcite = (Block) Block.blockRegistry.getObject("wavelength:calcite");
        Wavelength.LOGGER.info("Using balcite blocks {} and {}", basalt.getUnlocalizedName(), calcite.getUnlocalizedName());

        GEODE_TOPAZ = new WorldGenGeode(
                new BlockMetaPair(basalt, 0),
                new BlockMetaPair(calcite, 0),
                (Block) Block.blockRegistry.getObject("wavelength:topaz_block"),
                (Block) Block.blockRegistry.getObject("wavelength:topaz_budding"),
                (Block) Block.blockRegistry.getObject("wavelength:topaz_bud_medium"),
                (Block) Block.blockRegistry.getObject("wavelength:topaz_cluster"),
                WorldGenGeode.Type.TOPAZ
        );

        GEODE_CITRINE = new WorldGenGeode(
                new BlockMetaPair(basalt, 0),
                new BlockMetaPair(calcite, 0),
                (Block) Block.blockRegistry.getObject("wavelength:citrine_block"),
                (Block) Block.blockRegistry.getObject("wavelength:citrine_budding"),
                (Block) Block.blockRegistry.getObject("wavelength:citrine_bud_medium"),
                (Block) Block.blockRegistry.getObject("wavelength:citrine_cluster"),
                WorldGenGeode.Type.CITRINE
        );

        GEODE_AMETHYST = new WorldGenGeode(
                new BlockMetaPair(basalt, 0),
                new BlockMetaPair(calcite, 0),
                (Block) Block.blockRegistry.getObject("wavelength:amethyst_block"),
                (Block) Block.blockRegistry.getObject("wavelength:amethyst_budding"),
                (Block) Block.blockRegistry.getObject("wavelength:amethyst_bud_medium"),
                (Block) Block.blockRegistry.getObject("wavelength:amethyst_cluster"),
                WorldGenGeode.Type.AMETHYST
        );
    }
}
