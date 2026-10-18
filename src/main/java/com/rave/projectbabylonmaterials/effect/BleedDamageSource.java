package com.rave.projectbabylonmaterials.effect;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public final class BleedDamageSource extends DamageSource implements AutoCloseable {
    private LivingEntity target;
    private LivingDamageEvent damageEvent;
    private final boolean detonation;
    private boolean closed;

    public BleedDamageSource(DamageSource source, LivingEntity target, boolean detonation) {
        super(source.typeHolder());
        this.target = target;
        this.detonation = detonation;
    }

    public void capture(LivingDamageEvent event) {
        if (!closed && damageEvent == null && event.getSource() == this && event.getEntity() == target) damageEvent = event;
    }

    public float damage(boolean accepted) {
        return damageEvent == null ? 0 : BleedRules.acceptedDamage(damageEvent.getAmount(), accepted, damageEvent.isCanceled());
    }

    @Override
    public boolean is(TagKey<DamageType> tag) {
        return detonation && DamageTypeTags.BYPASSES_COOLDOWN.equals(tag) || super.is(tag);
    }

    @Override
    public void close() {
        closed = true;
        damageEvent = null;
        target = null;
    }
}
