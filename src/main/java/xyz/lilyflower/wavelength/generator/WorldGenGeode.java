package xyz.lilyflower.wavelength.generator;

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
import xyz.lilyflower.wavelength.include.BlockMetaPair;
import xyz.lilyflower.wavelength.include.BlockPos;
import xyz.lilyflower.wavelength.include.DoublePerlinNoiseSampler;

public class WorldGenGeode extends WorldGenerator {
    public enum Type {
        TOPAZ,
        CITRINE,
        AMETHYST
    }

	private final int minGenOffset, maxGenOffset;//geodeFeatureConfig.minGenOffset geodeFeatureConfig.maxGenOffset
	private final int threshold;//geodeFeatureConfig.invalidBlocksThreshold
	private final int[] distribution;//geodeFeatureConfig.distributionPoints
	private final int[] wall;//geodeFeatureConfig.outerWallDistance
	private final double fill, internal, midpoint, external;//geodeLayerThicknessConfig filling innerLayer middleLayer outerLayer
	private final int[] offset;//geodeFeatureConfig.pointOffset
	private final double crack;//geodeCrackConfig.generateCrackChance
	private final double size;//geodeCrackConfig.baseCrackSize
	private final int point;//geodeCrackConfig.crackPointOffset
	private final double noise;//geodeFeatureConfig.noiseMultiplier
	private final double chance;//Formerly known as geodeFeatureConfig.useAlternateLayer0Chance
	private final double placement;//geodeFeatureConfig.usePotentialPlacementsChance

	private final BlockMetaPair basalt;
	private final BlockMetaPair calcite;
	private final List<Block> buds;
	private final Block inner;
	private final Block budding;
    public final Type type;

	public WorldGenGeode(BlockMetaPair basalt, BlockMetaPair calcite, Block inner, Block budding, Block first, Block second, Type type) {
		this(-16, 16, 1, new int[]{3, 4}, new int[]{4, 5, 6}, 1.7D, 2.2D, 3.2D, 4.2D, new int[]{1, 2}, 0.95D, 2.0D, 2, 0.05D, 0.083D, 0.35D,
				basalt, calcite, inner, budding, first, second, type);
	}

	private WorldGenGeode(
            int minOffset, int maxOffset, int threshold, int[] distribution,
            int[] outerWallDist, double fill, double internal, double midpoint,
            double external, int[] offset, double crack, double size,
            int point, double noise, double chance, double placement,
            BlockMetaPair basalt, BlockMetaPair calcite, Block inner,
            Block budding, Block first, Block second, Type type
    ) {
		this.basalt = basalt;
		this.calcite = calcite;
		this.inner = inner;
		this.budding = budding;
        this.buds = ImmutableList.of(first, second);
        this.minGenOffset = minOffset;
        this.maxGenOffset = maxOffset;
		this.threshold = threshold;
		this.distribution = distribution;
        this.wall = outerWallDist;
		this.fill = fill;
        this.internal = internal;
        this.midpoint = midpoint;
        this.external = external;
        this.offset = offset;
        this.crack = crack;
        this.size = size;
        this.point = point;
        this.noise = noise;
        this.chance = chance;
		this.placement = placement;
        this.type = type;
	}

	/**
	 * This is used when generating amethyst so it doesn't generate in the middle of the air, ocean, hanging in trees, etc.
	 */
	protected boolean isInvalidCorner(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
        Material material = block.getMaterial();
        boolean valid = (material == Material.rock || material == Material.grass || material == Material.sand || material == Material.water);
		return !valid || !block.isOpaqueCube();
	}

	/**
	 * Geode code from 1.17, ported to 1.7.10 by Roadhog360, with help of embeddedt to port the noise samplers by using Trove instead of FastUtil where applicable.
	 * Note: Original variable locations are left as comments above the respective variable to make it easier to backtrack through the vanilla 1.17 code.
	 * Some of them use a number provider to do .get to get a number in the range. If this would get two numbers I used nextBoolean() instead to be faster.
	 */
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		BlockPos position = new BlockPos(x, y, z);
		List<Pair<BlockPos, Integer>> locations = Lists.newLinkedList();
		int point = SolarisExtensions.getRandom(distribution, random);
		DoublePerlinNoiseSampler sampler = DoublePerlinNoiseSampler.create(random, -4, 1.0D);//Somehow this was (double[])(1.0D) which doesn't make sense. Decompiler weirdism?
		List<BlockPos> positions = Lists.newLinkedList();
		double deviation = (double) point / (double) wall[wall.length - 1];
		double filling = 1.0D / Math.sqrt(this.fill);
		double inner = 1.0D / Math.sqrt(internal + deviation);
		double middleLayerSqrt = 1.0D / Math.sqrt(midpoint + deviation);
		double outerLayerSqrt = 1.0D / Math.sqrt(external + deviation);
		double crackSize = 1.0D / Math.sqrt(size + random.nextDouble() / 2.0D + (point > 3 ? deviation : 0.0D));
		boolean cracking = (double) random.nextFloat() < crack;
		int invalid = 0;

		int targetX;
		for (int i = 0; i < point; ++i) {
			targetX = SolarisExtensions.getRandom(wall, random);
			int targetY = SolarisExtensions.getRandom(wall, random);
			int targetZ = SolarisExtensions.getRandom(wall, random);
			BlockPos there = position.add(targetX, targetY, targetZ);
			if (isInvalidCorner(world, there.getX(), there.getY(), there.getZ())) {
				++invalid;
				if (invalid > threshold) {
					return false; // Comment this line to disable the valid generation check for testing purposes.
				}
			}

			locations.add(Pair.of(there, SolarisExtensions.getRandom(offset, random)));
		}

		if (cracking) {
			targetX = point * 2 + 1;
			switch (random.nextInt(4)) {
				case 0:
					positions.add(position.add(targetX, 7, 0));
					positions.add(position.add(targetX, 5, 0));
					positions.add(position.add(targetX, 1, 0));
					break;
				case 1:
					positions.add(position.add(0, 7, targetX));
					positions.add(position.add(0, 5, targetX));
					positions.add(position.add(0, 1, targetX));
					break;
				case 2:
					positions.add(position.add(targetX, 7, targetX));
					positions.add(position.add(targetX, 5, targetX));
					positions.add(position.add(targetX, 1, targetX));
					break;
				case 3:
					positions.add(position.add(0, 7, 0));
					positions.add(position.add(0, 5, 0));
					positions.add(position.add(0, 1, 0));
					break;
			}
		}

		List<BlockPos> buddings = Lists.newArrayList();
		Iterator<BlockPos> iterator = BlockPos.iterate(position.add(minGenOffset, minGenOffset, minGenOffset), position.add(maxGenOffset, maxGenOffset, maxGenOffset)).iterator();

        Wavelength.LOGGER.info("Generating {} geode at {} {} {}", this.type.name(), x, y, z);
		while (iterator.hasNext()) {
			BlockPos that = iterator.next();

			if (world.getBlock(
					that.getX(),
					that.getY(),
					that.getZ()
			).getBlockHardness(world, that.getX(), that.getY(), that.getZ()) == -1) {
				continue;
			}

			double sampled = sampler.sample(that.getX(), that.getY(), that.getZ()) * noise;
			double current = 0.0;
			for (Pair<BlockPos, Integer> pair : locations) {
				current += SolarisExtensions.invSqrt(
						that.getSquaredDistance(pair.getLeft()) + (double) pair.getRight()
				) + sampled;
			}

			if (current < outerLayerSqrt) {
				continue;
			}

			double distance = 0.0;
			for (BlockPos where : positions) {
				distance += SolarisExtensions.invSqrt(
						that.getSquaredDistance(where) + (double) this.point
				) + sampled;
			}

			if (cracking && distance >= crackSize && current < filling) world.setBlockToAir(that.getX(), that.getY(), that.getZ());
			else if (current >= filling) world.setBlockToAir(that.getX(), that.getY(), that.getZ());
			else if (current >= inner) {
				boolean place = random.nextFloat() < this.chance;
				if (place) world.setBlock(that.getX(), that.getY(), that.getZ(), budding);
				else world.setBlock(that.getX(), that.getY(), that.getZ(), this.inner);

				if (place && random.nextFloat() < placement) {
					buddings.add(new BlockPos(that));
				}
			} else if (current >= middleLayerSqrt) world.setBlock(
					that.getX(),
					that.getY(),
					that.getZ(),
					calcite.get(),
					calcite.getMeta(), 2
			);
			else world.setBlock(that.getX(), that.getY(), that.getZ(), basalt.get(), basalt.getMeta(), 2);
		}

		for (BlockPos budding : buddings) {
			Block block = SolarisExtensions.getRandom(buds, random);

			for (EnumFacing facing : EnumFacing.values()) {
				BlockPos there = budding.offset(facing);

				if (world.isAirBlock(there.getX(), there.getY(), there.getZ())) {
					int metadata = (random.nextBoolean() ? 0 : 6) + facing.ordinal();
					world.setBlock(there.getX(), there.getY(), there.getZ(), block, metadata, 2);
					break;
				}
			}
		}

		return true;
	}
}
