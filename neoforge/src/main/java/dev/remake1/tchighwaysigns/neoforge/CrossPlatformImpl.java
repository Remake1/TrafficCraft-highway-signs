package dev.remake1.tchighwaysigns.neoforge;

import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.config.ModClientConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

public final class CrossPlatformImpl {

    public static void registerConfig() {
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC, HighwaySigns.MOD_ID + "-client.toml");
    }
}
