package dev.remake1.tchighwaysigns.fabric;

import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.config.ModClientConfig;
import fuzs.forgeconfigapiport.impl.config.ForgeConfigRegistryImpl;
import net.minecraftforge.fml.config.ModConfig;

public final class CrossPlatformImpl {

    public static void registerConfig() {
        ForgeConfigRegistryImpl.INSTANCE.register(HighwaySigns.MOD_ID, ModConfig.Type.CLIENT, ModClientConfig.SPEC, HighwaySigns.MOD_ID + "-client.toml");
    }
}
