package com.rave.projectbabylonmaterials.client.hud;

/** Resolved on-screen placement of a HUD element for the current frame. */
public record HudPlacement(int x, int y, float scale, boolean visible, boolean shiftForOffhand) {
}
