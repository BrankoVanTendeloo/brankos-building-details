package branko.brankosbuildingdetails.block.glasspanel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which face of a glass panel is being broken by each player.
 * Used by the server-side mixin to intercept block breaks and remove individual faces.
 */
public class GlassPanelBreakContext {
	private static final Map<Player, BreakInfo> BREAKING_FACES = new ConcurrentHashMap<>();

	public record BreakInfo(BlockPos pos, Direction face) {
	}

	public static void startBreaking(Player player, BlockPos pos, Direction face) {
		BREAKING_FACES.put(player, new BreakInfo(pos, face));
	}

	public static BreakInfo getBreakingInfo(Player player) {
		return BREAKING_FACES.get(player);
	}

	public static void stopBreaking(Player player) {
		BREAKING_FACES.remove(player);
	}

	/**
	 * Handles breaking a single face of a glass panel.
	 * Returns true if the face was successfully removed.
	 */
	public static boolean breakFace(Level level, BlockPos pos, BlockState state, Direction face, Player player) {
		if (!GlassPanelBlock.hasFace(state, face)) {
			return false;
		}

		BlockState newState = GlassPanelBlock.removeFace(state, face);

		// If removing this face would leave no faces, destroy the whole block
		if (MultifaceBlock.availableFaces(newState).isEmpty()) {
			level.destroyBlock(pos, true, player);
			return true;
		}

		// Check if the remaining state can survive
		if (!newState.canSurvive(level, pos)) {
			level.destroyBlock(pos, true, player);
			return true;
		}

		// Set the new state with the face removed
		level.setBlock(pos, newState, Block.UPDATE_ALL);
		return true;
	}
}
