package com.rave.projectbabylonmaterials.client.photon.gui;


final class ClassSelectionPhotonMotion {
    static final int SPARK_LIFETIME = 240;
    static final int TRAIL_POINTS = 24;
    static final float CAMERA_DISTANCE = 10000;

    private ClassSelectionPhotonMotion() {
    }

    static float sparkRadius(boolean central) {
        return central ? 1.8F : 1.4F;
    }

    static float sparkWidth(boolean central) {
        return 12;
    }

    static float sparkSpeed(boolean central) {
        return central ? 24 : 18;
    }

    static float sparkRail(float center, float bannerWidth, int side) {
        return center + (side == 0 ? -1 : 1) * (bannerWidth * 0.5F + 12);
    }

    static float columnX(float time, int column, int layer, float progress) {
        return (float) Math.sin(time * 0.014 + column * 1.7 + layer * 2.1 + progress * 2.2) * (layer == 0 ? 6 : 3);
    }

    static float columnWidth(float time, int column, int layer, float progress) {
        float breathe = 1 + (float) Math.sin(time * 0.018 + column + progress * 2.5) * 0.06F;
        float taper = 0.75F + 0.25F * (float) Math.sin(Math.PI * progress);
        return (layer == 0 ? 108 : 70) * breathe * taper;
    }

    static float columnAlpha(float time, int column, int layer, float progress) {
        float ends = (float) Math.pow(Math.sin(Math.PI * progress), 0.8);
        float wave = 0.82F + 0.18F * (float) Math.sin(progress * 7 - time * 0.04 + column + layer);
        return Math.max(0, ends * wave * (layer == 0 ? 0.18F : 0.13F));
    }

    static float crossSectionAlpha(float position) {
        float sine = (float) Math.sin(Math.PI * Math.max(0, Math.min(1, position)));
        return sine * sine;
    }
}
