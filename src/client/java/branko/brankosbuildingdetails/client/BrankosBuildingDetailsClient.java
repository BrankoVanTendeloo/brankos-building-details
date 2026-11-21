package branko.brankosbuildingdetails.client;

import branko.brankosbuildingdetails.BrankosBuildingDetails;
import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.client.particle.SpectralParticleProvider;
import branko.brankosbuildingdetails.client.renderer.GlowSpectralItemFrameRenderer;
import branko.brankosbuildingdetails.client.renderer.SpectralItemFrameRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrankosBuildingDetailsClient implements ClientModInitializer {
	/**
	 * Tracks which glass panel block each player is attacking on the client side.
	 * Used by the client-side mixin to prevent break animation for multi-face panels.
	 */
	public static final Map<String, BlockPos> ATTACKING_PANEL_FACES = new ConcurrentHashMap<>();

	@Override
	public void onInitializeClient() {
		// Register entity renderers for spectral item frames
		EntityRendererRegistry.register(BrankosBuildingDetails.SPECTRAL_ITEM_FRAME_TYPE, SpectralItemFrameRenderer::new);
		EntityRendererRegistry.register(BrankosBuildingDetails.GLOW_SPECTRAL_ITEM_FRAME_TYPE, GlowSpectralItemFrameRenderer::new);

		// Register spectral particle provider with custom texture
		ParticleProviderRegistry.getInstance().register(BrankosBuildingDetails.SPECTRAL_PARTICLE, SpectralParticleProvider::new);

		// Track which glass panel face is being attacked on the client side
		AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
			BlockState state = level.getBlockState(pos);
			if (!(state.getBlock() instanceof GlassPanelBlock)) {
				return InteractionResult.PASS;
			}

			// Check if the panel has more than one face — if so, track it
			if (GlassPanelBlock.availableFaces(state).size() > 1) {
				ATTACKING_PANEL_FACES.put(player.getStringUUID(), pos);
			}

			return InteractionResult.PASS;
		});
	}

	/**
	 * Clears the tracking for the given player.
	 */
	public static void clearTracking(String playerUuid) {
		ATTACKING_PANEL_FACES.remove(playerUuid);
	}
}