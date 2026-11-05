package dev.remake1.tchighwaysigns.neoforge;

import dev.remake1.tchighwaysigns.HighwaySigns;
import net.neoforged.fml.common.Mod;

@Mod(HighwaySigns.MOD_ID)
public final class HighwaySignsNeoForge {
    public HighwaySignsNeoForge() {
        // Architectury API finds the mod event bus itself on NeoForge.
        HighwaySigns.init();
    }
}
