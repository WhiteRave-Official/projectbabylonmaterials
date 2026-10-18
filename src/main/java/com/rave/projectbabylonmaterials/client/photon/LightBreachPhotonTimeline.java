package com.rave.projectbabylonmaterials.client.photon;

import com.lowdragmc.lowdraglib.utils.GradientColor;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.Gradient;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.curve.Curve;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.curve.ECBCurves;


final class LightBreachPhotonTimeline {
    private LightBreachPhotonTimeline() {}

    static Curve size(int fadeIn, int active, int fadeOut, float minimum) {
        float total = fadeIn + active + fadeOut;
        float in = fadeIn / total;
        float out = (fadeIn + active) / total;
        Curve curve = new Curve(0, 1, 0, 1, 1, "lifetime", "size");

        curve.setCurves(new ECBCurves(
                0, minimum, in / 3, minimum, in * 2 / 3, 1, in, 1,
                in, 1, in + (out - in) / 3, 1, in + (out - in) * 2 / 3, 1, out, 1,
                out, 1, out + (1 - out) / 3, 1, out + (1 - out) * 2 / 3, minimum, 1, minimum));
        return curve;
    }

    static Gradient opacity(int fadeIn, int active, int fadeOut) {
        float total = fadeIn + active + fadeOut;
        Gradient gradient = new Gradient();
        GradientColor colors = new GradientColor(0x00FFFFFF, 0x00FFFFFF);
        colors.addAlpha(fadeIn / total, 1);
        colors.addAlpha((fadeIn + active) / total, 1);
        gradient.getGradientColor().deserializeNBT(colors.serializeNBT());
        return gradient;
    }
}
