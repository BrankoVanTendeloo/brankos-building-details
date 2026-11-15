package branko.brankosbuildingdetails;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class BrankosBuildingDetailsItems {
	private static final ResourceKey<Item> GLASS_PANEL_KEY =
		ResourceKey.create(Registries.ITEM, BrankosBuildingDetails.id("glass_panel"));

	public static final Item GLASS_PANEL = Registry.register(
		BuiltInRegistries.ITEM,
		GLASS_PANEL_KEY,
		new BlockItem(
			BrankosBuildingDetailsBlocks.GLASS_PANEL,
			new Item.Properties().setId(GLASS_PANEL_KEY).useBlockDescriptionPrefix()
		)
	);

	private BrankosBuildingDetailsItems() {
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COLORED_BLOCKS)
			.register(output -> output.insertAfter(Items.STAINED_GLASS_PANE.pink(), GLASS_PANEL));
	}
}
