package dev.remake1.tchighwaysigns.forge;

import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.config.ModClientConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class CrossPlatformImpl {

    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC, HighwaySigns.MOD_ID + "-client.toml");
    }
}
