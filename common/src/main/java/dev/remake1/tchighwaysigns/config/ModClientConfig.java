package dev.remake1.tchighwaysigns.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModClientConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> TEXT_RENDER_DISTANCE;

    static {
        BUILDER.push("rendering");

        TEXT_RENDER_DISTANCE = BUILDER.comment("Max distance in blocks at which text and arrows of highway signs are rendered. The sign panel itself is always visible. (Default: 96)")
            .defineInRange("text_render_distance", 96, 16, 256);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private ModClientConfig() {}
}
