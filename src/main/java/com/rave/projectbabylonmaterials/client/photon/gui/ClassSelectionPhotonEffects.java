package com.rave.projectbabylonmaterials.client.photon.gui;

import com.lowdragmc.photon.client.gameobject.emitter.data.RendererSetting;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction3;
import com.lowdragmc.photon.client.gameobject.emitter.data.material.TextureMaterial;
import com.lowdragmc.photon.client.gameobject.emitter.data.shape.Box;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleConfig;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.lowdragmc.lowdraglib.utils.GradientColor;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.color.Gradient;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;


final class ClassSelectionPhotonEffects {
    private static final ResourceLocation SPARK = ResourceLocation.fromNamespaceAndPath(
            ProjectBabylonMaterials.MODID, "textures/particles/light_particle_big.png");

    private ClassSelectionPhotonEffects() {
    }

    static ParticleEmitter sparks(int limit, boolean central) {
        ParticleEmitter emitter = base(SPARK, limit);
        emitter.setName("class_selection_sparks");
        ParticleConfig config = emitter.config;
        config.setStartLifetime(NumberFunction.constant(ClassSelectionPhotonMotion.SPARK_LIFETIME));
        float radius = ClassSelectionPhotonMotion.sparkRadius(central);
        config.setStartSize(new NumberFunction3(radius, radius, 1));
        config.emission.setEmissionRate(NumberFunction.constant(limit / (float) ClassSelectionPhotonMotion.SPARK_LIFETIME));
        config.shape.setShape(new Box());
        config.shape.setScale(new NumberFunction3(ClassSelectionPhotonMotion.sparkWidth(central), 240, 0));
        config.velocityOverLifetime.setEnable(true);
        config.velocityOverLifetime.setLinear(new NumberFunction3(0, ClassSelectionPhotonMotion.sparkSpeed(central), 0));
        config.colorOverLifetime.setEnable(true);
        Gradient fade = new Gradient();
        fade.getGradientColor().deserializeNBT(new GradientColor(0xFFFFFFFF, 0x00FFFFFF).serializeNBT());
        config.colorOverLifetime.setColor(fade);
        return emitter;
    }

    private static ParticleEmitter base(ResourceLocation texture, int limit) {
        ParticleEmitter emitter = new ParticleEmitter();
        ParticleConfig config = emitter.config;
        config.setDuration(200);
        config.setLooping(true);
        config.setMaxParticles(limit);
        config.setStartSpeed(NumberFunction.constant(0));
        config.setStartSize(new NumberFunction3(1, 1, 1));
        config.setStartColor(NumberFunction.color(0xFFFFFFFF));
        config.setParallelUpdate(false);
        config.setParallelRendering(false);
        config.physics.setEnable(false);
        config.material.setCull(false);
        config.material.setDepthMask(false);
        config.material.setDepthTest(false);
        config.material.setMaterial(new TextureMaterial(texture));
        config.renderer.setLayer(RendererSetting.Layer.Opaque);
        config.renderer.setBloomEffect(false);
        config.renderer.getCull().setEnable(false);
        config.lights.setEnable(true);
        config.lights.setSkyLight(NumberFunction.constant(15));
        config.lights.setBlockLight(NumberFunction.constant(15));
        return emitter;
    }
}
