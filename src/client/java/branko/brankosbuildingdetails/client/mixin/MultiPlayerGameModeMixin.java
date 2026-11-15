package branko.brankosbuildingdetails.client.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.client.BrankosBuildingDetailsClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	@Shadow public abstract void stopDestroyBlock();

	/**
	 * Prevents the client from playing break animation and particles when breaking
	 * a glass panel face (not the last face). The server handles the face removal
	 * and sends a block state update, so the client should not show a break.
	 */
	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
	private void interceptGlassPanelBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) {
			return;
		}

		// Only intercept if we're tracking this block as a multi-face glass panel
		if (!BrankosBuildingDetailsClient.ATTACKING_PANEL_FACES.containsKey(client.player.getStringUUID())) {
			return;
		}
		if (!BrankosBuildingDetailsClient.ATTACKING_PANEL_FACES.get(client.player.getStringUUID()).equals(pos)) {
			return;
		}

		BlockState state = client.level.getBlockState(pos);
		if (!(state.getBlock() instanceof GlassPanelBlock)) {
			return;
		}

		// If the panel has more than one face, prevent the client break animation
		if (GlassPanelBlock.availableFaces(state).size() > 1) {
			// Stop the destruction animation
			this.stopDestroyBlock();
			// Clear the tracking
			BrankosBuildingDetailsClient.clearTracking(client.player.getStringUUID());
			// Cancel the client-side break — the server will send the updated block state
			cir.setReturnValue(false);
		}
	}
}
