package branko.brankosbuildingdetails;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public final class BrankosBuildingDetailsBlocks {
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
