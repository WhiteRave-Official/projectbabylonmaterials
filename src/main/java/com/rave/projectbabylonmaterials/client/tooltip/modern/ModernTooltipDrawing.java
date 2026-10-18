package com.rave.projectbabylonmaterials.client.tooltip.modern;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ModernTooltipDrawing {
    public static final int BACKGROUND = 0xF5101113;
    public static final int PANEL = 0xFF191B1F;
    public static final int BORDER = 0xFF454A52;
    public static final int TEXT = 0xEEE9DF;
    public static final int MUTED = 0xA0A5AD;
    public static final int ACCENT = 0xFFBD55;
    public static final int POSITIVE = 0x6EDDD8;
    public static final int NEGATIVE = 0xF17E78;

    private ModernTooltipDrawing() {
    }

    public static List<Component> wrap(Font font, FormattedText text, int width) {
        List<Component> result = new ArrayList<>();
        for (FormattedText line : font.getSplitter().splitLines(text, Math.max(1, width), Style.EMPTY)) {
            MutableComponent component = Component.empty();
            line.visit((style, value) -> {
                component.append(Component.literal(value).withStyle(style));
                return Optional.empty();
            }, Style.EMPTY);
            result.add(component);
        }
        return result;
    }

    public static void outline(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void divider(GuiGraphics graphics, int x, int y, int width) {
        graphics.fill(x, y, x + width, y + 1, BORDER);
    }

    public static int text(GuiGraphics graphics, Font font, List<Component> lines, int x, int y, int color) {
        for (Component line : lines) {
            graphics.drawString(font, line, x, y, color, false);
            y += font.lineHeight + 2;
        }
        return y;
    }
}
