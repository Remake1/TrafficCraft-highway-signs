package dev.remake1.tchighwaysigns.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.block.entity.HighwaySignBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(HighwaySigns.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<HighwaySignBlockEntity>> HIGHWAY_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES.register("highway_sign_block_entity",
        () -> BlockEntityType.Builder.of(HighwaySignBlockEntity::new, ModBlocks.HIGHWAY_SIGN.get()).build(null));

    private ModBlockEntities() {}

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
