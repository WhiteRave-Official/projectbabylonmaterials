package com.rave.projectbabylonmaterials.hud;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * How an element's own box aligns to its {@link HudAnchor} point along one axis.
 * {@code START} = left/top edge on the point, {@code CENTER} = centred, {@code END} = right/bottom edge on the point.
 */
public enum HudAlign implements StringRepresentable {
    START("start", 0.0F),
    CENTER("center", 0.5F),
    END("end", 1.0F);

    public static final Codec<HudAlign> CODEC = StringRepresentable.fromEnum(HudAlign::values);

    private final String name;
    private final float factor;

    HudAlign(String name, float factor) {
        this.name = name;
        this.factor = factor;
    }

    public float factor() {
        return factor;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
