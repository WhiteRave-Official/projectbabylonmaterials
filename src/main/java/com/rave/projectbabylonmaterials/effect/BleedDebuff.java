package com.rave.projectbabylonmaterials.effect;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedDebuff extends MobEffect {

    public BleedDebuff() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 100 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            DamageSource source = new DamageSource(
                    serverLevel.registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(ISSDamageTypes.BLOOD_MAGIC)
            );
            LivingEntity sourceEntity = entity.getLastHurtByMob();
            if (sourceEntity == null) {
                sourceEntity = entity;
            }
            double spellPower = sourceEntity.getAttributeValue(AttributeRegistry.SPELL_POWER);
            double bloodPower = sourceEntity.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER);
            float baseDamage = 1.0F * (amplifier + 1);
            float damage = (float) (baseDamage * spellPower * bloodPower);
            entity.hurt(source, damage);
        }
        return true;
    }
}
