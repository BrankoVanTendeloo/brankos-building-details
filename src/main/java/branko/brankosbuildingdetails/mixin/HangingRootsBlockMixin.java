package branko.brankosbuildingdetails.mixin;

import branko.brankosbuildingdetails.BrankosBuildingDetails;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class HangingRootsBlockMixin {

	@Inject(method = "animateTick", at = @At("TAIL"))
	private void spawnFallingDirtParticles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		// Only spawn particles for hanging roots blocks
		if (state.getBlock() != Blocks.HANGING_ROOTS) {
			return;
		}
		// Spawn a falling dirt piece particle rarely (1 in 800 chance per animate tick)
		if (random.nextInt(800) == 0) {
			double x = pos.getX() + random.nextDouble();
			double y = pos.getY() + 1.0D;
			double z = pos.getZ() + random.nextDouble();
			level.addParticle(
				BrankosBuildingDetails.FALLING_DIRT_PIECE_PARTICLE,
				x, y, z,
				0.0D, 0.0D, 0.0D
			);
		}
	}
}
