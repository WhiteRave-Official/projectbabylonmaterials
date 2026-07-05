package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.init.PBMEffects;
import com.rave.projectbabylonmaterials.network.client.ClientboundShadowFormAfterimagePacket;
import com.rave.projectbabylonmaterials.network.client.ClientboundShadowFormStatePacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID)
public class ShadowFormEffect extends MobEffect {
    private static final String CONCEALED_KEY = "project_babylon_materials.shadow_form_concealed";
    private static final String RESTORE_TICK_KEY = "project_babylon_materials.shadow_form_restore_tick";
    private static final String NEXT_AFTERIMAGE_TICK_KEY = "project_babylon_materials.shadow_form_afterimage_tick";
    private static final int REVEAL_DURATION_TICKS = 100;
    private static final int AFTERIMAGE_INTERVAL_TICKS = 100;
    private static final int IN_SHADOWS_DURATION_TICKS = 60;
    private static final long STATE_RESYNC_INTERVAL_TICKS = 20L;

    public ShadowFormEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x241A38);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return true;
        }

        long gameTime = entity.level().getGameTime();
        if (isConcealed(entity)) {
            refreshInShadows(entity);
            if ((gameTime % STATE_RESYNC_INTERVAL_TICKS) == 0L) {
                syncConcealment(entity, true);
            }

            long nextAfterimageTick = entity.getPersistentData().getLong(NEXT_AFTERIMAGE_TICK_KEY);
            if (nextAfterimageTick <= 0L) {
                entity.getPersistentData().putLong(NEXT_AFTERIMAGE_TICK_KEY, gameTime + AFTERIMAGE_INTERVAL_TICKS);
            } else if (gameTime >= nextAfterimageTick) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new ClientboundShadowFormAfterimagePacket(entity.getId()));
                entity.getPersistentData().putLong(NEXT_AFTERIMAGE_TICK_KEY, gameTime + AFTERIMAGE_INTERVAL_TICKS);
            }
            return true;
        }

        long restoreTick = entity.getPersistentData().getLong(RESTORE_TICK_KEY);
        if (restoreTick > 0L && gameTime < restoreTick && (gameTime % STATE_RESYNC_INTERVAL_TICKS) == 0L) {
            syncConcealment(entity, false);
        }
        if (restoreTick > 0L && gameTime >= restoreTick) {
            setConcealed(entity, true, gameTime);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (!event.getEffectInstance().is(PBMEffects.SHADOW_FORM) || event.getEntity().level().isClientSide()) {
            return;
        }

        setConcealed(event.getEntity(), true, event.getEntity().level().getGameTime());
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffectInstance() == null || !event.getEffectInstance().is(PBMEffects.SHADOW_FORM) || event.getEntity().level().isClientSide()) {
            return;
        }

        clearShadowFormState(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0.0F) {
            return;
        }

        LivingEntity target = event.getEntity();
        if (target.hasEffect(PBMEffects.SHADOW_FORM)) {
            breakConcealment(target);
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker && attacker.hasEffect(PBMEffects.SHADOW_FORM)) {
            breakConcealment(attacker);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) {
            return;
        }

        if (!entity.hasEffect(PBMEffects.SHADOW_FORM) && hasShadowFormState(entity)) {
            clearShadowFormState(entity);
        }
    }

    public static boolean isConcealed(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(CONCEALED_KEY);
    }

    private static void breakConcealment(LivingEntity entity) {
        if (entity.level().isClientSide() || !entity.hasEffect(PBMEffects.SHADOW_FORM)) {
            return;
        }

        long gameTime = entity.level().getGameTime();
        entity.getPersistentData().putLong(RESTORE_TICK_KEY, gameTime + REVEAL_DURATION_TICKS);
        entity.getPersistentData().putLong(NEXT_AFTERIMAGE_TICK_KEY, 0L);

        if (isConcealed(entity)) {
            entity.getPersistentData().putBoolean(CONCEALED_KEY, false);
            syncConcealment(entity, false);
        }
    }

    private static void setConcealed(LivingEntity entity, boolean concealed, long gameTime) {
        entity.getPersistentData().putBoolean(CONCEALED_KEY, concealed);
        if (concealed) {
            entity.getPersistentData().remove(RESTORE_TICK_KEY);
            entity.getPersistentData().putLong(NEXT_AFTERIMAGE_TICK_KEY, gameTime + AFTERIMAGE_INTERVAL_TICKS);
            refreshInShadows(entity);
        } else {
            entity.getPersistentData().putLong(RESTORE_TICK_KEY, gameTime + REVEAL_DURATION_TICKS);
            entity.getPersistentData().putLong(NEXT_AFTERIMAGE_TICK_KEY, 0L);
        }

        syncConcealment(entity, concealed);
    }

    private static void refreshInShadows(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(PBMEffects.IN_SHADOWS, IN_SHADOWS_DURATION_TICKS, 0, false, false, true));
    }

    private static boolean hasShadowFormState(LivingEntity entity) {
        return entity.getPersistentData().contains(CONCEALED_KEY)
                || entity.getPersistentData().contains(RESTORE_TICK_KEY)
                || entity.getPersistentData().contains(NEXT_AFTERIMAGE_TICK_KEY);
    }

    private static void clearShadowFormState(LivingEntity entity) {
        entity.getPersistentData().remove(CONCEALED_KEY);
        entity.getPersistentData().remove(RESTORE_TICK_KEY);
        entity.getPersistentData().remove(NEXT_AFTERIMAGE_TICK_KEY);
        entity.removeEffect(PBMEffects.IN_SHADOWS);
        syncConcealment(entity, false);
    }

    private static void syncConcealment(LivingEntity entity, boolean concealed) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new ClientboundShadowFormStatePacket(entity.getId(), concealed));
    }
}
