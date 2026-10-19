package com.rave.projectbabylonmaterials.client.tooltip.modern;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

final class AttributeTooltipIcons {
    static final int SIZE = 9;
    private static final Map<String, ResourceLocation> ICONS = AttributeIconNames.fileNames().stream()
            .collect(Collectors.toUnmodifiableMap(Function.identity(), file -> ResourceLocation.fromNamespaceAndPath(
                    "project_babylon_materials", "textures/tooltip/attribute_icons/" + file)));

    private AttributeTooltipIcons() {
    }

    static ResourceLocation find(Component name) {
        if (name.getContents() instanceof TranslatableContents translation) {
            String file = AttributeIconNames.fileName(translation.getKey());
            if (file != null) {
                return ICONS.get(file);
            }
        }
        for (Component sibling : name.getSiblings()) {
            ResourceLocation icon = find(sibling);
            if (icon != null) {
                return icon;
            }
        }
        return null;
    }
}
