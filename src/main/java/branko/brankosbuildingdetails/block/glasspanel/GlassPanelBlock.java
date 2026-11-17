package branko.brankosbuildingdetails.block.glasspanel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
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

	public static boolean hasFace(BlockState state, Direction face) {
		return state.getValue(getFaceProperty(face));
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

	public static VoxelShape getShapeForDirection(Direction direction) {
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
		return switch (type) {
			case LAND -> true;
			case WATER -> state.getFluidState().isSourceOfType(Fluids.WATER);
			default -> false;
		};
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
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction clickedFace = context.getClickedFace();
		if (!isFaceSupported(clickedFace)) {
			return null;
		}
		// When canBeReplaced returns true (clicking on an existing glass panel),
		// the target position IS the clicked block, so the panel goes on the clicked face.
		// When placing in a new block (air/replaceable), the target is the adjacent empty block,
		// so the panel goes on the opposite face to attach against the clicked block face.
		Direction faceToAdd;
		BlockState baseState;
		BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
		if (context.replacingClickedOnBlock() && clickedState.getBlock() instanceof GlassPanelBlock) {
			faceToAdd = clickedFace;
			baseState = clickedState;
		} else {
			faceToAdd = clickedFace.getOpposite();
			baseState = defaultBlockState();
		}
		BlockState state = baseState.setValue(getFaceProperty(faceToAdd), true);
		// Handle waterlogging — check the target position where the block will be placed
		BlockPos targetPos = context.replacingClickedOnBlock() ? context.getClickedPos() : context.getClickedPos().relative(context.getClickedFace());
		net.minecraft.world.level.material.FluidState fluidState = context.getLevel().getFluidState(targetPos);
		if (fluidState.getType() == net.minecraft.world.level.material.Fluids.WATER) {
			state = state.setValue(WATERLOGGED, true);
		}
		return state;
	}

	@Override
	public boolean isValidStateForPlacement(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
		// Glass panels are freestanding - no backing block check needed
		return isFaceSupported(direction);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		// Glass panels are freestanding - don't remove faces when adjacent blocks change.
		// But if waterlogged, schedule a fluid tick so water can flow out through vacant faces.
		if (state.getValue(WATERLOGGED)) {
			tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return state;
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		// Only allow replacement when the held item is a glass panel item
		// This prevents other blocks from replacing the glass panel while
		// still allowing glass panels to add faces to existing glass panels
		return context.getItemInHand().getItem() instanceof BlockItem blockItem
				&& blockItem.getBlock() == this;
	}
}
