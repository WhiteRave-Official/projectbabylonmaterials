package com.rave.projectbabylonmaterials.client.hud;

import com.rave.projectbabylonmaterials.config.PBMClientConfig;
import com.rave.projectbabylonmaterials.hud.HudElementLayout;
import com.rave.projectbabylonmaterials.hud.HudElements;
import com.rave.projectbabylonmaterials.hud.HudLayout;

/**
 * Client-side holder for the synced HUD layout, and the resolver the overlay uses to place each element.
 *
 * <p>Resolution order per element: local client-config override (if enabled) → datapack value synced from the
 * server → hardcoded {@link HudElements} default.
 */
public final class ClientHudLayoutState {
    private static volatile HudLayout synced = HudElements.DEFAULT;

    private ClientHudLayoutState() {
    }

    /** Called from the HUD layout packet handler on the client thread. */
    public static void accept(HudLayout layout) {
        synced = layout;
    }

    /**
     * Resolves the final placement of an element given the screen size and the element's own (unscaled)
     * pixel size.
     */
    public static HudPlacement resolve(String elementId, int screenWidth, int screenHeight, int width, int height) {
        HudElementLayout base = synced.get(elementId).orElseGet(() -> HudElements.defaultFor(elementId));
        HudElementLayout layout = PBMClientConfig.applyHudOverride(elementId, base);

        int effectiveWidth = Math.round(width * layout.scale());
        int effectiveHeight = Math.round(height * layout.scale());

        int x = layout.anchor().x(screenWidth) + layout.offsetX() - Math.round(layout.alignX().factor() * effectiveWidth);
        int y = layout.anchor().y(screenHeight) + layout.offsetY() - Math.round(layout.alignY().factor() * effectiveHeight);

        return new HudPlacement(x, y, layout.scale(), layout.visible(), layout.shiftForOffhand());
    }
}
