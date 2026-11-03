package dev.remake1.tchighwaysigns;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.architectury.platform.Platform;
import dev.remake1.tchighwaysigns.init.ClientInitWrapper;
import dev.remake1.tchighwaysigns.registry.ModBlockEntities;
import dev.remake1.tchighwaysigns.registry.ModBlocks;
import dev.remake1.tchighwaysigns.registry.ModItems;
import dev.remake1.tchighwaysigns.registry.ModNetworkManager;
import net.fabricmc.api.EnvType;

public final class HighwaySigns {
    public static final String MOD_ID = "tchighwaysigns";
    public static final String MOD_NAME = "TrafficCraft: Highway Signs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModNetworkManager.init();
        CrossPlatform.registerConfig();

        if (Platform.getEnv() == EnvType.CLIENT) {
            ClientInitWrapper.init();
        }
    }
}
