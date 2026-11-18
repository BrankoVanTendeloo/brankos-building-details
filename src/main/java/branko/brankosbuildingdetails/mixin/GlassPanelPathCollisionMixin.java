
package branko.brankosbuildingdetails.mixin;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;
import branko.brankosbuildingdetails.util.GlassPanelJumpHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.chicken.Chicken;
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

    @Inject(
            method = "getFloorLevel",
            at = @At("RETURN"),
            cancellable = true
    )
    private void brankosBuildingDetails$correctGlassPanelFloor(
            BlockPos pos,
            CallbackInfoReturnable<Double> cir
    ) {
        Mob mob = ((NodeEvaluatorAccessor) this)
                .brankosBuildingDetails$getMob();

        BlockState state = mob.level().getBlockState(pos);

        if (state.getBlock() instanceof GlassPanelBlock
                && GlassPanelBlock.hasFace(state, Direction.DOWN)) {

            cir.setReturnValue(pos.getY() + 1.0 / 16.0);
        }
    }

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

    @Shadow
    protected abstract Node findAcceptedNode(
            int x,
            int y,
            int z,
            int jumpSize,
            double nodeHeight,
            Direction travelDirection,
            PathType blockPathTypeCurrent
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
                    Math.max(1.125F, mob.maxUpStep())
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


            boolean collides = brankosBuildingDetails$collidesWithGlassPanel(
                    current,
                    candidate
            );

            if (collides) {
                boolean crossesVerticalPanel =
                        GlassPanelJumpHelper.crossesVerticalPanel(
                                mob,
                                current.x + 0.5,
                                nodeHeight,
                                current.z + 0.5,
                                candidate.x + 0.5,
                                this.getFloorLevel(
                                        new BlockPos(candidate.x, candidate.y, candidate.z)
                                ),
                                candidate.z + 0.5
                        );

                if (crossesVerticalPanel) {
                    double candidateFloor = this.getFloorLevel(
                            new BlockPos(candidate.x, candidate.y, candidate.z)
                    );

                    boolean canJump = GlassPanelJumpHelper.canJumpOver(
                            mob,
                            current.x + 0.5, nodeHeight, current.z + 0.5,
                            candidate.x + 0.5, candidateFloor, candidate.z + 0.5
                    );

                    if (mob instanceof Chicken) {
                        System.out.println(
                                "[GlassPanel] Chicken crossing detected. Can jump: "
                                        + canJump
                        );
                    }

                    if (canJump) {
                        neighbors[writeIndex++] = candidate;
                    }

                    continue;
                }

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

                Node aboveNode = this.findAcceptedNode(
                        candidate.x,
                        candidate.y + 1,
                        candidate.z,
                        jumpSize - 1,
                        nodeHeight,
                        direction,
                        blockPathTypeCurrent
                );


                Node jumpNode = this.tryJumpOn(
                        candidate.x, candidate.y, candidate.z,
                        jumpSize, nodeHeight, direction,
                        blockPathTypeCurrent,
                        new BlockPos.MutableBlockPos()
                );

                if (jumpNode == null) continue;

                // A jump is only valid if it actually changes the vertical position.
                if (jumpNode.y <= candidate.y) continue;

                // Also verify that the mob can travel to the resulting node
                // without intersecting a glass panel.
                if (brankosBuildingDetails$collidesWithGlassPanel(current, jumpNode)) {
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

        double startY = this.getFloorLevel(
                new BlockPos(from.x, from.y, from.z)
        );
        double endY = this.getFloorLevel(
                new BlockPos(to.x, to.y, to.z)
        );

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

            double feetY = Math.max(startY, endY);

            AABB mobBox = new AABB(
                    x - halfWidth,
                    feetY,
                    z - halfWidth,
                    x + halfWidth,
                    feetY + height,
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

                        VoxelShape shape = state.getCollisionShape(level, pos);

                        for (AABB shapeBox : shape.toAabbs()) {
                            AABB worldShape = shapeBox.move(
                                    blockX,
                                    blockY,
                                    blockZ
                            );

                            if (mobBox.intersects(worldShape)) {
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
