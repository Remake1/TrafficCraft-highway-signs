package dev.remake1.tchighwaysigns.data;

import dev.remake1.tchighwaysigns.HighwaySigns;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * An enum whose values have a translated name under {@code enum.tchighwaysigns.<group>.<name>}.
 */
public interface TranslatableEnum {

    /** @return The group part of the translation key, e.g. {@code highway_arrow}. */
    String getTranslationGroup();

    /** @return The value part of the translation key, e.g. {@code up_left}. */
    String getName();

    default MutableComponent getValueTranslation() {
        return Component.translatable("enum." + HighwaySigns.MOD_ID + "." + getTranslationGroup() + "." + getName());
    }
}
