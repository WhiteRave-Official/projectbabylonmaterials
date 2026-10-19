package com.rave.projectbabylonmaterials.client.tooltip.modern;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class AttributeIconNames {
    private static final Map<String, String> ICONS = createIcons();

    private AttributeIconNames() {
    }

    static String fileName(String descriptionKey) {
        String key = descriptionKey.endsWith(".value") ? descriptionKey.substring(0, descriptionKey.length() - 6) : descriptionKey;
        return ICONS.get(key);
    }

    static Set<String> fileNames() {
        return Set.copyOf(ICONS.values());
    }

    private static Map<String, String> createIcons() {
        Map<String, String> icons = new HashMap<>();
        icons.put("attribute.name.generic.armor", "armor_icon.png");
        icons.put("attribute.name.generic.armor_toughness", "armor_toughness_icon.png");
        icons.put("attribute.name.generic.max_health", "max_health_icon.png");
        icons.put("attribute.name.generic.movement_speed", "movement_speed_icon.png");
        icons.put("attribute.name.generic.attack_speed", "attack_speed_icon.png");
        icons.put("attribute.name.generic.attack_damage", "attack_damage_icon.png");
        icons.put("damage_source.epicfight.damage", "attack_damage_icon.png");
        icons.put("attribute.name.epicfight.weight", "weight_icon.png");
        icons.put("attribute.name.epicfight.stun_armor", "stun_armor_icon.png");
        icons.put("attribute.name.epicfight.staminar", "max_stamina_icon.png");
        icons.put("attribute.name.epicfight.stamina_regen", "stamina_regen_icon.png");
        icons.put("attribute.name.epicfight.impact", "impact_icon.png");
        icons.put("attribute.name.epicfight.armor_negation", "armor_negation_icon.png");
        icons.put("attribute.name.epicfight.max_strikes", "max_strikes_icon.png");
        icons.put("attribute.name.epicfight.offhand_attack_speed", "attack_speed_icon.png");
        icons.put("attribute.name.epicfight.offhand_impact", "impact_icon.png");
        icons.put("attribute.name.epicfight.offhand_armor_negation", "armor_negation_icon.png");
        icons.put("attribute.name.epicfight.offhand_max_strikes", "max_strikes_icon.png");
        icons.put("attribute.name.project_babylon_materials.magic_armor", "magic_armor_icon.png");
        icons.put("attribute.name.project_babylon_materials.crit_chance", "crit_chance_icon.png");
        icons.put("attribute.name.project_babylon_materials.crit_damage", "crit_damage_icon.png");
        icons.put("attribute.name.project_babylon_materials.healing_strength", "healing_strength_icon.png");
        icons.put("attribute.name.project_babylon_materials.lifesteal", "lifesteal_icon.png");
        icons.put("attribute.name.project_babylon_materials.ranged_draw_speed", "bow_pull_time_speed_icon.png");
        icons.put("attribute.irons_spellbooks.max_mana", "max_mana_icon.png");
        icons.put("attribute.irons_spellbooks.mana_regen", "mana_regen_icon.png");
        icons.put("attribute.irons_spellbooks.spell_power", "spell_power_icon.png");
        icons.put("attribute.irons_spellbooks.spell_resist", "magic_armor_icon.png");
        icons.put("attribute.irons_spellbooks.cooldown_reduction", "spell_cooldown_reduction_icon.png");
        icons.put("attribute.irons_spellbooks.cast_time_reduction", "spell_cast_speed_icon.png");
        for (String school : new String[]{"fire", "ice", "lightning", "holy", "ender", "blood", "evocation", "nature", "eldritch"}) {
            String iconSchool = school.equals("eldritch") ? "eldrich" : school;
            icons.put("attribute.irons_spellbooks." + school + "_spell_power", iconSchool + "_spell_power_icon.png");
            icons.put("attribute.irons_spellbooks." + school + "_magic_resist", iconSchool + "_spell_resist_icon.png");
        }
        return Map.copyOf(icons);
    }
}
