package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.init.PBAttributes;
import com.rave.projectbabylonmaterials.network.client.ClientboundCritEffectPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class CritDamageHandler {
    private static final String LAST_CRIT_TICK_KEY = "project_babylon_materials.last_crit_tick";
    private static final String LAST_CRIT_TARGET_KEY = "project_babylon_materials.last_crit_target";
    private static final String LAST_CRIT_PENDING_KEY = "project_babylon_materials.last_crit_pending";
    private static final String LAST_CRIT_DAMAGE_KEY = "project_babylon_materials.last_crit_damage";

    private CritDamageHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getNewDamage() <= 0.0F || event.getEntity().level().isClientSide) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        double critChance = getAttributeValueSafely(attacker, PBAttributes.CRIT_CHANCE);
        if (critChance <= 0.0D) {
            return;
        }
        if (attacker.getRandom().nextDouble() >= critChance) {
            return;
        }
        double critDamage = getAttributeValueSafely(attacker, PBAttributes.CRIT_DAMAGE);
        if (critDamage <= 0.0D) {
            return;
        }
        float finalDamage = (float) (event.getNewDamage() * (1.0D + critDamage));
        event.setNewDamage(finalDamage);
        markCriticalHit(attacker, event.getEntity(), finalDamage);
        spawnCritFeedback(event.getEntity(), attacker);
    }

    private static void markCriticalHit(LivingEntity attacker, LivingEntity target, float finalDamage) {
        attacker.getPersistentData().putLong(LAST_CRIT_TICK_KEY, attacker.level().getGameTime());
        attacker.getPersistentData().putUUID(LAST_CRIT_TARGET_KEY, target.getUUID());
        attacker.getPersistentData().putBoolean(LAST_CRIT_PENDING_KEY, true);
        attacker.getPersistentData().putFloat(LAST_CRIT_DAMAGE_KEY, finalDamage);
    }

    private static void spawnCritFeedback(LivingEntity target, LivingEntity attacker) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.65D;
        double z = target.getZ();
        serverLevel.sendParticles(ParticleTypes.CRIT, x, y, z, 14, 0.3D, 0.4D, 0.3D, 0.15D);
        double popupX = x + (target.getRandom().nextDouble() - 0.5D) * 0.6D;
        double popupY = target.getY() + target.getBbHeight() + 0.45D;
        double popupZ = z + (target.getRandom().nextDouble() - 0.5D) * 0.6D;
        ClientboundCritEffectPacket packet = new ClientboundCritEffectPacket(popupX, popupY, popupZ);
        PacketDistributor.sendToPlayersNear(serverLevel, null, popupX, popupY, popupZ, 32.0D, packet);
    }

    private static double getAttributeValueSafely(LivingEntity entity, Holder<Attribute> attribute) {
        if (!entity.getAttributes().hasAttribute(attribute)) {
            return 0.0D;
        }
        return entity.getAttributeValue(attribute);
    }
}
