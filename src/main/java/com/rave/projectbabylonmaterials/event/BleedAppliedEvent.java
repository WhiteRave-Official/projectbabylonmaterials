package com.rave.projectbabylonmaterials.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public final class BleedAppliedEvent extends Event {
    private final LivingEntity target;
    private final LivingEntity owner;
    private final MobEffectInstance effect;

    public BleedAppliedEvent(LivingEntity target, LivingEntity owner, MobEffectInstance effect) {
        this.target = target;
        this.owner = owner;
        this.effect = new MobEffectInstance(effect);
    }

    public LivingEntity target() { return target; }
    public LivingEntity owner() { return owner; }
    public MobEffectInstance effect() { return new MobEffectInstance(effect); }
}
