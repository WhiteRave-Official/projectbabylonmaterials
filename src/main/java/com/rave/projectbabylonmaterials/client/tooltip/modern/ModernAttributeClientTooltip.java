package com.rave.projectbabylonmaterials.client.tooltip.modern;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

final class ModernAttributeClientTooltip implements ClientTooltipComponent {
    private static final Pattern MODIFIER_KEY = Pattern.compile("attribute\\.modifier\\.(plus|take|equals)\\.[012]");
    private static final Map<String, String> ICONS = Map.of(
            "attribute.name.generic.armor", "armor_icon",
            "attribute.name.generic.armor_toughness", "armor_toughness_icon",
            "attribute.name.project_babylon_materials.magic_armor", "magic_armor_icon");
    private final List<Component> label;
    private final Component value;
    private final ResourceLocation icon;
    private final int width;
    private final int height;

    private ModernAttributeClientTooltip(Font font, int width, Component name, Component value) {
        this.width = width;
        this.value = value;
        this.label = ModernTooltipDrawing.wrap(font, name, width - font.width(value) - 30);
        this.height = Math.max(17, label.size() * (font.lineHeight + 2) + 5);
        String iconName = name.getContents() instanceof TranslatableContents translation
                ? ICONS.get(translation.getKey()) : null;
        this.icon = iconName == null ? null : ResourceLocation.fromNamespaceAndPath("project_babylon_materials",
                "textures/gui/hud/" + iconName + ".png");
    }

    static ModernAttributeClientTooltip create(Font font, int width, Component line) {
        Component modifier = findModifier(line);
        if (modifier == null || !line.getString().strip().equals(modifier.plainCopy().getString().strip())) {
            return null;
        }
        TranslatableContents translation = (TranslatableContents) modifier.getContents();
        Object[] arguments = translation.getArgs();
        if (arguments.length != 2 || !(arguments[1] instanceof Component name)) {
            return null;
        }
        String key = translation.getKey();
        String amount = arguments[0] instanceof Component component ? component.getString() : String.valueOf(arguments[0]);
        boolean negative = key.contains(".take.");
        String sign = negative ? "-" : key.contains(".plus.") ? "+" : "";
        String unit = key.endsWith(".0") ? "" : "%";
        Component value = Component.literal(sign + amount + unit).withStyle(style ->
                style.withColor(negative ? ModernTooltipDrawing.NEGATIVE : ModernTooltipDrawing.POSITIVE));
        return new ModernAttributeClientTooltip(font, width, name, value);
    }

    private static Component findModifier(Component component) {
        if (component.getContents() instanceof TranslatableContents translation
                && MODIFIER_KEY.matcher(translation.getKey()).matches()) {
            return component;
        }
        for (Component sibling : component.getSiblings()) {
            Component found = findModifier(sibling);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        if (icon != null) {
            graphics.blit(icon, x, y + 2, 12, 12, 0, 0, 16, 16, 16, 16);
        }
        ModernTooltipDrawing.text(graphics, font, label, x + 18, y + 3, ModernTooltipDrawing.TEXT);
        graphics.drawString(font, value, x + width - font.width(value), y + 3, ModernTooltipDrawing.POSITIVE, false);
        graphics.fill(x, y + height - 1, x + width, y + height, 0x503A3E45);
    }
}
