package com.rave.projectbabylonmaterials.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.UUID;


public record BleedOwnerData(UUID ownerId, String dimension, boolean player, double spellPower, double bloodPower) {
    public static final String KEY = "project_babylon_materials.bleed_owner";

    public BleedOwnerData {
        Objects.requireNonNull(ownerId, "ownerId");
        if (dimension == null || dimension.isBlank() || ResourceLocation.tryParse(dimension) == null) throw new IllegalArgumentException("Invalid owner dimension");
        spellPower = validPower(spellPower);
        bloodPower = validPower(bloodPower);
    }

    public void writeTo(CompoundTag persistentData) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", ownerId);
        tag.putString("dimension", dimension);
        tag.putBoolean("player", player);
        tag.putDouble("spell_power", spellPower);
        tag.putDouble("blood_power", bloodPower);
        persistentData.put(KEY, tag);
    }

    public static BleedOwnerData readFrom(CompoundTag persistentData) {
        CompoundTag tag = persistentData.getCompound(KEY);
        if (!tag.hasUUID("owner") || tag.getString("dimension").isBlank() || ResourceLocation.tryParse(tag.getString("dimension")) == null) return null;
        return new BleedOwnerData(tag.getUUID("owner"), tag.getString("dimension"), tag.getBoolean("player"),
                tag.contains("spell_power", Tag.TAG_ANY_NUMERIC) ? tag.getDouble("spell_power") : 1.0,
                tag.contains("blood_power", Tag.TAG_ANY_NUMERIC) ? tag.getDouble("blood_power") : 1.0);
    }

    public static void recordApplication(CompoundTag persistentData, BleedOwnerData owner, boolean applied) {
        if (applied) owner.writeTo(persistentData);
    }

    public static void clear(CompoundTag persistentData) { persistentData.remove(KEY); }

    private static double validPower(double value) { return Double.isFinite(value) && value >= 0 ? value : 1.0; }
}
