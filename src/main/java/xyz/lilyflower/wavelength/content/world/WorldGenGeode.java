package xyz.lilyflower.wavelength.content.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import xyz.lilyflower.solaris.util.SolarisExtensions;
import xyz.lilyflower.wavelength.init.Wavelength;
import xyz.lilyflower.wavelength.redist.BlockMetaPair;
import xyz.lilyflower.wavelength.redist.BlockPos;
import xyz.lilyflower.wavelength.redist.DoublePerlinNoiseSampler;

public class WorldGenGeode extends WorldGenerator {
    public enum Type {
        TOPAZ,
        CITRINE,
        AMETHYST
    }

	private final int minGenOffset, maxGenOffset;//geodeFeatureConfig.minGenOffset geodeFeatureConfig.maxGenOffset
	private final int threshold;//geodeFeatureConfig.invalidBlocksThreshold
	private final int[] distribution;//geodeFeatureConfig.distributionPoints
	private final int[] outerWallDistance;//geodeFeatureConfig.outerWallDistance
	private final double fill, innerLayer, middleLayer, outerLayer;//geodeLayerThicknessConfig filling innerLayer middleLayer outerLayer
	private final int[] offset;//geodeFeatureConfig.pointOffset
	private final double generateCrackChance;//geodeCrackConfig.generateCrackChance
	private final double baseCrackSize;//geodeCrackConfig.baseCrackSize
	private final int crackPointOffset;//geodeCrackConfig.crackPointOffset
	private final double noise;//geodeFeatureConfig.noiseMultiplier
	private final double buddingAmethystChance;//Formerly known as geodeFeatureConfig.useAlternateLayer0Chance
	private final double usePotentialPlacementsChance;//geodeFeatureConfig.usePotentialPlacementsChance

	private final BlockMetaPair outer;
	private final BlockMetaPair middle;
	private final List<Block> buds;
	private final Block inner;
	private final Block budding;
    public final Type type;

	public WorldGenGeode(BlockMetaPair outer, BlockMetaPair middle, Block inner, Block budding, Block bud1, Block bud2, Type type) {
		this(-16, 16, 1, new int[]{3, 4}, new int[]{4, 5, 6}, 1.7D, 2.2D, 3.2D, 4.2D, new int[]{1, 2}, 0.95D, 2.0D, 2, 0.05D, 0.083D, 0.35D,
				outer, middle, inner, budding, bud1, bud2, type);
	}

//	public WorldGenGeode(BlockMetaPair outer, BlockMetaPair middleBlock) {
//		this(outer, middleBlock, ModBlocks.AMETHYST_BLOCK.get(), ModBlocks.BUDDING_AMETHYST.get(), ModBlocks.AMETHYST_CLUSTER_1.get(), ModBlocks.AMETHYST_CLUSTER_2.get());
//	}

	private WorldGenGeode(
            int minOffset, int maxOffset, int threshold, int[] distribution,
            int[] outerWallDist, double fill, double inner, double middle,
            double outer, int[] pointOff, double crackChance, double baseCrack,
            int crackPointOff, double noiseAmp, double budChance, double potentialPlaceChance,
            BlockMetaPair outerBlock, BlockMetaPair middleBlock, Block innerBlock,
            Block innerBuddingBlock, Block bud1, Block bud2, Type type
    ) {
		this.outer = outerBlock;
		this.middle = middleBlock;
		this.inner = innerBlock;
		this.budding = innerBuddingBlock;
        this.buds = ImmutableList.of(bud1, bud2);
        this.minGenOffset = minOffset;
        this.maxGenOffset = maxOffset;
		this.threshold = threshold;
		this.distribution = distribution;
        this.outerWallDistance = outerWallDist;
		this.fill = fill;
        this.innerLayer = inner;
        this.middleLayer = middle;
        this.outerLayer = outer;
        this.offset = pointOff;
        this.generateCrackChance = crackChance;
        this.baseCrackSize = baseCrack;
        this.crackPointOffset = crackPointOff;
        this.noise = noiseAmp;
        this.buddingAmethystChance = budChance;
		this.usePotentialPlacementsChance = potentialPlaceChance;
        this.type = type;
	}

	/**
	 * This is used when generating amethyst so it doesn't generate in the middle of the air, ocean, hanging in trees, etc.
	 */
	protected boolean isInvalidCorner(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block.getMaterial() != Material.rock || !block.isOpaqueCube();
	}

	/**
	 * Geode code from 1.17, ported to 1.7.10 by Roadhog360, with help of embeddedt to port the noise samplers by using Trove instead of FastUtil where applicable.
	 * Note: Original variable locations are left as comments above the respective variable to make it easier to backtrack through the vanilla 1.17 code.
	 * Some of them use a number provider to do .get to get a number in the range. If this would get two numbers I used nextBoolean() instead to be faster.
	 */
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		List<Pair<BlockPos, Integer>> list = Lists.newLinkedList();
		int point = SolarisExtensions.getRandom(distribution, random);
		DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(random, -4, 1.0D);//Somehow this was (double[])(1.0D) which doesn't make sense. Decompiler weirdism?
		List<BlockPos> list2 = Lists.newLinkedList();
		double outerWallMaxDiv = (double) point / (double) outerWallDistance[outerWallDistance.length - 1];
		double filling = 1.0D / Math.sqrt(this.fill);
		double inner = 1.0D / Math.sqrt(innerLayer + outerWallMaxDiv);
		double middleLayerSqrt = 1.0D / Math.sqrt(middleLayer + outerWallMaxDiv);
		double outerLayerSqrt = 1.0D / Math.sqrt(outerLayer + outerWallMaxDiv);
		double crackSize = 1.0D / Math.sqrt(baseCrackSize + random.nextDouble() / 2.0D + (point > 3 ? outerWallMaxDiv : 0.0D));
		boolean cracking = (double) random.nextFloat() < generateCrackChance;
		int m = 0;

		int s;
		BlockPos blockPos6;
		for (int r = 0; r < point; ++r) {
			s = SolarisExtensions.getRandom(outerWallDistance, random);
			int p = SolarisExtensions.getRandom(outerWallDistance, random);
			int q = SolarisExtensions.getRandom(outerWallDistance, random);
			blockPos6 = blockPos.add(s, p, q);
			if (isInvalidCorner(world, blockPos6.getX(), blockPos6.getY(), blockPos6.getZ())) {
				++m;
				if (m > threshold) {
					return false; // Comment this line to disable the valid generation check for testing purposes.
				}
			}

			list.add(Pair.of(blockPos6, SolarisExtensions.getRandom(offset, random)));
		}

		if (cracking) {
			s = point * 2 + 1;
			switch (random.nextInt(4)) {
				case 0:
					list2.add(blockPos.add(s, 7, 0));
					list2.add(blockPos.add(s, 5, 0));
					list2.add(blockPos.add(s, 1, 0));
					break;
				case 1:
					list2.add(blockPos.add(0, 7, s));
					list2.add(blockPos.add(0, 5, s));
					list2.add(blockPos.add(0, 1, s));
					break;
				case 2:
					list2.add(blockPos.add(s, 7, s));
					list2.add(blockPos.add(s, 5, s));
					list2.add(blockPos.add(s, 1, s));
					break;
				case 3:
					list2.add(blockPos.add(0, 7, 0));
					list2.add(blockPos.add(0, 5, 0));
					list2.add(blockPos.add(0, 1, 0));
					break;
			}
		}

		List<BlockPos> buddingList = Lists.newArrayList();
		Iterator<BlockPos> var48 = BlockPos.iterate(blockPos.add(minGenOffset, minGenOffset, minGenOffset), blockPos.add(maxGenOffset, maxGenOffset, maxGenOffset)).iterator();

		Block budBlock;
        Wavelength.LOGGER.info("Generating {} geode at {} {} {}", this.inner.getUnlocalizedName(), x, y, z);
		while (true) {
			double current;
			double v;
			BlockPos blockPos3;
			do {
				if (!var48.hasNext()) {

					for (BlockPos buddingPos : buddingList) {
						budBlock = SolarisExtensions.getRandom(buds, random);
						EnumFacing[] directions = EnumFacing.values();

						for (EnumFacing budFacing : directions) {
							BlockPos budPos = buddingPos.offset(budFacing);

							if (world.isAirBlock(budPos.getX(), budPos.getY(), budPos.getZ())) {
								world.setBlock(budPos.getX(), budPos.getY(), budPos.getZ(), budBlock, (random.nextBoolean() ? 0 : 6)/*picks a random bud size*/ + budFacing.ordinal(), 2);
								break;
							}
						}
					}

					return true;
				}

				blockPos3 = var48.next();
				double t = doublePerlinNoiseSampler.sample(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()) * noise;
				current = 0.0D;
				v = 0.0D;

				Iterator<Pair<BlockPos, Integer>> var40;
				Pair<BlockPos, Integer> pair;
				for (var40 = list.iterator(); var40.hasNext(); current += SolarisExtensions.invSqrt(blockPos3.getSquaredDistance(pair.getLeft()) + (double) pair.getRight()) + t) {
					pair = var40.next();
				} //Almost deleted this code for being unused, but the variable in the for loop is vital to later parts of the code.

				BlockPos blockPos4;
				Iterator<BlockPos> var41;
				for (var41 = list2.iterator(); var41.hasNext(); v += SolarisExtensions.invSqrt(blockPos3.getSquaredDistance(blockPos4) + (double) crackPointOffset) + t) {
					blockPos4 = var41.next();
				} //Almost deleted this code for being unused, but the variable in the for loop is vital to later parts of the code.
			} while (current < outerLayerSqrt);

			if (world.getBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()).getBlockHardness(world, blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()) != -1) {
				if (cracking && v >= crackSize && current < filling) {
					world.setBlockToAir(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
				} else if (current >= filling) {
					world.setBlockToAir(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());//FillingProvider
					//Fun fact, comment out this line for some really odd shapes lol
				} else if (current >= inner) {
					boolean bl2 = (double) random.nextFloat() < this.buddingAmethystChance;
					if (bl2) {
						world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), budding);//AlternateInnerLayerProvider
					} else {
						world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), this.inner);//InnerLayerProvider
					}

					//This boolean is always true and !true == false
					if ((/* !geodeFeatureConfig.placementsRequireLayer0Alternate || */bl2) && (double) random.nextFloat() < usePotentialPlacementsChance) {
						buddingList.add(new BlockPos(blockPos3));
					}
				} else if (current >= middleLayerSqrt) {
					world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), middle.get(), middle.getMeta(), 2);//MiddleLayerProvider also TODO I need to make this layer configurable
				} else if (current >= outerLayerSqrt) {
					world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), outer.get(), outer.getMeta(), 2);//OuterLayerProvider
				}
			}
		}
	}
}
