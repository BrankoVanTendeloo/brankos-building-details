package branko.brankosbuildingdetails.client.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;

import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Makes the FluidRenderer treat GlassPanelBlock like HalfTransparentBlock
 * so that water inside a waterlogged glass panel uses the water_overlay
 * texture instead of the flowing water texture.
 *
 * The FluidRenderer.tesselate() method checks whether the block at the fluid's
 * position is an instance of HalfTransparentBlock or LeavesBlock to decide
 * whether to use the overlay material for side faces. Since GlassPanelBlock
 * extends MultifaceBlock (which extends Block directly), it doesn't match
 * either check. This mixin intercepts the block reference and replaces it
 * with a HalfTransparentBlock instance so the instanceof check passes.
 *
 * Only triggers when the glass panel has a face active, so that water next to
 * a glass panel without any faces doesn't use the overlay.
 */
@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {

	/**
	 * Tracks the block state currently being processed in the tesselate loop,
	 * so the block redirect can check active faces.
	 */
	private static final ThreadLocal<BlockState> currentState = new ThreadLocal<>();

	/**
	 * Tracks the face direction being rendered in the tesselate loop,
	 * so the block redirect can check if the glass panel has a face
	 * pointing toward the water.
	 */
	private static final ThreadLocal<Direction> currentDirection = new ThreadLocal<>();

	/**
	 * Intercepts the face direction when stored in local variable 41,
	 * so the block redirect knows which direction the water is facing.
	 */
	@ModifyVariable(
		method = "tesselate",
		at = @At("STORE"),
		index = 41
	)
	private Direction trackDirection(Direction direction) {
		currentDirection.set(direction);
		return direction;
	}

	/**
	 * Intercepts the block state when it is stored in local variable 49,
	 * just before getBlock() is called to check instanceof HalfTransparentBlock.
	 * There are 4 astore 49 instructions (one per face direction in the tableswitch),
	 * so we capture all of them.
	 */
	@ModifyVariable(
		method = "tesselate",
		at = @At("STORE"),
		index = 49
	)
	private BlockState trackState(BlockState state) {
		currentState.set(state);
		return state;
	}

	/**
	 * Intercepts the block reference right after BlockState.getBlock() is called
	 * and before the instanceof HalfTransparentBlock check. For GlassPanelBlock,
	 * replaces it with a HalfTransparentBlock instance so the overlay check passes.
	 *
	 * Only replaces when the glass panel has a face pointing toward the water
	 * (i.e., the face opposite the direction from the water to the panel).
	 */
	@ModifyVariable(
		method = "tesselate",
		at = @At("STORE"),
		index = 52
	)
	private Block redirectGlassPanelToHalfTransparent(Block block) {
		if (block instanceof GlassPanelBlock) {
			BlockState state = currentState.get();
			Direction direction = currentDirection.get();
			if (state != null && direction != null) {
				// The direction is from the water block toward the glass panel.
				// The glass panel face that holds back the water is the opposite direction.
				if (MultifaceBlock.hasFace(state, direction.getOpposite())) {
					return net.minecraft.world.level.block.Blocks.GLASS;
				}
			}
		}
		currentState.remove();
		currentDirection.remove();
		return block;
	}
}
