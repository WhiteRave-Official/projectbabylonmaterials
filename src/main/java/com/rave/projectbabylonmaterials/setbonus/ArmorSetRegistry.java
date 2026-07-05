package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ArmorSetRegistry {

    private static final Map<String, ArmorSetDefinition> SETS_BY_ID = new LinkedHashMap<>();

    private ArmorSetRegistry() {
    }

    public static void register(ArmorSetDefinition set) {
        Objects.requireNonNull(set, "set");
        ArmorSetDefinition previous = SETS_BY_ID.putIfAbsent(set.getId(), set);
        if (previous != null) {
            throw new IllegalStateException("Armor set is already registered: " + set.getId());
        }
    }

    public static void registerAll(Collection<ArmorSetDefinition> sets) {
        for (ArmorSetDefinition set : sets) {
            register(set);
        }
    }

    public static void clear() {
        SETS_BY_ID.clear();
    }

    public static List<ArmorSetDefinition> getAll() {
        return List.copyOf(SETS_BY_ID.values());
    }

    public static ArmorSetDefinition findContaining(Item item) {
        for (ArmorSetDefinition set : SETS_BY_ID.values()) {
            if (set.contains(item)) {
                return set;
            }
        }
        return null;
    }

    public static ArmorSetDefinition findMatching(LivingEntity entity) {
        for (ArmorSetDefinition set : SETS_BY_ID.values()) {
            if (set.matches(entity)) {
                return set;
            }
        }
        return null;
    }

    public static ArmorSetDefinition findById(String setId) {
        return SETS_BY_ID.get(setId);
    }
}
