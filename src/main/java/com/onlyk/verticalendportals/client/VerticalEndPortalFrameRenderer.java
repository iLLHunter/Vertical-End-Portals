package com.onlyk.verticalendportals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.onlyk.verticalendportals.VerticalEndPortalFrameBlock;
import com.onlyk.verticalendportals.VerticalEndPortalFrameBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class VerticalEndPortalFrameRenderer implements BlockEntityRenderer<VerticalEndPortalFrameBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public VerticalEndPortalFrameRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            VerticalEndPortalFrameBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = blockEntity.getBlockState();
        Direction face = state.getValue(VerticalEndPortalFrameBlock.FACE);
        Direction facing = state.getValue(VerticalEndPortalFrameBlock.FACING);
        if (face.getAxis() == facing.getAxis()) {
            return;
        }

        BlockState renderState = Blocks.END_PORTAL_FRAME.defaultBlockState()
                .setValue(EndPortalFrameBlock.FACING, Direction.SOUTH)
                .setValue(EndPortalFrameBlock.HAS_EYE, state.getValue(VerticalEndPortalFrameBlock.HAS_EYE));

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(frameRotation(face, facing));
        poseStack.translate(-0.5, -0.5, -0.5);
        blockRenderer.renderSingleBlock(
                renderState,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                ModelData.EMPTY,
                null
        );
        poseStack.popPose();
    }

    private static Quaternionf frameRotation(Direction face, Direction facing) {
        Vector3f up = face.step();
        Vector3f forward = facing.step();
        Vector3f right = new Vector3f(up).cross(forward);
        Matrix3f basis = new Matrix3f()
                .setColumn(0, right)
                .setColumn(1, up)
                .setColumn(2, forward);
        return new Quaternionf().setFromNormalized(basis);
    }
}
