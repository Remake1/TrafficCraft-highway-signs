package dev.remake1.tchighwaysigns.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;

/**
 * All user editable content of a highway sign.
 */
public class HighwaySignData {

    public static final int MIN_WIDTH = 1;
    public static final int MAX_WIDTH = 8;
    public static final int MIN_HEIGHT = 1;
    public static final int MAX_HEIGHT = 4;
    public static final int LINES_PER_BLOCK = 3;
    public static final int MAX_LANES = MAX_WIDTH;
    public static final float DEFAULT_LANE_SCALE = 1.5f;

    private static final String NBT_WIDTH = "Width";
    private static final String NBT_HEIGHT = "Height";
    private static final String NBT_BACKGROUND = "Background";
    private static final String NBT_TEXT_COLOR = "TextColor";
    private static final String NBT_BORDER = "Border";
    private static final String NBT_LINES = "Lines";
    private static final String NBT_LANE_ARROWS = "LaneArrows";
    private static final String NBT_LANE_SCALE = "LaneScale";

    private int width;
    private int height;
    private int backgroundColor;
    private int textColor;
    private boolean border;
    private final List<HighwaySignLine> lines = new ArrayList<>();
    /** One arrow per lane, shown evenly distributed in a row at the bottom of the sign. */
    private final List<HighwayArrow> laneArrows = new ArrayList<>();
    private float laneArrowScale = DEFAULT_LANE_SCALE;

    public HighwaySignData() {
        this.width = 1;
        this.height = 1;
        this.backgroundColor = HighwaySignColorPresets.GREEN;
        this.textColor = HighwaySignColorPresets.WHITE;
        this.border = true;
    }

    public HighwaySignData copy() {
        HighwaySignData data = new HighwaySignData();
        data.width = width;
        data.height = height;
        data.backgroundColor = backgroundColor;
        data.textColor = textColor;
        data.border = border;
        lines.forEach(x -> data.lines.add(x.copy()));
        data.laneArrows.addAll(laneArrows);
        data.laneArrowScale = laneArrowScale;
        return data;
    }

    /** Replaces all content and styles of this sign with a copy of {@code other}. */
    public void set(HighwaySignData other) {
        this.width = other.width;
        this.height = other.height;
        this.backgroundColor = other.backgroundColor;
        this.textColor = other.textColor;
        this.border = other.border;
        this.lines.clear();
        other.lines.forEach(x -> this.lines.add(x.copy()));
        this.laneArrows.clear();
        this.laneArrows.addAll(other.laneArrows);
        this.laneArrowScale = other.laneArrowScale;
    }

    public static int getMaxLines(int height) {
        return height * LINES_PER_BLOCK;
    }

    public int getMaxLines() {
        return getMaxLines(height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = Mth.clamp(width, MIN_WIDTH, MAX_WIDTH);
        this.height = Mth.clamp(height, MIN_HEIGHT, MAX_HEIGHT);
        trimLines();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor | 0xFF000000;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor | 0xFF000000;
    }

    public boolean hasBorder() {
        return border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    /** @return The mutable list of lines. */
    public List<HighwaySignLine> getLines() {
        return lines;
    }

    public boolean canAddLine() {
        return lines.size() < getMaxLines();
    }

    /** @return The mutable list of lane arrows. {@link HighwayArrow#NONE} is an empty lane. */
    public List<HighwayArrow> getLaneArrows() {
        return laneArrows;
    }

    public boolean canAddLane() {
        return laneArrows.size() < MAX_LANES;
    }

    public float getLaneArrowScale() {
        return laneArrowScale;
    }

    public void setLaneArrowScale(float scale) {
        this.laneArrowScale = Float.isNaN(scale) ? DEFAULT_LANE_SCALE : Mth.clamp(scale, HighwaySignLine.MIN_SCALE, HighwaySignLine.MAX_SCALE);
    }

    private void trimLines() {
        while (lines.size() > getMaxLines()) {
            lines.remove(lines.size() - 1);
        }
    }

    /** Clamps all values into their valid ranges. Must be called on data received from clients. */
    public void sanitize() {
        setSize(width, height);
        backgroundColor |= 0xFF000000;
        textColor |= 0xFF000000;
        lines.forEach(HighwaySignLine::sanitize);
        trimLines();
        laneArrows.replaceAll(x -> x == null ? HighwayArrow.NONE : x);
        while (laneArrows.size() > MAX_LANES) {
            laneArrows.remove(laneArrows.size() - 1);
        }
        setLaneArrowScale(laneArrowScale);
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putByte(NBT_WIDTH, (byte)width);
        nbt.putByte(NBT_HEIGHT, (byte)height);
        nbt.putInt(NBT_BACKGROUND, backgroundColor);
        nbt.putInt(NBT_TEXT_COLOR, textColor);
        nbt.putBoolean(NBT_BORDER, border);
        ListTag list = new ListTag();
        lines.forEach(x -> list.add(x.toNbt()));
        nbt.put(NBT_LINES, list);
        if (!laneArrows.isEmpty()) {
            byte[] ids = new byte[laneArrows.size()];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = (byte)laneArrows.get(i).getId();
            }
            nbt.putByteArray(NBT_LANE_ARROWS, ids);
            nbt.putFloat(NBT_LANE_SCALE, laneArrowScale);
        }
        return nbt;
    }

    public static HighwaySignData fromNbt(CompoundTag nbt) {
        HighwaySignData data = new HighwaySignData();
        data.width = nbt.contains(NBT_WIDTH) ? nbt.getByte(NBT_WIDTH) : 1;
        data.height = nbt.contains(NBT_HEIGHT) ? nbt.getByte(NBT_HEIGHT) : 1;
        if (nbt.contains(NBT_BACKGROUND)) data.backgroundColor = nbt.getInt(NBT_BACKGROUND);
        if (nbt.contains(NBT_TEXT_COLOR)) data.textColor = nbt.getInt(NBT_TEXT_COLOR);
        if (nbt.contains(NBT_BORDER)) data.border = nbt.getBoolean(NBT_BORDER);
        ListTag list = nbt.getList(NBT_LINES, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            data.lines.add(HighwaySignLine.fromNbt(list.getCompound(i)));
        }
        for (byte id : nbt.getByteArray(NBT_LANE_ARROWS)) {
            data.laneArrows.add(HighwayArrow.getById(id));
        }
        if (nbt.contains(NBT_LANE_SCALE)) data.laneArrowScale = nbt.getFloat(NBT_LANE_SCALE);
        data.sanitize();
        return data;
    }
}
