package dev.remake1.tchighwaysigns.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.block.HighwaySignBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(HighwaySigns.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> HIGHWAY_SIGN = BLOCKS.register("highway_sign", HighwaySignBlock::new);

    private ModBlocks() {}

    public static void register() {
        BLOCKS.register();
    }
}
