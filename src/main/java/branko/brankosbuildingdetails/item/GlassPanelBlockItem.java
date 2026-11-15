package branko.brankosbuildingdetails.item;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Custom BlockItem for GlassPanelBlock that handles placement
 * on blocks adjacent to existing glass panels. When clicking a
 * block next to a glass panel, it adds a face to the panel
 * instead of placing a new block.
 */
public class GlassPanelBlockItem extends BlockItem {
	public GlassPanelBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		Level level = context.getLevel();
		BlockPos targetPos = context.getClickedPos();
		Direction clickedFace = context.getClickedFace();

		// context.getClickedPos() is the target position where the block would be placed.
		// If that position already contains a glass panel, handle placement on the panel.
		BlockState existingState = level.getBlockState(targetPos);
		if (existingState.getBlock() instanceof GlassPanelBlock) {
			// Check if the player is clicking directly on an existing panel face.
			// If so, don't place anything.
			Player player = context.getPlayer();
			if (player != null) {
				double reach = player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.BLOCK_INTERACTION_RANGE);
				HitResult hitResult = player.pick(reach, 0.0F, false);
				if (hitResult instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(targetPos)) {
					Direction faceHit = GlassPanelBlock.getFaceAtHit(existingState, blockHit);
					if (faceHit != null) {
						// Clicking directly on an existing panel face — don't place
						return false;
					}
				}
			}
			// Add the face to the existing glass panel.
			// The clicked face is the direction from the backing block toward the glass panel.
			// The panel shape sits at the edge matching the face direction (e.g., NORTH=true → panel at Z=0→1).
			// The backing block is behind the clicked face, so the panel should be at the opposite edge.
			Direction faceToAdd = clickedFace.getOpposite();
			// If the face already exists, don't place anything
			if (existingState.getValue(GlassPanelBlock.getFaceProperty(faceToAdd))) {
				return false;
			}
			BlockState newState = existingState.setValue(
				GlassPanelBlock.getFaceProperty(faceToAdd), true
			);
			// Handle waterlogging
			net.minecraft.world.level.material.FluidState fluidState = level.getFluidState(targetPos);
			if (fluidState.getType() == net.minecraft.world.level.material.Fluids.WATER) {
				newState = newState.setValue(GlassPanelBlock.WATERLOGGED, true);
			}
			level.setBlock(targetPos, newState, Block.UPDATE_ALL);
			return true;
		}

		// Normal placement
		return super.placeBlock(context, state);
	}
}
