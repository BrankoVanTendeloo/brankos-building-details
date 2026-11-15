package branko.brankosbuildingdetails.client.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Intercepts block outline submission for glass panels
 * so that only the targeted face is outlined.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	/**
	 * Replaces the BlockOutlineRenderState shape with only the targeted face's
	 * shape when the outlined block is a glass panel.
	 */
	@Redirect(
		method = "submitHitOutline",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;shape()Lnet/minecraft/world/phys/shapes/VoxelShape;"
		)
	)
	private VoxelShape replaceGlassPanelOutlineShape(BlockOutlineRenderState state) {
		Minecraft client = Minecraft.getInstance();
		if (client.level == null) {
			return state.shape();
		}

		// Only handle glass panel blocks
		var blockState = client.level.getBlockState(state.pos());
		if (!(blockState.getBlock() instanceof GlassPanelBlock)) {
			return state.shape();
		}

		// Get the crosshair hit to determine which face is being targeted
		HitResult hitResult = client.hitResult;
		if (!(hitResult instanceof BlockHitResult hit)) {
			return state.shape();
		}
		if (!hit.getBlockPos().equals(state.pos())) {
			return state.shape();
		}

		// Find which face was hit
		Direction face = GlassPanelBlock.getFaceAtHit(blockState, hit);
		if (face == null) {
			return state.shape();
		}

		// Return the shape for just the targeted face
		VoxelShape faceShape = GlassPanelBlock.getShapeForDirection(face);
		return faceShape.isEmpty() ? state.shape() : faceShape;
	}
}

