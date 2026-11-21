package branko.brankosbuildingdetails;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import branko.brankosbuildingdetails.item.GlassPanelBlockItem;
import branko.brankosbuildingdetails.item.GlowSpectralItemFrameItem;
import branko.brankosbuildingdetails.item.SpectralItemFrameItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class BrankosBuildingDetailsItems {
	private static final ResourceKey<Item> SANDSTONE_BRICKS_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("sandstone_bricks"));

	public static final Item SANDSTONE_BRICKS = Registry.register(
		BuiltInRegistries.ITEM,
		SANDSTONE_BRICKS_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.SANDSTONE_BRICKS,
			new Item.Properties().setId(SANDSTONE_BRICKS_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> SANDSTONE_BRICK_STAIRS_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("sandstone_brick_stairs"));

	public static final Item SANDSTONE_BRICK_STAIRS = Registry.register(
		BuiltInRegistries.ITEM,
		SANDSTONE_BRICK_STAIRS_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.SANDSTONE_BRICK_STAIRS,
			new Item.Properties().setId(SANDSTONE_BRICK_STAIRS_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> SANDSTONE_BRICK_SLAB_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("sandstone_brick_slab"));

	public static final Item SANDSTONE_BRICK_SLAB = Registry.register(
		BuiltInRegistries.ITEM,
		SANDSTONE_BRICK_SLAB_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.SANDSTONE_BRICK_SLAB,
			new Item.Properties().setId(SANDSTONE_BRICK_SLAB_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> SANDSTONE_BRICK_WALL_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("sandstone_brick_wall"));

	public static final Item SANDSTONE_BRICK_WALL = Registry.register(
		BuiltInRegistries.ITEM,
		SANDSTONE_BRICK_WALL_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.SANDSTONE_BRICK_WALL,
			new Item.Properties().setId(SANDSTONE_BRICK_WALL_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> RED_SANDSTONE_BRICKS_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("red_sandstone_bricks"));

	public static final Item RED_SANDSTONE_BRICKS = Registry.register(
		BuiltInRegistries.ITEM,
		RED_SANDSTONE_BRICKS_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.RED_SANDSTONE_BRICKS,
			new Item.Properties().setId(RED_SANDSTONE_BRICKS_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> RED_SANDSTONE_BRICK_STAIRS_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("red_sandstone_brick_stairs"));

	public static final Item RED_SANDSTONE_BRICK_STAIRS = Registry.register(
		BuiltInRegistries.ITEM,
		RED_SANDSTONE_BRICK_STAIRS_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.RED_SANDSTONE_BRICK_STAIRS,
			new Item.Properties().setId(RED_SANDSTONE_BRICK_STAIRS_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> RED_SANDSTONE_BRICK_SLAB_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("red_sandstone_brick_slab"));

	public static final Item RED_SANDSTONE_BRICK_SLAB = Registry.register(
		BuiltInRegistries.ITEM,
		RED_SANDSTONE_BRICK_SLAB_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.RED_SANDSTONE_BRICK_SLAB,
			new Item.Properties().setId(RED_SANDSTONE_BRICK_SLAB_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> RED_SANDSTONE_BRICK_WALL_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("red_sandstone_brick_wall"));

	public static final Item RED_SANDSTONE_BRICK_WALL = Registry.register(
		BuiltInRegistries.ITEM,
		RED_SANDSTONE_BRICK_WALL_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.RED_SANDSTONE_BRICK_WALL,
			new Item.Properties().setId(RED_SANDSTONE_BRICK_WALL_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> GLASS_PANEL_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("glass_panel"));

	public static final Item GLASS_PANEL = Registry.register(
		BuiltInRegistries.ITEM,
		GLASS_PANEL_KEY,
		new GlassPanelBlockItem(
			BrankosBuildingDetailsBlocks.GLASS_PANEL,
			new Item.Properties().setId(GLASS_PANEL_KEY).useBlockDescriptionPrefix()
		)
	);

	private static final ResourceKey<Item> SPECTRAL_ITEM_FRAME_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("spectral_item_frame"));

	public static final Item SPECTRAL_ITEM_FRAME = Registry.register(
		BuiltInRegistries.ITEM,
		SPECTRAL_ITEM_FRAME_KEY,
		new SpectralItemFrameItem(
			BrankosBuildingDetails.SPECTRAL_ITEM_FRAME_TYPE,
			new Item.Properties().setId(SPECTRAL_ITEM_FRAME_KEY)
		)
	);

	private static final ResourceKey<Item> GLOW_SPECTRAL_ITEM_FRAME_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("glow_spectral_item_frame"));

	public static final Item GLOW_SPECTRAL_ITEM_FRAME = Registry.register(
		BuiltInRegistries.ITEM,
		GLOW_SPECTRAL_ITEM_FRAME_KEY,
		new GlowSpectralItemFrameItem(
			BrankosBuildingDetails.GLOW_SPECTRAL_ITEM_FRAME_TYPE,
			new Item.Properties().setId(GLOW_SPECTRAL_ITEM_FRAME_KEY)
		)
	);

	private BrankosBuildingDetailsItems() {
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(Blocks.CUT_SANDSTONE_SLAB, SANDSTONE_BRICKS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(SANDSTONE_BRICKS, SANDSTONE_BRICK_STAIRS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(SANDSTONE_BRICK_STAIRS, SANDSTONE_BRICK_SLAB));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(SANDSTONE_BRICK_SLAB, SANDSTONE_BRICK_WALL));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(Blocks.CUT_RED_SANDSTONE_SLAB, RED_SANDSTONE_BRICKS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(RED_SANDSTONE_BRICKS, RED_SANDSTONE_BRICK_STAIRS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(RED_SANDSTONE_BRICK_STAIRS, RED_SANDSTONE_BRICK_SLAB));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
			.register(output -> output.insertAfter(RED_SANDSTONE_BRICK_SLAB, RED_SANDSTONE_BRICK_WALL));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COLORED_BLOCKS)
			.register(output -> output.insertAfter(Items.STAINED_GLASS_PANE.pink(), GLASS_PANEL));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
			.register(output -> output.insertAfter(Items.ITEM_FRAME, SPECTRAL_ITEM_FRAME));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
			.register(output -> output.insertAfter(Items.GLOW_ITEM_FRAME, GLOW_SPECTRAL_ITEM_FRAME));
	}
}
