package dev.remake1.tchighwaysigns.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.remake1.tchighwaysigns.HighwaySigns;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HighwaySigns.MOD_ID)
public final class HighwaySignsForge {
    public HighwaySignsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(HighwaySigns.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        HighwaySigns.init();
    }
}
