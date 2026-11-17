
package branko.brankosbuildingdetails.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public abstract class GlassPanelPathCollisionMixin {

    @Shadow
    protected abstract PathType getCachedPathType(int x, int y, int z);

    @Shadow
    protected abstract double getFloorLevel(BlockPos pos);

    @Shadow
    protected abstract Node tryJumpOn(
            int x,
            int y,
            int z,
            int jumpSize,
            double nodeHeight,
            Direction travelDirection,
            PathType blockPathTypeCurrent,
            BlockPos.MutableBlockPos reusablePos
    );

    @Inject(
            method = "getNeighbors",
            at = @At("RETURN"),
            cancellable = true
    )
    private void brankosBuildingDetails$filterGlassPanelNeighbors(
            Node[] neighbors,
            Node current,
            CallbackInfoReturnable<Integer> cir
    ) {
        Mob mob = ((NodeEvaluatorAccessor) this)
                .brankosBuildingDetails$getMob();

        PathType blockPathTypeCurrent =
                this.getCachedPathType(current.x, current.y, current.z);

        PathType blockPathTypeAbove =
                this.getCachedPathType(current.x, current.y + 1, current.z);

        int jumpSize = 0;

        if (mob.getPathfindingMalus(blockPathTypeAbove) >= 0.0F
                && blockPathTypeCurrent != PathType.STICKY_HONEY) {
            jumpSize = Mth.floor(
                    Math.max(1.0F, mob.maxUpStep())
            );
        }

        double nodeHeight = this.getFloorLevel(
                new BlockPos(current.x, current.y, current.z)
        );

        int count = cir.getReturnValueI();
        int writeIndex = 0;

        for (int i = 0; i < count; i++) {
            Node candidate = neighbors[i];

            if (candidate == null) {
                continue;
            }

            if (brankosBuildingDetails$collidesWithGlassPanel(
                    current,
                    candidate
            )) {
                int dx = candidate.x - current.x;
                int dz = candidate.z - current.z;

                if (Math.abs(dx) + Math.abs(dz) != 1 || jumpSize <= 0) {
                    continue;
                }

                Direction direction;

                if (dx > 0) {
                    direction = Direction.EAST;
                } else if (dx < 0) {
                    direction = Direction.WEST;
                } else if (dz > 0) {
                    direction = Direction.SOUTH;
                } else {
                    direction = Direction.NORTH;
                }


                Node jumpNode = this.tryJumpOn(
                        candidate.x,
                        candidate.y,
                        candidate.z,
                        jumpSize,
                        nodeHeight,
                        direction,
                        blockPathTypeCurrent,
                        new BlockPos.MutableBlockPos()
                );

                if (jumpNode == null) {
                    continue;
                }

                candidate = jumpNode;
            }

            neighbors[writeIndex++] = candidate;
        }

        cir.setReturnValue(writeIndex);
    }

    private boolean brankosBuildingDetails$collidesWithGlassPanel(
            Node from,
            Node to
    ) {
        if (from.y != to.y) {
            return false;
        }

        Mob mob = ((NodeEvaluatorAccessor) this)
                .brankosBuildingDetails$getMob();

        BlockGetter level = mob.level();

        double halfWidth = mob.getBbWidth() / 2.0;
        double height = mob.getBbHeight();

        double startX = from.x + 0.5;
        double startZ = from.z + 0.5;

        double endX = to.x + 0.5;
        double endZ = to.z + 0.5;

        double dx = endX - startX;
        double dz = endZ - startZ;

        double distance = Math.sqrt(dx * dx + dz * dz);
        int steps = Math.max(1, (int) Math.ceil(distance * 32.0));

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;

            double x = startX + dx * progress;
            double z = startZ + dz * progress;

            AABB mobBox = new AABB(
                    x - halfWidth,
                    from.y,
                    z - halfWidth,
                    x + halfWidth,
                    from.y + height,
                    z + halfWidth
            );

            int minX = (int) Math.floor(mobBox.minX);
            int minY = (int) Math.floor(mobBox.minY);
            int minZ = (int) Math.floor(mobBox.minZ);

            int maxX = (int) Math.floor(mobBox.maxX);
            int maxY = (int) Math.floor(mobBox.maxY);
            int maxZ = (int) Math.floor(mobBox.maxZ);

            for (int blockX = minX; blockX <= maxX; blockX++) {
                for (int blockY = minY; blockY <= maxY; blockY++) {
                    for (int blockZ = minZ; blockZ <= maxZ; blockZ++) {

                        pos.set(blockX, blockY, blockZ);

                        BlockState state = level.getBlockState(pos);

                        if (!(state.getBlock() instanceof GlassPanelBlock)) {
                            continue;
                        }
                        System.out.println("[BBD] Checking panel at " + pos);

                        VoxelShape shape = state.getCollisionShape(level, pos);

                        for (AABB shapeBox : shape.toAabbs()) {
                            AABB worldShape = shapeBox.move(
                                    blockX,
                                    blockY,
                                    blockZ
                            );

                            if (mobBox.intersects(worldShape)) {
                                System.out.println("[BBD] PANEL COLLISION between " + from.x + " " + from.y + " " + from.z + " and " + to.x + " " + to.y + " " + to.z);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
