package com.rave.projectbabylonmaterials.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Datapack-driven placement of a single HUD element.
 *
 * <p>Final position = {@code anchor} point on the screen, plus {@code offsetX/offsetY} pixels, minus the
 * element's own (scaled) size times the {@code alignX/alignY} factor. All fields are optional in JSON and
 * default to the neutral values below.
 */
public record HudElementLayout(
        HudAnchor anchor,
        HudAlign alignX,
        HudAlign alignY,
        int offsetX,
        int offsetY,
        float scale,
        boolean visible,
        boolean shiftForOffhand
) {
    public static final Codec<HudElementLayout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HudAnchor.CODEC.optionalFieldOf("anchor", HudAnchor.BOTTOM_CENTER).forGetter(HudElementLayout::anchor),
            HudAlign.CODEC.optionalFieldOf("align_x", HudAlign.START).forGetter(HudElementLayout::alignX),
            HudAlign.CODEC.optionalFieldOf("align_y", HudAlign.START).forGetter(HudElementLayout::alignY),
            Codec.INT.optionalFieldOf("offset_x", 0).forGetter(HudElementLayout::offsetX),
            Codec.INT.optionalFieldOf("offset_y", 0).forGetter(HudElementLayout::offsetY),
            Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(HudElementLayout::scale),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(HudElementLayout::visible),
            Codec.BOOL.optionalFieldOf("shift_for_offhand", false).forGetter(HudElementLayout::shiftForOffhand)
    ).apply(instance, HudElementLayout::new));

    /** Returns a copy with the position-relevant fields replaced (used by the client-config override). */
    public HudElementLayout withPlacement(HudAnchor newAnchor, int newOffsetX, int newOffsetY, float newScale, boolean newVisible) {
        return new HudElementLayout(newAnchor, alignX, alignY, newOffsetX, newOffsetY, newScale, newVisible, shiftForOffhand);
    }
}
