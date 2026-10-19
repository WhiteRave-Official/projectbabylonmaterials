package com.rave.projectbabylonmaterials.effect;

import com.rave.projectbabylonmaterials.init.PBMEffects;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.event.BleedApplyingEvent;
import com.rave.projectbabylonmaterials.event.BleedAppliedEvent;
import com.rave.projectbabylonmaterials.event.BleedDamageEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;


public final class Bleeding {
    private static final String SPELL_POWER = "irons_spellbooks:spell_power";
    private static final String BLOOD_POWER = "irons_spellbooks:blood_spell_power";
    private static final String INTERVAL = "project_babylon_materials.bleed_interval";

    private Bleeding() { }

    public static boolean apply(LivingEntity target, LivingEntity owner, int durationTicks, int amplifier) {
        return tryApply(target, owner, 1.0F, durationTicks, amplifier);
    }

    public static boolean tryApply(LivingEntity target, LivingEntity owner, float chance, int durationTicks, int amplifier) {
        if (target.level().isClientSide() || owner == null || durationTicks <= 0 || amplifier < 0) return false;
        var event = new BleedApplyingEvent(target, owner, chance, durationTicks, amplifier);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.duration() <= 0 || event.chance() <= 0 || (event.chance() < 1 && owner.getRandom().nextFloat() >= event.chance())) return false;
        var current = target.getEffect(PBMEffects.BLEED_DEBUFF.get());
        int level = BleedRules.amplifier(event.amplifier(), current == null ? -1 : current.getAmplifier(), event.stackLimit());
        var record = new BleedOwnerData(owner.getUUID(), owner.level().dimension().location().toString(), owner instanceof Player,
                power(owner, SPELL_POWER), power(owner, BLOOD_POWER));
        boolean applied = target.addEffect(new MobEffectInstance(PBMEffects.BLEED_DEBUFF.get(), event.duration(), level), owner);
        ProjectBabylonMaterials.LOGGER.debug("BLEED_APPLY tick={} target={} owner={} duration={} amplifier={} applied={}",
                target.level().getGameTime(), target.getUUID(), owner.getUUID(), event.duration(), level, applied);

        BleedOwnerData.recordApplication(target.getPersistentData(), record, applied);
        if (applied) {
            target.getPersistentData().putInt(INTERVAL, event.interval());
            var active = target.getEffect(PBMEffects.BLEED_DEBUFF.get());
            if (active != null) MinecraftForge.EVENT_BUS.post(new BleedAppliedEvent(target, owner, active));
        }
        return applied;
    }

    public static int interval(LivingEntity target) {
        int value = target.getPersistentData().getInt(INTERVAL);
        return value > 0 ? value : 100;
    }

    public static void clearSchedule(LivingEntity target) { target.getPersistentData().remove(INTERVAL); }

    public static void tick(LivingEntity target, int amplifier) {
        var effect = target.getEffect(PBMEffects.BLEED_DEBUFF.get());
        if (effect != null && (effect.isInfiniteDuration()
                ? target.tickCount % interval(target) == 0 : BleedRules.shouldTick(effect.getDuration(), interval(target)))) {
            dealDamage(target, tickDamage(target, amplifier), false, getOwnerData(target), resolveOwner(target));
        }
    }

    public static boolean detonate(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel)) return false;
        var effect = target.getEffect(PBMEffects.BLEED_DEBUFF.get());
        if (effect == null || effect.isInfiniteDuration()) {
            ProjectBabylonMaterials.LOGGER.debug("BLEED_DETONATE_SKIP target={} reason={}",
                    target.getUUID(), effect == null ? "no_effect" : "infinite_duration");
            return false;
        }
        var record = getOwnerData(target);
        var owner = resolveOwner(target, record);
        float damage = tickDamage(target, effect.getAmplifier()) * BleedRules.remainingTicks(effect.getDuration(), interval(target));
        ProjectBabylonMaterials.LOGGER.debug("BLEED_DETONATE_BEGIN target={} owner={} duration={} amplifier={} interval={} requested={}",
                target.getUUID(), record == null ? null : record.ownerId(), effect.getDuration(), effect.getAmplifier(), interval(target), damage);
        boolean removed = target.removeEffect(PBMEffects.BLEED_DEBUFF.get());
        ProjectBabylonMaterials.LOGGER.debug("BLEED_DETONATE_REMOVE target={} removed={} remaining={}",
                target.getUUID(), removed, target.hasEffect(PBMEffects.BLEED_DEBUFF.get()));
        if (!removed) return false;
        if (damage > 0 && target.isAlive()) dealDamage(target, damage, true, record, owner);
        return true;
    }

    private static void dealDamage(LivingEntity target, float damage, boolean detonation, BleedOwnerData record, LivingEntity owner) {
        if (!(target.level() instanceof ServerLevel level) || !Float.isFinite(damage) || damage <= 0) return;
        DamageSource source = IronSpellbooksEffectCompat.createDamageSource(level, "irons_spellbooks:blood_magic");
        if (source == null) return;
        float dealt;
        boolean accepted;
        try (var bleedSource = new BleedDamageSource(source, target, detonation)) {
            accepted = target.hurt(bleedSource, damage);
            dealt = bleedSource.damage(accepted);
        }
        ProjectBabylonMaterials.LOGGER.debug("BLEED_DAMAGE target={} owner={} requested={} accepted={} dealt={} detonation={}",
                target.getType(), record == null ? null : record.ownerId(), damage, accepted, dealt, detonation);
        if (dealt > 0) MinecraftForge.EVENT_BUS.post(new BleedDamageEvent(target, owner, record, dealt, detonation));
    }

    public static BleedOwnerData getOwnerData(LivingEntity target) {
        return target.hasEffect(PBMEffects.BLEED_DEBUFF.get()) ? BleedOwnerData.readFrom(target.getPersistentData()) : null;
    }

    public static UUID getOwnerId(LivingEntity target) {
        var record = getOwnerData(target);
        return record == null ? null : record.ownerId();
    }

    public static LivingEntity resolveOwner(LivingEntity target) {
        var record = getOwnerData(target);
        return resolveOwner(target, record);
    }

    private static LivingEntity resolveOwner(LivingEntity target, BleedOwnerData record) {
        if (record == null || !(target.level() instanceof ServerLevel level)) return null;
        if (record.player()) {
            var player = level.getServer().getPlayerList().getPlayer(record.ownerId());
            return player != null && player.isAlive() ? player : null;
        }
        var ownerLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(record.dimension())));
        Entity owner = ownerLevel == null ? null : ownerLevel.getEntity(record.ownerId());
        if (owner instanceof LivingEntity living && living.isAlive()) return living;

        for (ServerLevel other : level.getServer().getAllLevels()) {
            if (other == ownerLevel) continue;
            owner = other.getEntity(record.ownerId());
            if (owner instanceof LivingEntity living && living.isAlive()) return living;
        }
        return null;
    }

    public static float tickDamage(LivingEntity target, int amplifier) {
        var record = getOwnerData(target);
        var owner = resolveOwner(target, record);
        double spellPower = owner != null ? power(owner, SPELL_POWER) : record != null ? record.spellPower() : 1.0;
        double bloodPower = owner != null ? power(owner, BLOOD_POWER) : record != null ? record.bloodPower() : 1.0;
        return (float)((amplifier + 1.0) * spellPower * bloodPower);
    }

    private static double power(LivingEntity entity, String attribute) {
        double value = IronSpellbooksEffectCompat.getAttributeValue(entity, attribute, 1.0);
        return Double.isFinite(value) && value >= 0 ? value : 1.0;
    }
}
