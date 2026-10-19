package com.rave.projectbabylonmaterials.client.tooltip.epicfight;

public enum EpicFightTooltipAttribute {
    ARMOR_NEGATION("attribute.name.epicfight.armor_negation", "%"),
    IMPACT("attribute.name.epicfight.impact", "");

    private final String descriptionKey;
    private final String unit;

    EpicFightTooltipAttribute(String descriptionKey, String unit) {
        this.descriptionKey = descriptionKey;
        this.unit = unit;
    }

    public String descriptionKey() {
        return descriptionKey;
    }

    public String unit() {
        return unit;
    }

    public static EpicFightTooltipAttribute fromKey(String key) {
        for (var attribute : values()) {
            if (attribute.descriptionKey.equals(key) || (attribute.descriptionKey + ".value").equals(key)) {
                return attribute;
            }
        }
        return null;
    }
}
