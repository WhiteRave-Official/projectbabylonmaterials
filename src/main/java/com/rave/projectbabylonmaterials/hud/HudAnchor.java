package com.rave.projectbabylonmaterials.hud;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * A reference point on the screen, expressed as a fraction of the screen width/height.
 * Combined with an element's {@link HudAlign} and pixel offset to place it.
 */
public enum HudAnchor implements StringRepresentable {
    TOP_LEFT("top_left", 0.0F, 0.0F),
    TOP_CENTER("top_center", 0.5F, 0.0F),
    TOP_RIGHT("top_right", 1.0F, 0.0F),
    CENTER_LEFT("center_left", 0.0F, 0.5F),
    CENTER("center", 0.5F, 0.5F),
    CENTER_RIGHT("center_right", 1.0F, 0.5F),
    BOTTOM_LEFT("bottom_left", 0.0F, 1.0F),
    BOTTOM_CENTER("bottom_center", 0.5F, 1.0F),
    BOTTOM_RIGHT("bottom_right", 1.0F, 1.0F);

    public static final Codec<HudAnchor> CODEC = StringRepresentable.fromEnum(HudAnchor::values);

    private final String name;
    private final float xFactor;
    private final float yFactor;

    HudAnchor(String name, float xFactor, float yFactor) {
        this.name = name;
        this.xFactor = xFactor;
        this.yFactor = yFactor;
    }

    public int x(int screenWidth) {
        return Math.round(xFactor * screenWidth);
    }

    public int y(int screenHeight) {
        return Math.round(yFactor * screenHeight);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
