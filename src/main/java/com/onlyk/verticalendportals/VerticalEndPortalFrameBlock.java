package com.onlyk.verticalendportals;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VerticalEndPortalFrameBlock extends BaseEntityBlock {
    public static final MapCodec<VerticalEndPortalFrameBlock> CODEC = simpleCodec(VerticalEndPortalFrameBlock::new);
    public static final DirectionProperty FACE = DirectionProperty.create("face");
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty HAS_EYE = BlockStateProperties.EYE;

    private static final VoxelShape BASE_UP = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
    private static final VoxelShape BASE_DOWN = Block.box(0.0, 3.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape BASE_NORTH = Block.box(0.0, 0.0, 3.0, 16.0, 16.0, 16.0);
    private static final VoxelShape BASE_SOUTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 13.0);
    private static final VoxelShape BASE_EAST = Block.box(0.0, 0.0, 0.0, 13.0, 16.0, 16.0);
    private static final VoxelShape BASE_WEST = Block.box(3.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape FULL_UP = Shapes.or(BASE_UP, Block.box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0));
    private static final VoxelShape FULL_DOWN = Shapes.or(BASE_DOWN, Block.box(4.0, 0.0, 4.0, 12.0, 3.0, 12.0));
    private static final VoxelShape FULL_NORTH = Shapes.or(BASE_NORTH, Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 3.0));
    private static final VoxelShape FULL_SOUTH = Shapes.or(BASE_SOUTH, Block.box(4.0, 4.0, 13.0, 12.0, 12.0, 16.0));
    private static final VoxelShape FULL_EAST = Shapes.or(BASE_EAST, Block.box(13.0, 4.0, 4.0, 16.0, 12.0, 12.0));
    private static final VoxelShape FULL_WEST = Shapes.or(BASE_WEST, Block.box(0.0, 4.0, 4.0, 3.0, 12.0, 12.0));

    public VerticalEndPortalFrameBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACE, Direction.UP)
                .setValue(FACING, Direction.SOUTH)
                .setValue(HAS_EYE, false));
    }

    @Override
    protected MapCodec<VerticalEndPortalFrameBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VerticalEndPortalFrameBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        Direction markerDirection = getMarkerDirection(context, face);
        return defaultBlockState()
                .setValue(FACE, face)
                .setValue(FACING, markerDirection)
                .setValue(HAS_EYE, false);
    }

    private static Direction getMarkerDirection(BlockPlaceContext context, Direction face) {
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() != face.getAxis()) {
                return direction.getOpposite();
            }
        }

        return face.getAxis() == Direction.Axis.Y ? Direction.SOUTH : Direction.UP;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction face = state.getValue(FACE);
        return state.getValue(HAS_EYE) ? fullShape(face) : baseShape(face);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(HAS_EYE) ? 15 : 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!stack.is(Items.ENDER_EYE) || state.getValue(HAS_EYE)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        level.setBlock(pos, state.setValue(HAS_EYE, true), Block.UPDATE_ALL);
        level.levelEvent(1503, pos, 0);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        tryCreatePortal(level, pos, state.getValue(FACE));
        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state
                .setValue(FACE, rotation.rotate(state.getValue(FACE)))
                .setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state
                .setValue(FACE, mirror.mirror(state.getValue(FACE)))
                .setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, HAS_EYE);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    private void tryCreatePortal(Level level, BlockPos changedPos, Direction face) {
        Direction[] planeAxes = planeAxes(face.getAxis());
        Direction firstAxis = planeAxes[0];
        Direction secondAxis = planeAxes[1];

        for (int firstOffset = -2; firstOffset <= 2; firstOffset++) {
            for (int secondOffset = -2; secondOffset <= 2; secondOffset++) {
                BlockPos center = changedPos
                        .relative(firstAxis, -firstOffset)
                        .relative(secondAxis, -secondOffset);

                if (isCompleteFrame(level, center, firstAxis, secondAxis, face)) {
                    createPortal(level, center, firstAxis, secondAxis, face.getAxis());
                    return;
                }
            }
        }
    }

    private boolean isCompleteFrame(
            Level level,
            BlockPos center,
            Direction firstAxis,
            Direction secondAxis,
            Direction face
    ) {
        for (int offset = -1; offset <= 1; offset++) {
            if (!isFrameBlock(
                    level,
                    center.relative(firstAxis, offset).relative(secondAxis, -2),
                    face,
                    secondAxis
            )) {
                return false;
            }
            if (!isFrameBlock(
                    level,
                    center.relative(firstAxis, offset).relative(secondAxis, 2),
                    face,
                    secondAxis.getOpposite()
            )) {
                return false;
            }
            if (!isFrameBlock(
                    level,
                    center.relative(firstAxis, -2).relative(secondAxis, offset),
                    face,
                    firstAxis
            )) {
                return false;
            }
            if (!isFrameBlock(
                    level,
                    center.relative(firstAxis, 2).relative(secondAxis, offset),
                    face,
                    firstAxis.getOpposite()
            )) {
                return false;
            }
        }

        return true;
    }

    private boolean isFrameBlock(Level level, BlockPos pos, Direction expectedFace, Direction expectedFacing) {
        BlockState state = level.getBlockState(pos);
        return state.is(VerticalEndPortals.VERTICAL_END_PORTAL_FRAME.get())
                && state.getValue(HAS_EYE)
                && state.getValue(FACE) == expectedFace
                && state.getValue(FACING) == expectedFacing;
    }

    private void createPortal(
            Level level,
            BlockPos center,
            Direction firstAxis,
            Direction secondAxis,
            Direction.Axis normalAxis
    ) {
        BlockState portalState = VerticalEndPortals.VERTICAL_END_PORTAL.get()
                .defaultBlockState()
                .setValue(VerticalEndPortalBlock.AXIS, normalAxis);

        for (int firstOffset = -1; firstOffset <= 1; firstOffset++) {
            for (int secondOffset = -1; secondOffset <= 1; secondOffset++) {
                BlockPos portalPos = center
                        .relative(firstAxis, firstOffset)
                        .relative(secondAxis, secondOffset);
                level.setBlock(portalPos, portalState, Block.UPDATE_ALL);
            }
        }

        level.globalLevelEvent(1038, center, 0);
    }

    private static Direction[] planeAxes(Direction.Axis normalAxis) {
        return switch (normalAxis) {
            case X -> new Direction[]{Direction.SOUTH, Direction.UP};
            case Y -> new Direction[]{Direction.EAST, Direction.SOUTH};
            case Z -> new Direction[]{Direction.EAST, Direction.UP};
        };
    }

    private static VoxelShape baseShape(Direction face) {
        return switch (face) {
            case DOWN -> BASE_DOWN;
            case NORTH -> BASE_NORTH;
            case SOUTH -> BASE_SOUTH;
            case WEST -> BASE_WEST;
            case EAST -> BASE_EAST;
            default -> BASE_UP;
        };
    }

    private static VoxelShape fullShape(Direction face) {
        return switch (face) {
            case DOWN -> FULL_DOWN;
            case NORTH -> FULL_NORTH;
            case SOUTH -> FULL_SOUTH;
            case WEST -> FULL_WEST;
            case EAST -> FULL_EAST;
            default -> FULL_UP;
        };
    }
}
