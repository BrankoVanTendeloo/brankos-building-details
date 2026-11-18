
package branko.brankosbuildingdetails.util;

import branko.brankosbuildingdetails.block.glasspanel.GlassPanelBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.HashSet;
import java.util.Set;

public final class GlassPanelJumpHelper {

    private GlassPanelJumpHelper() {}

    public static boolean crossesVerticalPanel(
            Mob mob,
            double startX, double startY, double startZ,
            double endX, double endY, double endZ
    ) {
        BlockGetter level = mob.level();

        double halfWidth = mob.getBbWidth() / 2.0;
        double height = mob.getBbHeight();

        double dx = endX - startX;
        double dz = endZ - startZ;

        double distance = Math.sqrt(dx * dx + dz * dz);
        int steps = Math.max(1, (int) Math.ceil(distance * 32.0));

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        Set<BlockPos> panelsAtStart = new HashSet<>();

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;

            double x = startX + dx * t;
            double z = startZ + dz * t;
            double y = startY + (endY - startY) * t;

            AABB mobBox = new AABB(
                    x - halfWidth, y, z - halfWidth,
                    x + halfWidth, y + height, z + halfWidth
            );

            for (int bx = Mth.floor(mobBox.minX);
                 bx <= Mth.floor(mobBox.maxX); bx++) {
                for (int by = Mth.floor(mobBox.minY);
                     by <= Mth.floor(mobBox.maxY); by++) {
                    for (int bz = Mth.floor(mobBox.minZ);
                         bz <= Mth.floor(mobBox.maxZ); bz++) {

                        pos.set(bx, by, bz);
                        BlockState state = level.getBlockState(pos);

                        if (!(state.getBlock() instanceof GlassPanelBlock)) {
                            continue;
                        }

                        BlockPos panelPos = pos.immutable();

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (!GlassPanelBlock.hasFace(state, direction)) {
                                continue;
                            }

                            for (AABB shapeBox :
                                    GlassPanelBlock.getShapeForDirection(direction).toAabbs()) {

                                AABB worldShape = shapeBox.move(bx, by, bz);

                                if (!mobBox.intersects(worldShape)) {
                                    continue;
                                }

                                if (i == 0) {
                                    panelsAtStart.add(panelPos);
                                } else if (!panelsAtStart.contains(panelPos)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean canJumpOver(
            Mob mob,
            double startX, double startY, double startZ,
            double endX, double endY, double endZ
    ) {
        BlockGetter level = mob.level();

        double halfWidth = mob.getBbWidth() / 2.0;
        double height = mob.getBbHeight();

        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;

        double distance = Math.sqrt(dx * dx + dz * dz);
        int steps = Math.max(16, (int) Math.ceil(distance * 64.0));

        // Use the same minimum jump-height assumption as WalkNodeEvaluator.
        double jumpHeight = Math.max(1.25, mob.maxUpStep())
                + Math.max(0.0, mob.getBbWidth()) * 1.5;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;

            double x = startX + dx * t;
            double z = startZ + dz * t;

            // Approximate the jump as a parabolic arc.
            double y = startY + dy * t
                    + 4.0 * jumpHeight * t * (1.0 - t);

            AABB mobBox = new AABB(
                    x - halfWidth, y, z - halfWidth,
                    x + halfWidth, y + height, z + halfWidth
            );

            for (int bx = Mth.floor(mobBox.minX);
                 bx <= Mth.floor(mobBox.maxX); bx++) {
                for (int by = Mth.floor(mobBox.minY);
                     by <= Mth.floor(mobBox.maxY); by++) {
                    for (int bz = Mth.floor(mobBox.minZ);
                         bz <= Mth.floor(mobBox.maxZ); bz++) {

                        pos.set(bx, by, bz);

                        VoxelShape shape = level.getBlockState(pos)
                                .getCollisionShape(level, pos, CollisionContext.of(mob));

                        for (AABB shapeBox : shape.toAabbs()) {
                            if (mobBox.intersects(shapeBox.move(bx, by, bz))) {
                                if (mob instanceof Chicken) {
                                    System.out.println(
                                            "[GlassPanel] Collision at block "
                                                    + pos
                                                    + ", block state: "
                                                    + level.getBlockState(pos)
                                                    + ", jump progress: "
                                                    + t
                                                    + ", mob feet Y: "
                                                    + y
                                                    + ", obstacle: "
                                                    + shapeBox.move(bx, by, bz)
                                    );
                                }

                                return false;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("[GlassPanel] Jump trajectory accepted.");
        return true;
    }
}
