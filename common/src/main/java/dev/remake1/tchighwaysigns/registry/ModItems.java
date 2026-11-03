package dev.remake1.tchighwaysigns.registry;

import de.mrjulsen.trafficcraft.registry.ModCreativeModeTab;
import dev.architectury.extensions.injected.InjectedItemPropertiesExtension;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.remake1.tchighwaysigns.HighwaySigns;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(HighwaySigns.MOD_ID, Registries.ITEM);

    /** Listed in TrafficCraft's creative tab. */
    public static final RegistrySupplier<Item> HIGHWAY_SIGN = ITEMS.register("highway_sign",
        () -> new BlockItem(ModBlocks.HIGHWAY_SIGN.get(), ((InjectedItemPropertiesExtension) new Item.Properties()).arch$tab(ModCreativeModeTab.MOD_TAB)));

    private ModItems() {}

    public static void register() {
        ITEMS.register();
    }
}
