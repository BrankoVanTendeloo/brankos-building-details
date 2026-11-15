package branko.brankosbuildingdetails.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBreakContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;

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
		BlockState state = level.getBlockState(pos);

		if (!(state.getBlock() instanceof GlassPanelBlock)) {
			return;
		}

		// Get the face being broken from the context
		GlassPanelBreakContext.BreakInfo breakInfo = GlassPanelBreakContext.getBreakingInfo(player);
		if (breakInfo == null || !breakInfo.pos().equals(pos)) {
			return;
		}

		Direction face = breakInfo.face();
		if (face == null) {
			return;
		}

		// Remove only the targeted face
		if (GlassPanelBreakContext.breakFace(level, pos, state, face, player)) {
			GlassPanelBreakContext.stopBreaking(player);
			cir.setReturnValue(true);
		}
	}
}
