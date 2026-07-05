package com.rave.projectbabylonmaterials.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedDebuff extends MobEffect {
    private static final String BLOOD_MAGIC_DAMAGE_TYPE = "irons_spellbooks:blood_magic";
    private static final String SPELL_POWER_ATTRIBUTE_ID = "irons_spellbooks:spell_power";
    private static final String BLOOD_SPELL_POWER_ATTRIBUTE_ID = "irons_spellbooks:blood_spell_power";

    public BleedDebuff() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 100 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            DamageSource source = IronSpellbooksEffectCompat.createDamageSource(serverLevel, BLOOD_MAGIC_DAMAGE_TYPE);
            if (source == null) {
                return;
            }

            LivingEntity sourceEntity = entity.getLastHurtByMob();
            if (sourceEntity == null) {
                sourceEntity = entity;
            }
            double spellPower = IronSpellbooksEffectCompat.getAttributeValue(sourceEntity, SPELL_POWER_ATTRIBUTE_ID, 1.0D);
            double bloodPower = IronSpellbooksEffectCompat.getAttributeValue(sourceEntity, BLOOD_SPELL_POWER_ATTRIBUTE_ID, 1.0D);
            float baseDamage = 1.0F * (amplifier + 1);
            float damage = (float)(baseDamage * spellPower * bloodPower);
            entity.hurt(source, damage);
        }
    }
}

