package com.onlyk.verticalendportals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VerticalEndPortalBlockEntity extends TheEndPortalBlockEntity {
    public VerticalEndPortalBlockEntity(BlockPos pos, BlockState state) {
        super(VerticalEndPortals.VERTICAL_END_PORTAL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public boolean shouldRenderFace(Direction face) {
        return face.getAxis() == getBlockState().getValue(VerticalEndPortalBlock.AXIS);
    }
}
