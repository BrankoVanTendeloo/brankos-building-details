package branko.brankosbuildingdetails.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBreakContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow protected ServerPlayer player;

	@Shadow protected ServerLevel level;

	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
	private void interceptGlassPanelBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		ServerPlayer player = this.player;

		// Don't allow breaking if the held item can't destroy blocks (e.g., swords in creative mode)
		BlockState stateForCheck = level.getBlockState(pos);
		if (!player.getMainHandItem().canDestroyBlock(stateForCheck, level, pos, player)) {
			return;
		}

		// Get the breaking context to verify this is a tracked glass panel break
		GlassPanelBreakContext.BreakInfo breakInfo = GlassPanelBreakContext.getBreakingInfo(player);
		if (breakInfo == null || !breakInfo.pos().equals(pos)) {
			return;
		}

		// Read the current state from the level — the stale `state` parameter
		// may still have faces that were already removed by a previous break.
		BlockState currentState = level.getBlockState(pos);
		if (!(currentState.getBlock() instanceof GlassPanelBlock)) {
			return;
		}

		// Re-detect the face at break time to handle rapid successive breaks.
		Direction face = findFaceAtHit(currentState, player, pos);
		if (face == null) {
			// The aimed face was already removed by a previous break in this burst.
			// Still intercept to prevent vanilla destroyBlock from running and
			// deleting the entire remaining block without sound or particles.
			cir.setReturnValue(false);
			return;
		}

		// Remove only the targeted face
		if (GlassPanelBreakContext.breakFace(level, pos, currentState, face, player)) {
			// Do NOT call stopBreaking here — keep the context active so subsequent
			// destroyBlock calls (from the player's continued attack) are still
			// intercepted as face breaks. The context is cleared when the player
			// stops attacking or switches blocks.
			// Return false so the server does NOT send a block break packet to the client.
			// The client continues the destruction animation, but the setBlock update
			// (UPDATE_ALL) sends a block state change packet, so the client sees the
			// updated block (with the face removed) without a flash.
			cir.setReturnValue(false);
		}
	}

	/**
	 * Determines which glass panel face the player is targeting by raycasting from the player's
	 * eye position to get the exact hit location, then checking which panel face that location
	 * falls within on the current block state.
	 */
	private Direction findFaceAtHit(BlockState state, ServerPlayer player, BlockPos pos) {
		double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
		HitResult hitResult = player.pick(reach, 0.0F, false);
		if (!(hitResult instanceof BlockHitResult blockHit)) {
			return null;
		}
		if (!blockHit.getBlockPos().equals(pos)) {
			return null;
		}

		double x = blockHit.getLocation().x() - pos.getX();
		double y = blockHit.getLocation().y() - pos.getY();
		double z = blockHit.getLocation().z() - pos.getZ();

		for (Direction face : Direction.values()) {
			if (!GlassPanelBlock.hasFace(state, face)) {
				continue;
			}
			boolean onFace = switch (face) {
				case NORTH -> z <= 1.0 / 16.0;
				case SOUTH -> z >= 15.0 / 16.0;
				case EAST -> x >= 15.0 / 16.0;
				case WEST -> x <= 1.0 / 16.0;
				case UP -> y >= 15.0 / 16.0;
				case DOWN -> y <= 1.0 / 16.0;
				default -> false;
			};
			if (onFace) {
				return face;
			}
		}
		return null;
	}
}
