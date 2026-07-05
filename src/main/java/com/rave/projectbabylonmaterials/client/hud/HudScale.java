package com.rave.projectbabylonmaterials.client.hud;

import net.minecraft.client.gui.GuiGraphics;

/** Applies a HUD element's scale about its top-left corner. Must be called inside an active {@code pushPose()}. */
public final class HudScale {
    private HudScale() {
    }

    public static void apply(GuiGraphics guiGraphics, int x, int y, float scale) {
        if (scale == 1.0F) {
            return;
        }
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.pose().translate(-x, -y, 0.0F);
    }
}
