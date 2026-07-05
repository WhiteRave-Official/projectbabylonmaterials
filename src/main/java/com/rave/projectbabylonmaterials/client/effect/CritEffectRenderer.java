package com.rave.projectbabylonmaterials.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class CritEffectRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ProjectBabylonMaterials.MODID,
            "textures/effects/critical_strike_effect_animation.png"
    );
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);
    private static final int FRAME_COUNT = 6;
    private static final int FRAME_TIME = 2;
    private static final int MAX_AGE = FRAME_COUNT * FRAME_TIME;
    private static final float EFFECT_SCALE = 0.6F;
    private static final float EFFECT_HALF_SIZE = 0.5F;
    private static final List<CritEffectInstance> ACTIVE_EFFECTS = new LinkedList<>();

    private CritEffectRenderer() {
    }

    public static void spawn(double x, double y, double z) {
        ACTIVE_EFFECTS.add(new CritEffectInstance(x, y, z));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (ACTIVE_EFFECTS.isEmpty()) {
            return;
        }

        Iterator<CritEffectInstance> iterator = ACTIVE_EFFECTS.iterator();
        while (iterator.hasNext()) {
            CritEffectInstance instance = iterator.next();
            instance.age++;
            if (instance.age >= MAX_AGE) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || ACTIVE_EFFECTS.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = event.getCamera();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RENDER_TYPE);

        for (CritEffectInstance instance : ACTIVE_EFFECTS) {
            renderInstance(instance, camera, poseStack, vertexConsumer);
        }

        bufferSource.endBatch(RENDER_TYPE);
    }

    private static void renderInstance(CritEffectInstance instance, Camera camera, PoseStack poseStack, VertexConsumer vertexConsumer) {
        int frame = Math.min(FRAME_COUNT - 1, instance.age / FRAME_TIME);
        float minV = frame / (float) FRAME_COUNT;
        float maxV = (frame + 1) / (float) FRAME_COUNT;

        poseStack.pushPose();
        poseStack.translate(
                instance.x - camera.getPosition().x,
                instance.y - camera.getPosition().y,
                instance.z - camera.getPosition().z
        );
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-EFFECT_SCALE, -EFFECT_SCALE, EFFECT_SCALE);

        Matrix4f matrix = poseStack.last().pose();
        addVertex(vertexConsumer, matrix, -EFFECT_HALF_SIZE, -EFFECT_HALF_SIZE, 0.0F, minV);
        addVertex(vertexConsumer, matrix, -EFFECT_HALF_SIZE, EFFECT_HALF_SIZE, 0.0F, maxV);
        addVertex(vertexConsumer, matrix, EFFECT_HALF_SIZE, EFFECT_HALF_SIZE, 1.0F, maxV);
        addVertex(vertexConsumer, matrix, EFFECT_HALF_SIZE, -EFFECT_HALF_SIZE, 1.0F, minV);

        poseStack.popPose();
    }

    private static void addVertex(VertexConsumer vertexConsumer, Matrix4f matrix, float x, float y,
                                  float u, float v) {
        vertexConsumer.addVertex(matrix, x, y, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    private static final class CritEffectInstance {
        private final double x;
        private final double y;
        private final double z;
        private int age;

        private CritEffectInstance(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

