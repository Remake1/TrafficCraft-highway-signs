package dev.remake1.tchighwaysigns.init;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.remake1.tchighwaysigns.client.HighwaySignColors;
import dev.remake1.tchighwaysigns.client.ber.HighwaySignBlockEntityRenderer;
import dev.remake1.tchighwaysigns.registry.ModBlockEntities;
import dev.remake1.tchighwaysigns.registry.ModBlocks;

public final class ClientInit {

    public static void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(mc -> {
            BlockEntityRendererRegistry.register(ModBlockEntities.HIGHWAY_SIGN_BLOCK_ENTITY.get(), HighwaySignBlockEntityRenderer::new);
        });

        ClientLifecycleEvent.CLIENT_STARTED.register(mc -> {
            ColorHandlerRegistry.registerBlockColors(new HighwaySignColors.Block(), ModBlocks.HIGHWAY_SIGN);
            ColorHandlerRegistry.registerItemColors(new HighwaySignColors.Item(), ModBlocks.HIGHWAY_SIGN);
        });
    }
}
