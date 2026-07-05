package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.network.chat.Component;

public enum ArmorSetBonusType {
    MATERIAL("tooltip.project_babylon_materials.material_passive"),
    CLASS("tooltip.project_babylon_materials.class_passive");

    private final String translationKey;

    ArmorSetBonusType(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getTitle() {
        return Component.translatable(translationKey);
    }

    public String getTranslationKey() {
        return translationKey;
    }
}

