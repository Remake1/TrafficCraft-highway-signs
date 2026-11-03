package dev.remake1.tchighwaysigns.fabric;

import dev.remake1.tchighwaysigns.HighwaySigns;
import net.fabricmc.api.ModInitializer;

public final class HighwaySignsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HighwaySigns.init();
    }
}
