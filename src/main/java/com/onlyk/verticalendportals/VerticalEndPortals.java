package com.onlyk.verticalendportals;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(VerticalEndPortals.MODID)
public class VerticalEndPortals {
    public static final String MODID = "vertical_end_portals";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<VerticalEndPortalFrameBlock> VERTICAL_END_PORTAL_FRAME = BLOCKS.registerBlock(
            "vertical_end_portal_frame",
            VerticalEndPortalFrameBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.END_PORTAL_FRAME)
    );

    public static final DeferredBlock<VerticalEndPortalBlock> VERTICAL_END_PORTAL = BLOCKS.registerBlock(
            "vertical_end_portal",
            VerticalEndPortalBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.END_PORTAL)
    );

    public static final DeferredItem<BlockItem> VERTICAL_END_PORTAL_FRAME_ITEM = ITEMS.registerSimpleBlockItem(
            "vertical_end_portal_frame",
            VERTICAL_END_PORTAL_FRAME
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VerticalEndPortalBlockEntity>>
            VERTICAL_END_PORTAL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
                    "vertical_end_portal",
                    () -> BlockEntityType.Builder.of(
                            VerticalEndPortalBlockEntity::new,
                            VERTICAL_END_PORTAL.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VerticalEndPortalFrameBlockEntity>>
            VERTICAL_END_PORTAL_FRAME_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
                    "vertical_end_portal_frame",
                    () -> BlockEntityType.Builder.of(
                            VerticalEndPortalFrameBlockEntity::new,
                            VERTICAL_END_PORTAL_FRAME.get()
                    ).build(null)
            );

    public VerticalEndPortals(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(VERTICAL_END_PORTAL_FRAME_ITEM);
        }
    }
}
