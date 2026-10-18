package com.rave.projectbabylonmaterials.client.tooltip.modern;

public final class ModernTooltipLayout {
    private ModernTooltipLayout() {
    }

    public static Fit fit(int width, int height, int screenWidth, int screenHeight) {
        int availableWidth = Math.max(1, screenWidth - 24);
        int availableHeight = Math.max(1, screenHeight - 24);
        float scale = Math.min(1.0F, Math.min((float) availableWidth / Math.max(1, width),
                (float) availableHeight / Math.max(1, height)));
        return new Fit(scale, Math.min(availableWidth, (int) Math.ceil(width * (double) scale)),
                Math.min(availableHeight, (int) Math.ceil(height * (double) scale)));
    }

    public static int contentWidth(int screenWidth) {
        return Math.max(144, Math.min(254, screenWidth - 40));
    }

    public record Fit(float scale, int width, int height) {
    }
}
