
package branko.brankosbuildingdetails.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin {

    private static int brankosBuildingDetails$debugCount = 0;

    @Inject(
            method = "getPathTypeFromState",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void brankosBuildingDetails$handleGlassPanels(
            BlockGetter level,
            BlockPos pos,
            CallbackInfoReturnable<PathType> cir
    ) {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof GlassPanelBlock) {
            System.out.println("[BBD DEBUG] Panel detected; forcing WALKABLE at " + pos);
        }
    }
}
