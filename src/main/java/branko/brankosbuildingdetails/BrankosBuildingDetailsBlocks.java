package branko.brankosbuildingdetails;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public final class BrankosBuildingDetailsBlocks {
	private static final ResourceKey<Block> SANDSTONE_BRICKS_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("sandstone_bricks"));

	public static final Block SANDSTONE_BRICKS = Registry.register(
		BuiltInRegistries.BLOCK,
		SANDSTONE_BRICKS_KEY,
		new Block(
			BlockBehaviour.Properties.of()
				.setId(SANDSTONE_BRICKS_KEY)
				.mapColor(MapColor.SAND)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> SANDSTONE_BRICK_STAIRS_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("sandstone_brick_stairs"));

	public static final Block SANDSTONE_BRICK_STAIRS = Registry.register(
		BuiltInRegistries.BLOCK,
		SANDSTONE_BRICK_STAIRS_KEY,
		new StairBlock(
			SANDSTONE_BRICKS.defaultBlockState(),
			BlockBehaviour.Properties.of()
				.setId(SANDSTONE_BRICK_STAIRS_KEY)
				.mapColor(MapColor.SAND)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> SANDSTONE_BRICK_SLAB_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("sandstone_brick_slab"));

	public static final Block SANDSTONE_BRICK_SLAB = Registry.register(
		BuiltInRegistries.BLOCK,
		SANDSTONE_BRICK_SLAB_KEY,
		new SlabBlock(
			BlockBehaviour.Properties.of()
				.setId(SANDSTONE_BRICK_SLAB_KEY)
				.mapColor(MapColor.SAND)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> SANDSTONE_BRICK_WALL_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("sandstone_brick_wall"));

	public static final Block SANDSTONE_BRICK_WALL = Registry.register(
		BuiltInRegistries.BLOCK,
		SANDSTONE_BRICK_WALL_KEY,
		new WallBlock(
			BlockBehaviour.Properties.of()
				.setId(SANDSTONE_BRICK_WALL_KEY)
				.mapColor(MapColor.SAND)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> RED_SANDSTONE_BRICKS_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("red_sandstone_bricks"));

	public static final Block RED_SANDSTONE_BRICKS = Registry.register(
		BuiltInRegistries.BLOCK,
		RED_SANDSTONE_BRICKS_KEY,
		new Block(
			BlockBehaviour.Properties.of()
				.setId(RED_SANDSTONE_BRICKS_KEY)
				.mapColor(MapColor.COLOR_ORANGE)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> RED_SANDSTONE_BRICK_STAIRS_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("red_sandstone_brick_stairs"));

	public static final Block RED_SANDSTONE_BRICK_STAIRS = Registry.register(
		BuiltInRegistries.BLOCK,
		RED_SANDSTONE_BRICK_STAIRS_KEY,
		new StairBlock(
			RED_SANDSTONE_BRICKS.defaultBlockState(),
			BlockBehaviour.Properties.of()
				.setId(RED_SANDSTONE_BRICK_STAIRS_KEY)
				.mapColor(MapColor.COLOR_ORANGE)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> RED_SANDSTONE_BRICK_SLAB_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("red_sandstone_brick_slab"));

	public static final Block RED_SANDSTONE_BRICK_SLAB = Registry.register(
		BuiltInRegistries.BLOCK,
		RED_SANDSTONE_BRICK_SLAB_KEY,
		new SlabBlock(
			BlockBehaviour.Properties.of()
				.setId(RED_SANDSTONE_BRICK_SLAB_KEY)
				.mapColor(MapColor.COLOR_ORANGE)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> RED_SANDSTONE_BRICK_WALL_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("red_sandstone_brick_wall"));

	public static final Block RED_SANDSTONE_BRICK_WALL = Registry.register(
		BuiltInRegistries.BLOCK,
		RED_SANDSTONE_BRICK_WALL_KEY,
		new WallBlock(
			BlockBehaviour.Properties.of()
				.setId(RED_SANDSTONE_BRICK_WALL_KEY)
				.mapColor(MapColor.COLOR_ORANGE)
				.strength(0.8F, 0.8F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
		)
	);

	private static final ResourceKey<Block> GLASS_PANEL_KEY =
		ResourceKey.create(Registries.BLOCK, BrankosBuildingDetails.id("glass_panel"));

	public static final Block GLASS_PANEL = Registry.register(
		BuiltInRegistries.BLOCK,
		GLASS_PANEL_KEY,
		new GlassPanelBlock(
			BlockBehaviour.Properties.of()
				.setId(GLASS_PANEL_KEY)
				.noOcclusion()
				.strength(0.3F)
				.sound(SoundType.GLASS)
				.pushReaction(PushReaction.POPPED)
		)
	);

	private BrankosBuildingDetailsBlocks() {
	}

	public static void initialize() {
		BrankosBuildingDetails.LOGGER.info("Registered blocks");
	}
}
