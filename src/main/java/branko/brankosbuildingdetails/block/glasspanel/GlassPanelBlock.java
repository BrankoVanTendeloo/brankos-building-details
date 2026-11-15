package branko.brankosbuildingdetails.block.glasspanel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;

import java.util.Set;

public class GlassPanelBlock extends MultifaceBlock {
	// Thin 1/16-block panel shapes for each face
	private static final VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape EAST_SHAPE = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private static final VoxelShape WEST_SHAPE = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape UP_SHAPE = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape DOWN_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

	public GlassPanelBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean isFaceSupported(Direction direction) {
		// All 6 faces are supported
		return true;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return calculateMultifaceShape(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return calculateMultifaceShape(state);
	}

	private VoxelShape calculateMultifaceShape(BlockState state) {
		Set<Direction> faces = availableFaces(state);
		if (faces.isEmpty()) {
			return Shapes.empty();
		}

		VoxelShape shape = Shapes.empty();
		for (Direction dir : faces) {
			shape = Shapes.or(shape, getShapeForDirection(dir));
		}
		return shape;
	}

	private VoxelShape getShapeForDirection(Direction direction) {
		return switch (direction) {
			case NORTH -> NORTH_SHAPE;
			case EAST -> EAST_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case UP -> UP_SHAPE;
			case DOWN -> DOWN_SHAPE;
			default -> Shapes.empty();
		};
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return type == PathComputationType.WATER && state.getFluidState().isSourceOfType(Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
	}

	/**
	 * Returns the face of the glass panel at the given position that was hit.
	 * Returns null if no face was hit.
	 */
	public static Direction getFaceAtHit(BlockState state, BlockHitResult hitResult) {
		BlockPos hitPos = hitResult.getBlockPos();
		Direction hitFace = hitResult.getDirection();

		// Check if the hit is within the thin panel area of any face
		Set<Direction> faces = availableFaces(state);
		for (Direction face : faces) {
			if (isHitOnFace(hitResult, face)) {
				return face;
			}
		}
		return null;
	}

	private static boolean isHitOnFace(BlockHitResult hitResult, Direction face) {
		BlockPos pos = hitResult.getBlockPos();
		double x = hitResult.getLocation().x() - pos.getX();
		double y = hitResult.getLocation().y() - pos.getY();
		double z = hitResult.getLocation().z() - pos.getZ();

		return switch (face) {
			case NORTH -> z <= 1.0 / 16.0;
			case SOUTH -> z >= 15.0 / 16.0;
			case EAST -> x >= 15.0 / 16.0;
			case WEST -> x <= 1.0 / 16.0;
			case UP -> y >= 15.0 / 16.0;
			case DOWN -> y <= 1.0 / 16.0;
			default -> false;
		};
	}

	/**
	 * Removes a single face from the block state.
	 */
	public static BlockState removeFace(BlockState state, Direction face) {
		BooleanProperty property = getFaceProperty(face);
		return state.setValue(property, false);
	}

	/**
	 * Checks if the block can survive without the given face.
	 */
	public static boolean canSurviveWithoutFace(BlockState state, LevelReader level, BlockPos pos, Direction face) {
		// Glass panels are freestanding - always survives
		return true;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		// Glass panels are freestanding - no support block needed
		return true;
	}

	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		// Glass panels should not be replaceable
		return false;
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		// Glass panels are freestanding - don't remove faces when adjacent blocks change
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		// Override to allow placement without support blocks
		BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
		BlockState blockState1 = context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace()));
		Direction direction = context.getClickedFace();

		if (!isFaceSupported(direction)) {
			return null;
		}

		BlockState newState = blockState.is(this)
			? blockState.setValue(getFaceProperty(direction), true)
			: (BlockState) this.defaultBlockState().setValue(getFaceProperty(direction), true);

		// Handle waterlogging
		if (blockState1.getFluidState().isSourceOfType(Fluids.WATER)) {
			newState = newState.setValue(WATERLOGGED, true);
		}

		return newState;
	}
}
