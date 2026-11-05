package dev.remake1.tchighwaysigns.registry;

import dev.architectury.platform.Platform;
import dev.remake1.tchighwaysigns.HighwaySigns;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {

    /**
     * Items that open the highway sign editor. Same tag as TrafficCraft's wrench uses.
     * TrafficCraft 1.1.x does not tag its wrench, so the addon adds it to this tag itself (see {@code data/forge} and {@code data/c}).
     */
    public static final TagKey<Item> WRENCHES = TagKey.create(Registries.ITEM, Platform.isForge() ? new ResourceLocation("forge", "tools/wrench") : new ResourceLocation("c", "wrenches"));

    /** Additional blocks highway signs can be mounted on (besides TrafficCraft posts). */
    public static final TagKey<Block> PANEL_MOUNTS = TagKey.create(Registries.BLOCK, new ResourceLocation(HighwaySigns.MOD_ID, "panel_mounts"));

    private ModTags() {}
}
