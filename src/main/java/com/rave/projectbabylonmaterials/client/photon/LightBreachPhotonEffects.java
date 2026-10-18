package com.rave.projectbabylonmaterials.client.photon;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static com.rave.projectbabylonmaterials.client.photon.PhotonEmitterFactory.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonMath.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonRenderPolicy.*;
import static com.rave.projectbabylonmaterials.client.photon.PhotonTextures.*;

final class LightBreachPhotonEffects {

    private LightBreachPhotonEffects() {}

    static void preparation(Entity caster, int age) {
        if (!(caster.level() instanceof ClientLevel level) || age % 3 != 0
                || !shouldRenderTransientEffect(caster, age)) return;
        StaticLevelEffect effect = new StaticLevelEffect(level);
        int count = photonCount(5);
        for (int i = 0; i < count; i++) {
            double angle = age * 0.16D + i * Mth.TWO_PI / count;
            double radius = 0.7D + (i % 3) * 0.4D;
            Vec3 position = caster.position().add(Math.cos(angle) * radius, 0.12D, Math.sin(angle) * radius);
            createTrailEmitterNoBloom(i % 3 == 0 ? LIGHT_MEDIUM_TEXTURE : LIGHT_SMALL_TEXTURE,
                    i % 3 == 0 ? 0.20F : 0.12F, photonLifetime(15), 0xFFFFE6A3,
                    -Math.cos(angle) * 0.01D, 0.07D + (i % 3) * 0.015D,
                    -Math.sin(angle) * 0.01D, (i & 1) == 0 ? 20.0F : -20.0F)
                    .emmit(effect, toVector(position), IDENTITY_ROTATION, UNIT_SCALE);
        }
    }

    static LightBreachColumnVisual createColumn(Entity controller, Vec3 base, float radius, float height) {
        if (!(controller.level() instanceof ClientLevel level)) {
            throw new IllegalArgumentException("Light Breach visuals require a client level");
        }
        return new LightBreachColumnVisual(level, base, radius, height);
    }
}
