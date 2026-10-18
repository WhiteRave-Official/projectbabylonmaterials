package com.rave.projectbabylonmaterials.client.tooltip.modern;

import com.rave.projectbabylonmaterials.setbonus.ArmorSetTooltipData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ArmorItem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ModernItemClientTooltip implements ClientTooltipComponent {
    private static final String PREFIX = "tooltip.project_babylon_materials.modern.";
    private final ModernItemTooltipData data;
    private final List<Component> title;
    private final Component subtitle;
    private final List<Row> rows = new ArrayList<>();
    private final List<Hint> hints = new ArrayList<>();
    private final ModernTooltipLayout.Fit fit;
    private final int width;
    private final int height;
    private final int durabilityY;
    private final int hintsY;

    public ModernItemClientTooltip(ModernItemTooltipData data) {
        this.data = data;
        Font font = Minecraft.getInstance().font;
        int contentWidth = ModernTooltipLayout.contentWidth(data.screenWidth());
        FormattedText name = data.stack().getHoverName();
        boolean firstLineIsTitle = !data.elements().isEmpty() && data.elements().get(0).left().isPresent();
        if (firstLineIsTitle) {
            name = data.elements().get(0).left().get();
        }
        title = ModernTooltipDrawing.wrap(font, name, contentWidth - 36);
        subtitle = data.stack().getItem() instanceof ArmorItem armor
                ? Component.translatable(PREFIX + "slot." + armor.getEquipmentSlot().getName())
                : Component.literal(BuiltInRegistries.ITEM.getKey(data.stack().getItem()).getNamespace());
        int y = Math.max(28, title.size() * (font.lineHeight + 2) + font.lineHeight + 3) + 16;
        int measuredWidth = contentWidth;
        Set<String> hintKeys = new LinkedHashSet<>();
        for (int i = firstLineIsTitle ? 1 : 0; i < data.elements().size(); i++) {
            var element = data.elements().get(i);
            ClientTooltipComponent child;
            if (element.left().isPresent()) {
                FormattedText line = element.left().get();
                String hint = hintKey(line);
                if (hint != null) {
                    hintKeys.add(hint);
                    continue;
                }
                if (line instanceof Component component && isTranslation(component, "item.durability")
                        && data.stack().isDamageableItem()) {
                    continue;
                }
                if (line.getString().isBlank()) {
                    if (!rows.isEmpty() && rows.get(rows.size() - 1).child() != null) {
                        rows.add(new Row(null, y));
                        y += 4;
                    }
                    continue;
                }
                child = line instanceof Component component
                        ? ModernAttributeClientTooltip.create(font, contentWidth, component) : null;
                if (child == null) {
                    child = new TextRow(font, line, contentWidth);
                }
            } else {
                var component = element.right().orElseThrow();
                if (component instanceof ArmorSetTooltipData) {
                    hintKeys.add("shift");
                }
                child = ClientTooltipComponent.create(component);
            }
            rows.add(new Row(child, y));
            measuredWidth = Math.max(measuredWidth, child.getWidth(font));
            y += child.getHeight() + 2;
        }
        durabilityY = data.stack().isDamageableItem() ? y + 4 : -1;
        if (durabilityY >= 0) {
            y = durabilityY + 21;
        }
        for (String key : hintKeys) {
            hints.add(new Hint(key.equals("shift") ? "SHIFT" : key.equals("alt") ? "ALT" : "CTRL",
                    Component.translatable(PREFIX + "hint." + key)));
        }
        hintsY = hints.isEmpty() ? -1 : y + 5;
        int hintHeight = hints.isEmpty() ? 0 : hintRows(font, measuredWidth) * 18 + 6;
        width = measuredWidth + 16;
        height = y + hintHeight + 8;
        fit = ModernTooltipLayout.fit(width, height, data.screenWidth(), data.screenHeight());
    }

    private static String hintKey(FormattedText text) {
        String line = text.getString().strip();
        if (line.equals(Component.translatable("tooltip.project_babylon_materials.hold_shift").getString().strip())) {
            return "shift";
        }
        for (String mod : List.of("project_babylon_jewelry", "project_babylon_materials")) {
            if (line.equals(Component.translatable("tooltip." + mod + ".hold_alt_gems").getString().strip())) {
                return "alt";
            }
            if (line.equals(Component.translatable("tooltip." + mod + ".hold_ctrl_enchants").getString().strip())) {
                return "ctrl";
            }
        }
        return null;
    }

    private static boolean isTranslation(Component line, String key) {
        if (line.getContents() instanceof TranslatableContents contents && contents.getKey().equals(key)) {
            return line.getString().strip().equals(line.plainCopy().getString().strip());
        }
        return line.getSiblings().stream().anyMatch(sibling -> line.getString().strip().equals(sibling.getString().strip())
                && isTranslation(sibling, key));
    }

    private int hintRows(Font font, int availableWidth) {
        int count = 1;
        int x = 0;
        for (Hint hint : hints) {
            int hintWidth = font.width(hint.key()) + font.width(hint.label()) + 16;
            if (x > 0 && x + hintWidth > availableWidth) {
                count++;
                x = 0;
            }
            x += hintWidth + 8;
        }
        return count;
    }

    @Override
    public int getHeight() {
        return fit.height();
    }

    @Override
    public int getWidth(Font font) {
        return fit.width();
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.flush();
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(fit.scale(), fit.scale(), 1);
            graphics.fill(-2, 2, width + 2, height + 2, 0x50000000);
            graphics.fill(0, 0, width, height, ModernTooltipDrawing.BACKGROUND);
            ModernTooltipDrawing.outline(graphics, 0, 0, width, height, ModernTooltipDrawing.BORDER);
            graphics.fill(0, 0, 28, 1, 0xFFFFBD55);
            graphics.fill(0, 0, 1, 10, 0xFFFFBD55);
            graphics.fill(width - 28, height - 1, width, height, 0xFFFFBD55);
            graphics.fill(width - 1, height - 10, width, height, 0xFFFFBD55);
            ModernTooltipDrawing.outline(graphics, 8, 8, 26, 26, ModernTooltipDrawing.BORDER);
            graphics.renderItem(data.stack(), 13, 13);
            int subtitleY = ModernTooltipDrawing.text(graphics, font, title, 40, 9, ModernTooltipDrawing.TEXT);
            graphics.drawString(font, subtitle, 40, subtitleY + 1, ModernTooltipDrawing.MUTED, false);
            int headerBottom = Math.max(28, title.size() * (font.lineHeight + 2) + font.lineHeight + 3) + 12;
            ModernTooltipDrawing.divider(graphics, 8, headerBottom, width - 16);
            for (Row row : rows) {
                if (row.child() == null) {
                    continue;
                }
                graphics.flush();
                row.child().renderText(font, 8, row.y(), graphics.pose().last().pose(), graphics.bufferSource());
                row.child().renderImage(font, 8, row.y(), graphics);
            }
            if (durabilityY >= 0) {
                renderDurability(graphics, font);
            }
            if (hintsY >= 0) {
                renderHints(graphics, font);
            }
            graphics.flush();
        } finally {
            graphics.pose().popPose();
        }
    }

    private void renderDurability(GuiGraphics graphics, Font font) {
        ModernTooltipDrawing.divider(graphics, 8, durabilityY, width - 16);
        int remaining = data.stack().getMaxDamage() - data.stack().getDamageValue();
        Component value = Component.literal(remaining + " / " + data.stack().getMaxDamage());
        Component label = Component.translatable(PREFIX + "durability");
        graphics.drawString(font, label, 8, durabilityY + 5, ModernTooltipDrawing.MUTED, false);
        graphics.drawString(font, value, width - 8 - font.width(value), durabilityY + 5, ModernTooltipDrawing.TEXT, false);
        int barWidth = width - 16;
        graphics.fill(8, durabilityY + 17, width - 8, durabilityY + 20, ModernTooltipDrawing.PANEL);
        int filled = Math.max(0, Math.min(barWidth, Math.round(barWidth * data.stack().getBarWidth() / 13.0F)));
        graphics.fill(8, durabilityY + 17, 8 + filled, durabilityY + 20, 0xFF000000 | data.stack().getBarColor());
    }

    private void renderHints(GuiGraphics graphics, Font font) {
        ModernTooltipDrawing.divider(graphics, 8, hintsY, width - 16);
        int x = 8;
        int y = hintsY + 6;
        for (Hint hint : hints) {
            int keyWidth = font.width(hint.key()) + 8;
            int hintWidth = keyWidth + font.width(hint.label()) + 8;
            if (x > 8 && x + hintWidth > width - 8) {
                x = 8;
                y += 18;
            }
            graphics.fill(x, y, x + keyWidth, y + 13, ModernTooltipDrawing.PANEL);
            ModernTooltipDrawing.outline(graphics, x, y, keyWidth, 13, ModernTooltipDrawing.BORDER);
            graphics.drawString(font, hint.key(), x + 4, y + 2, ModernTooltipDrawing.ACCENT, false);
            graphics.drawString(font, hint.label(), x + keyWidth + 4, y + 2, ModernTooltipDrawing.MUTED, false);
            x += hintWidth + 8;
        }
    }

    private record Row(ClientTooltipComponent child, int y) {
    }

    private record Hint(String key, Component label) {
    }

    private static final class TextRow implements ClientTooltipComponent {
        private final List<Component> lines;
        private final int width;
        private final int height;

        private TextRow(Font font, FormattedText text, int width) {
            this.lines = ModernTooltipDrawing.wrap(font, text, width);
            this.width = width;
            this.height = lines.size() * (font.lineHeight + 2);
        }

        @Override
        public int getWidth(Font font) {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
            ModernTooltipDrawing.text(graphics, font, lines, x, y, ModernTooltipDrawing.TEXT);
        }
    }
}
