package com.rave.projectbabylonmaterials.hud;

import java.util.Map;

/**
 * Registry of the HUD element ids and their hardcoded default placements (which reproduce the original
 * fixed positions). These defaults are the fallback when no datapack provides a layout for an element.
 */
public final class HudElements {
    /** Armor / toughness / magic-armor box, anchored to the left of the hotbar by default. */
    public static final String STATS_BOX = "stats_box";
    /** Dragonsteel set passive icon, anchored to the right of the hotbar by default. */
    public static final String DRAGONSTEEL_PASSIVE = "dragonsteel_passive";

    // Defaults reproduce the previous hardcoded math:
    //   stats box: right edge at screenW/2 - (HOTBAR_HALF_WIDTH + HOTBAR_GAP) = -97, bottom edge at screenH - 8.
    //   passive:   left edge at screenW/2 + (HOTBAR_HALF_WIDTH + PASSIVE_HOTBAR_GAP) = +99, top edge at screenH - 19.
    public static final HudElementLayout STATS_BOX_DEFAULT =
            new HudElementLayout(HudAnchor.BOTTOM_CENTER, HudAlign.END, HudAlign.END, -97, -8, 1.0F, true, true);
    public static final HudElementLayout DRAGONSTEEL_PASSIVE_DEFAULT =
            new HudElementLayout(HudAnchor.BOTTOM_CENTER, HudAlign.START, HudAlign.START, 99, -19, 1.0F, true, false);

    public static final HudLayout DEFAULT = new HudLayout(Map.of(
            STATS_BOX, STATS_BOX_DEFAULT,
            DRAGONSTEEL_PASSIVE, DRAGONSTEEL_PASSIVE_DEFAULT
    ));

    private HudElements() {
    }

    public static HudElementLayout defaultFor(String id) {
        return DEFAULT.get(id).orElse(STATS_BOX_DEFAULT);
    }
}
