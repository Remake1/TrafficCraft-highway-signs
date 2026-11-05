package dev.remake1.tchighwaysigns.data;

import java.util.Arrays;

/**
 * Horizontal alignment of a line of a highway sign.
 * The ids are stored in NBT and match the ids DragonLib's {@code ETextAlignment} used, so existing signs keep their alignment.
 */
public enum TextAlignment implements TranslatableEnum {
    LEFT(0, "left"),
    CENTER(1, "center"),
    RIGHT(2, "right");

    private final int id;
    private final String name;

    private TextAlignment(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTranslationGroup() {
        return "text_alignment";
    }

    public TextAlignment next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static TextAlignment getById(int id) {
        return Arrays.stream(values()).filter(x -> x.getId() == id).findFirst().orElse(LEFT);
    }
}
