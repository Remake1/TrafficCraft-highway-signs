package dev.remake1.tchighwaysigns.registry;

import dev.remake1.tchighwaysigns.HighwaySigns;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {

    /**
     * Items that open the highway sign editor. Same (common) tag as TrafficCraft's wrench uses on both platforms.
     * TrafficCraft 1.1.x does not tag its wrench, so the addon adds it to this tag itself (see {@code data/c}).
     */
    public static final TagKey<Item> WRENCHES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));

    /** Additional blocks highway signs can be mounted on (besides TrafficCraft posts). */
    public static final TagKey<Block> PANEL_MOUNTS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(HighwaySigns.MOD_ID, "panel_mounts"));

    private ModTags() {}
}
