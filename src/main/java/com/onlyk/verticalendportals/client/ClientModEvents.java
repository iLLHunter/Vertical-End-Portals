package com.onlyk.verticalendportals.client;

import com.onlyk.verticalendportals.VerticalEndPortals;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = VerticalEndPortals.MODID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                VerticalEndPortals.VERTICAL_END_PORTAL_FRAME_BLOCK_ENTITY.get(),
                VerticalEndPortalFrameRenderer::new
        );
        event.registerBlockEntityRenderer(
                VerticalEndPortals.VERTICAL_END_PORTAL_BLOCK_ENTITY.get(),
                TheEndPortalRenderer::new
        );
    }
}
