
package branko.brankosbuildingdetails.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.util.GlassPanelJumpHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MoveControl.class)
public abstract class MoveControlMixin {

    @Shadow @Final
    protected Mob mob;

    @Shadow
    protected double wantedX;

    @Shadow
    protected double wantedY;

    @Shadow
    protected double wantedZ;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z",
                    ordinal = 1
            )
    )
    private boolean brankosBuildingDetails$preventGlassPanelJump(
            BlockState state,
            TagKey<Block> tag
    ) {
        return state.getBlock() instanceof GlassPanelBlock
                || state.is(tag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void brankosBuildingDetails$jumpOverGlassPanels(
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci
    ) {
        if (!this.mob.onGround()
                || this.mob.isInLiquid()
                || this.mob.getNavigation().isDone()) {
            return;
        }

        double startX = this.mob.getX();
        double startY = this.mob.getY();
        double startZ = this.mob.getZ();

        double endX = this.wantedX;
        double endY = this.wantedY;
        double endZ = this.wantedZ;

        if (!GlassPanelJumpHelper.crossesVerticalPanel(
                this.mob,
                startX, startY, startZ,
                endX, endY, endZ
        )) {
            return;
        }

        this.mob.getJumpControl().jump();
    }
}
