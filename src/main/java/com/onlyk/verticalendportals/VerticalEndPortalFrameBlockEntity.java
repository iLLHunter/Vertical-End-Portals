package com.onlyk.verticalendportals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VerticalEndPortalFrameBlockEntity extends BlockEntity {
    public VerticalEndPortalFrameBlockEntity(BlockPos pos, BlockState state) {
        super(VerticalEndPortals.VERTICAL_END_PORTAL_FRAME_BLOCK_ENTITY.get(), pos, state);
    }
}
