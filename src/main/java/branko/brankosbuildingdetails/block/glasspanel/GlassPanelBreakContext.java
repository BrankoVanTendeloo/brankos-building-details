package branko.brankosbuildingdetails.block.glasspanel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

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
	 * Returns true if the face was successfully removed (partial break).
	 * Returns false if the whole block should be destroyed (last face) — lets the caller
	 * fall through to vanilla block breaking for proper sounds and particles.
	 */
	public static boolean breakFace(Level level, BlockPos pos, BlockState state, Direction face, Player player) {
		// Always re-read the current state from the level to avoid race conditions
		// when breaking faces in rapid succession
		BlockState currentState = level.getBlockState(pos);
		if (!(currentState.getBlock() instanceof GlassPanelBlock)) {
			return false;
		}

		if (!GlassPanelBlock.hasFace(currentState, face)) {
			return false;
		}

		BlockState newState = GlassPanelBlock.removeFace(currentState, face);

		// If removing this face would leave no faces, let vanilla destroy the whole block
		// to avoid doubled sounds/particles from our mixin + vanilla both running
		if (MultifaceBlock.availableFaces(newState).isEmpty()) {
			return false;
		}

		// Check if the remaining state can survive
		if (!newState.canSurvive(level, pos)) {
			return false;
		}

		// Play the break sound for the removed face - pass null to play for all nearby players
		// (passing a player would exclude them from hearing the sound)
		level.playSound(null, pos, currentState.getSoundType().getBreakSound(), SoundSource.BLOCKS, currentState.getSoundType().getVolume() * 0.8F, currentState.getSoundType().getPitch());

		// Set the new state with the face removed - UPDATE_ALL sends block update packets
		// to nearby clients so they see the updated state without a flash
		level.setBlock(pos, newState, Block.UPDATE_ALL);

		// Process the block's loot table for drops (e.g. Silk Touch) and spawn particles
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(
				new BlockParticleOption(ParticleTypes.BLOCK, currentState),
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				20, 0.5, 0.5, 0.5, 0
			);
			dropLoot(serverLevel, currentState, pos, player);
		}

		return true;
	}

	/**
	 * Processes the block's loot table and spawns any dropped items.
	 */
	private static void dropLoot(ServerLevel serverLevel, BlockState state, BlockPos pos, Player player) {
		Block block = state.getBlock();
		var lootTableKeyOpt = block.getLootTable();
		if (lootTableKeyOpt.isEmpty()) {
			return;
		}

		LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableKeyOpt.get());
		if (lootTable == null) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		Vec3 origin = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

		LootParams.Builder builder = new LootParams.Builder(serverLevel);
		builder.withParameter(LootContextParams.BLOCK_STATE, state);
		builder.withParameter(LootContextParams.TOOL, tool);
		builder.withParameter(LootContextParams.ORIGIN, origin);
		builder.withOptionalParameter(LootContextParams.THIS_ENTITY, player);

		LootParams params = builder.create(LootContextParamSets.BLOCK);
		java.util.List<ItemStack> drops = lootTable.getRandomItems(params);

		for (ItemStack stack : drops) {
			if (!stack.isEmpty()) {
				Block.popResource(serverLevel, pos, stack);
			}
		}
	}
}
