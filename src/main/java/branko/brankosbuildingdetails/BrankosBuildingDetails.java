package branko.brankosbuildingdetails;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBreakContext;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrankosBuildingDetails implements ModInitializer {
	public static final String MOD_ID = "brankos-building-details";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		BrankosBuildingDetailsBlocks.initialize();
		BrankosBuildingDetailsItems.initialize();

		// Track which face is being attacked when player starts breaking a glass panel
		AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
			if (level.isClientSide()) {
				return InteractionResult.PASS;
			}

			BlockState state = level.getBlockState(pos);
			if (!(state.getBlock() instanceof GlassPanelBlock)) {
				return InteractionResult.PASS;
			}

			// The clicked direction tells us which face of the block was clicked.
			// For a glass panel, we need to find which panel face is at that location.
			// Since panels are thin (1/16 block), the clicked direction helps us determine
			// which face the player is targeting.
			Direction face = findFaceAtDirection(state, pos, player, direction);
			if (face != null && GlassPanelBlock.hasFace(state, face)) {
				GlassPanelBreakContext.startBreaking(player, pos, face);
			}

			return InteractionResult.PASS;
		});

		PlayerBlockBreakEvents.CANCELED.register((world, player, pos, state, blockEntity) -> {
			GlassPanelBreakContext.stopBreaking(player);
		});
	}

	/**
	 * Determines which glass panel face the player is targeting based on their position
	 * relative to the block and the clicked direction.
	 */
	private Direction findFaceAtDirection(BlockState state, BlockPos pos, Player player, Direction clickedDirection) {
		// For UP/DOWN clicked faces, the clicked direction directly tells us which face
		if (clickedDirection == Direction.UP || clickedDirection == Direction.DOWN) {
			return clickedDirection;
		}

		double px = player.getX() - pos.getX() - 0.5;
		double pz = player.getZ() - pos.getZ() - 0.5;

		// Determine which face the player is closest to based on their position
		if (Math.abs(px) > Math.abs(pz)) {
			// Player is more to the east/west
			return px > 0 ? Direction.EAST : Direction.WEST;
		} else {
			// Player is more to the north/south
			return pz > 0 ? Direction.SOUTH : Direction.NORTH;
		}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
