package com.rave.projectbabylonmaterials.client.tooltip.epicfight;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.regex.Pattern;

public final class EpicFightTooltipLines {
    private static final Pattern NUMBER = Pattern.compile("[+-]?[0-9][0-9.,]*(?:\\s*%)?");
    private static final Pattern MODIFIER = Pattern.compile("attribute\\.modifier\\.(plus|take|equals)\\.[012]");

    private EpicFightTooltipLines() {
    }

    public static Value parse(Component line) {
        Component nativeValue = findValue(line);
        if (nativeValue != null && line.getString().strip().equals(nativeValue.plainCopy().getString().strip())) {
            var translation = (TranslatableContents) nativeValue.getContents();
            var attribute = EpicFightTooltipAttribute.fromKey(translation.getKey());
            Object argument = translation.getArgs()[0];
            String amount = argument instanceof Component component ? component.getString() : String.valueOf(argument);
            return new Value(attribute, Component.literal(amount + attribute.unit()));
        }
        String prefix = line.plainCopy().getString().strip();
        if (NUMBER.matcher(prefix).matches() && line.getSiblings().size() == 1) {
            Component label = line.getSiblings().get(0);
            if (label.getContents() instanceof TranslatableContents translation && translation.getArgs().length == 0
                    && label.getSiblings().isEmpty()) {
                var attribute = EpicFightTooltipAttribute.fromKey(translation.getKey());
                if (attribute != null && translation.getKey().equals(attribute.descriptionKey())) {
                    return new Value(attribute, Component.literal(prefix));
                }
            }
        }
        return null;
    }

    public static boolean isWeaponStat(Component line) {
        if (parse(line) != null) {
            return true;
        }
        if (line.getContents() instanceof TranslatableContents translation
                && MODIFIER.matcher(translation.getKey()).matches() && translation.getArgs().length == 2
                && translation.getArgs()[1] instanceof Component name
                && name.getContents() instanceof TranslatableContents attribute
                && EpicFightTooltipAttribute.fromKey(attribute.getKey()) != null
                && line.getString().strip().equals(line.plainCopy().getString().strip())) {
            return true;
        }
        if (line.plainCopy().getString().isBlank() && line.getSiblings().size() == 1) {
            return isWeaponStat(line.getSiblings().get(0));
        }
        return false;
    }

    public static boolean containsKey(Component line, String key) {
        if (line.getContents() instanceof TranslatableContents translation) {
            if (translation.getKey().equals(key)) {
                return true;
            }
            for (Object argument : translation.getArgs()) {
                if (argument instanceof Component component && containsKey(component, key)) {
                    return true;
                }
            }
        }
        return line.getSiblings().stream().anyMatch(sibling -> containsKey(sibling, key));
    }

    private static Component findValue(Component line) {
        if (line.getContents() instanceof TranslatableContents translation
                && EpicFightTooltipAttribute.fromKey(translation.getKey()) != null
                && translation.getKey().endsWith(".value") && translation.getArgs().length == 1) {
            return line;
        }
        for (Component sibling : line.getSiblings()) {
            Component found = findValue(sibling);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public record Value(EpicFightTooltipAttribute attribute, Component amount) {
        public Component label() {
            return Component.translatable(attribute.descriptionKey());
        }
    }
}
