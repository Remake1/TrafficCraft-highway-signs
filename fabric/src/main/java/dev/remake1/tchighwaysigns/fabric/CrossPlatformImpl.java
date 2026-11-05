package dev.remake1.tchighwaysigns.fabric;

import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.config.ModClientConfig;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ModConfig;

public final class CrossPlatformImpl {

    public static void registerConfig() {
        NeoForgeConfigRegistry.INSTANCE.register(HighwaySigns.MOD_ID, ModConfig.Type.CLIENT, ModClientConfig.SPEC, HighwaySigns.MOD_ID + "-client.toml");
    }
}
