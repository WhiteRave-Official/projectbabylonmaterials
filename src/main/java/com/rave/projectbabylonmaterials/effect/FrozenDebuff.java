package com.rave.projectbabylonmaterials.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FrozenDebuff extends MobEffect {

    public FrozenDebuff() {
        super(MobEffectCategory.HARMFUL, 0x87CEEB);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }


}

