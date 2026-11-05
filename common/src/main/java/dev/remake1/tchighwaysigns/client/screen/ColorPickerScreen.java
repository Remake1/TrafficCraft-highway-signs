package dev.remake1.tchighwaysigns.client.screen;

import java.util.Locale;
import java.util.function.IntConsumer;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * A simple HSV color picker for opaque colors: a saturation/brightness square, a hue bar and RGB/hex input.
 * Opened on top of another screen and returns to it when closed. The picked color is only passed on when "Done" is pressed.
 */
public class ColorPickerScreen extends Screen {

    private static final String KEY = "gui.tchighwaysigns.color_picker.";

    private static final int PICKER_SIZE = 100;
    private static final int HUE_BAR_WIDTH = 12;
    private static final int PANEL_WIDTH = 230;
    private static final int PANEL_HEIGHT = 140;
    private static final int FIELD_LABEL_WIDTH = 26;
    private static final int FIELD_HEIGHT = 16;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PANEL_COLOR = 0xC0101010;
    private static final int PANEL_BORDER_COLOR = 0xFF505050;

    private enum DragTarget { NONE, SQUARE, HUE }

    private final Screen parent;
    private final int initialRgb;
    private final IntConsumer onPicked;

    // The color is kept as HSV so that the hue survives while saturation or brightness is 0.
    private float hue;
    private float saturation;
    private float brightness;

    private DragTarget dragTarget = DragTarget.NONE;
    /** Set while the text fields are updated from code, so that their responders do not feed the values back. */
    private boolean syncing = false;

    private EditBox redBox;
    private EditBox greenBox;
    private EditBox blueBox;
    private EditBox hexBox;

    /**
     * @param parent The screen to return to.
     * @param initialColor The color the picker starts with (alpha is ignored).
     * @param onPicked Receives the picked color as opaque ARGB when "Done" is pressed.
     */
    public ColorPickerScreen(Screen parent, int initialColor, IntConsumer onPicked) {
        super(Component.translatable(KEY + "title"));
        this.parent = parent;
        this.initialRgb = initialColor & 0xFFFFFF;
        this.onPicked = onPicked;
        setFromRgb(initialRgb);
    }

    public Screen getParent() {
        return parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int left() {
        return (width - PANEL_WIDTH) / 2;
    }

    private int pickerTop() {
        return (height - PANEL_HEIGHT) / 2 + 14;
    }

    private int hueBarX() {
        return left() + PICKER_SIZE + 6;
    }

    private int rightColumnX() {
        return hueBarX() + HUE_BAR_WIDTH + 8;
    }

    private int rightColumnWidth() {
        return left() + PANEL_WIDTH - rightColumnX();
    }

    @Override
    protected void init() {
        final int x = rightColumnX() + FIELD_LABEL_WIDTH;
        final int w = rightColumnWidth() - FIELD_LABEL_WIDTH;
        int y = pickerTop() + 26;

        redBox = addChannelBox(x, y, w, KEY + "red");
        greenBox = addChannelBox(x, y + 19, w, KEY + "green");
        blueBox = addChannelBox(x, y + 38, w, KEY + "blue");

        hexBox = new EditBox(font, x + 1, pickerTop() + PICKER_SIZE - FIELD_HEIGHT + 1, w - 2, FIELD_HEIGHT - 2, Component.translatable(KEY + "hex"));
        hexBox.setMaxLength(7);
        hexBox.setFilter(s -> s.matches("#?[0-9a-fA-F]{0,6}"));
        hexBox.setResponder(this::onHexChanged);
        addRenderableWidget(hexBox);

        final int buttonY = pickerTop() + PICKER_SIZE + 6;
        final int buttonWidth = PANEL_WIDTH / 2 - 2;
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> done()).bounds(left(), buttonY, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> onClose()).bounds(left() + PANEL_WIDTH - buttonWidth, buttonY, buttonWidth, 20).build());

        updateFields(null);
    }

    private EditBox addChannelBox(int x, int y, int w, String key) {
        EditBox box = new EditBox(font, x + 1, y + 1, w - 2, FIELD_HEIGHT - 2, Component.translatable(key));
        box.setMaxLength(3);
        box.setFilter(s -> s.matches("[0-9]{0,3}"));
        box.setResponder(s -> onChannelChanged(box));
        return addRenderableWidget(box);
    }

    // --- Color state ---

    private int currentRgb() {
        return Mth.hsvToRgb(hue, saturation, brightness) & 0xFFFFFF;
    }

    private void setFromRgb(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        brightness = max;
        if (max <= 0) {
            return; // Black: keep hue and saturation, so the markers do not jump.
        }
        saturation = delta / max;
        if (delta <= 0) {
            return; // Gray: keep hue.
        }
        float h;
        if (max == r) {
            h = (g - b) / delta;
        } else if (max == g) {
            h = (b - r) / delta + 2.0f;
        } else {
            h = (r - g) / delta + 4.0f;
        }
        hue = Mth.positiveModulo(h / 6.0f, 1.0f);
    }

    /** Writes the current color into all text fields except {@code source} (the one the user is typing in). */
    private void updateFields(EditBox source) {
        final int rgb = currentRgb();
        syncing = true;
        if (redBox != source) redBox.setValue(Integer.toString((rgb >> 16) & 0xFF));
        if (greenBox != source) greenBox.setValue(Integer.toString((rgb >> 8) & 0xFF));
        if (blueBox != source) blueBox.setValue(Integer.toString(rgb & 0xFF));
        if (hexBox != source) hexBox.setValue(String.format(Locale.ROOT, "#%06X", rgb));
        syncing = false;
    }

    private void onChannelChanged(EditBox source) {
        if (syncing) {
            return;
        }
        int r = parseChannel(redBox.getValue());
        int g = parseChannel(greenBox.getValue());
        int b = parseChannel(blueBox.getValue());
        if (r < 0 || g < 0 || b < 0) {
            return; // Incomplete input, wait for more.
        }
        setFromRgb((r << 16) | (g << 8) | b);
        updateFields(source);
    }

    private void onHexChanged(String value) {
        if (syncing) {
            return;
        }
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() != 6) {
            return;
        }
        setFromRgb(Integer.parseInt(hex, 16));
        updateFields(hexBox);
    }

    /** @return The channel value (0..255, clamped) or -1 if the text is empty. */
    private static int parseChannel(String text) {
        if (text.isEmpty()) {
            return -1;
        }
        return Mth.clamp(Integer.parseInt(text), 0, 255);
    }

    private void done() {
        onPicked.accept(0xFF000000 | currentRgb());
        onClose();
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    @Override
    public void tick() {
        super.tick();
        // Keep the parent alive, e.g. so that the sign editor still notices when its block is removed.
        if (parent != null) {
            parent.tick();
        }
    }

    // --- Input ---

    private boolean inSquare(double mouseX, double mouseY) {
        return mouseX >= left() && mouseX < left() + PICKER_SIZE && mouseY >= pickerTop() && mouseY < pickerTop() + PICKER_SIZE;
    }

    private boolean inHueBar(double mouseX, double mouseY) {
        return mouseX >= hueBarX() && mouseX < hueBarX() + HUE_BAR_WIDTH && mouseY >= pickerTop() && mouseY < pickerTop() + PICKER_SIZE;
    }

    private boolean inOldPreview(double mouseX, double mouseY) {
        int x = rightColumnX() + rightColumnWidth() / 2;
        return mouseX >= x && mouseX < rightColumnX() + rightColumnWidth() && mouseY >= pickerTop() && mouseY < pickerTop() + 20;
    }

    private boolean inNewPreview(double mouseX, double mouseY) {
        return mouseX >= rightColumnX() && mouseX < rightColumnX() + rightColumnWidth() / 2 && mouseY >= pickerTop() && mouseY < pickerTop() + 20;
    }

    private void drag(double mouseX, double mouseY) {
        switch (dragTarget) {
            case SQUARE -> {
                saturation = Mth.clamp((float)(mouseX - left()) / (PICKER_SIZE - 1), 0.0f, 1.0f);
                brightness = 1.0f - Mth.clamp((float)(mouseY - pickerTop()) / (PICKER_SIZE - 1), 0.0f, 1.0f);
            }
            case HUE -> hue = Mth.clamp((float)(mouseY - pickerTop()) / PICKER_SIZE, 0.0f, 0.999f);
            default -> {
                return;
            }
        }
        updateFields(null);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (inSquare(mouseX, mouseY)) {
                dragTarget = DragTarget.SQUARE;
            } else if (inHueBar(mouseX, mouseY)) {
                dragTarget = DragTarget.HUE;
            } else if (inOldPreview(mouseX, mouseY)) {
                setFromRgb(initialRgb);
                updateFields(null);
                return true;
            }
            if (dragTarget != DragTarget.NONE) {
                setFocused(null);
                drag(mouseX, mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragTarget != DragTarget.NONE) {
            drag(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragTarget = DragTarget.NONE;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            done();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // --- Rendering ---

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Draws the background (see below) and all widgets.
        super.render(graphics, mouseX, mouseY, partialTick);

        if (inOldPreview(mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.translatable(KEY + "old"), mouseX, mouseY);
        } else if (inNewPreview(mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.translatable(KEY + "new"), mouseX, mouseY);
        }
    }

    /** Draws everything behind the widgets. {@link Screen#render} calls this before it draws the widgets. */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        final int left = left();
        final int top = pickerTop();
        final int panelTop = top - 20;
        graphics.fill(left - 6, panelTop, left + PANEL_WIDTH + 6, top + PICKER_SIZE + 32, PANEL_COLOR);
        graphics.renderOutline(left - 6, panelTop, PANEL_WIDTH + 12, PICKER_SIZE + 52, PANEL_BORDER_COLOR);
        graphics.drawString(font, title, left, panelTop + 6, TEXT_COLOR);

        renderSquare(graphics, left, top);
        renderHueBar(graphics, hueBarX(), top);
        renderPreview(graphics, rightColumnX(), top, rightColumnWidth());

        final int labelX = rightColumnX();
        graphics.drawString(font, redBox.getMessage(), labelX, redBox.getY() + 3, TEXT_COLOR);
        graphics.drawString(font, greenBox.getMessage(), labelX, greenBox.getY() + 3, TEXT_COLOR);
        graphics.drawString(font, blueBox.getMessage(), labelX, blueBox.getY() + 3, TEXT_COLOR);
        graphics.drawString(font, hexBox.getMessage(), labelX, hexBox.getY() + 3, TEXT_COLOR);
    }

    private void renderSquare(GuiGraphics graphics, int x, int y) {
        // One column per pixel: fully saturated/bright at the top, black at the bottom.
        for (int i = 0; i < PICKER_SIZE; i++) {
            int topColor = 0xFF000000 | Mth.hsvToRgb(hue, i / (float)(PICKER_SIZE - 1), 1.0f);
            graphics.fillGradient(x + i, y, x + i + 1, y + PICKER_SIZE, topColor, 0xFF000000);
        }
        int markerX = x + Math.round(saturation * (PICKER_SIZE - 1));
        int markerY = y + Math.round((1.0f - brightness) * (PICKER_SIZE - 1));
        int markerColor = brightness > 0.6f && saturation < 0.4f ? 0xFF000000 : 0xFFFFFFFF;
        graphics.renderOutline(markerX - 2, markerY - 2, 5, 5, markerColor);
    }

    private void renderHueBar(GuiGraphics graphics, int x, int y) {
        for (int i = 0; i < PICKER_SIZE; i++) {
            graphics.fill(x, y + i, x + HUE_BAR_WIDTH, y + i + 1, 0xFF000000 | Mth.hsvToRgb(i / (float)PICKER_SIZE, 1.0f, 1.0f));
        }
        int markerY = y + Math.round(hue * PICKER_SIZE);
        graphics.renderOutline(x - 1, markerY - 1, HUE_BAR_WIDTH + 2, 3, 0xFFFFFFFF);
    }

    private void renderPreview(GuiGraphics graphics, int x, int y, int w) {
        int half = w / 2;
        graphics.fill(x, y, x + half, y + 20, 0xFF000000 | currentRgb());
        graphics.fill(x + half, y, x + w, y + 20, 0xFF000000 | initialRgb);
        graphics.renderOutline(x - 1, y - 1, w + 2, 22, PANEL_BORDER_COLOR);
    }
}
