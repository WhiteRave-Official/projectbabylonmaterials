package com.rave.projectbabylonmaterials.event;

import com.rave.projectbabylonmaterials.effect.BleedOwnerData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public final class BleedDamageEvent extends Event {
    private final LivingEntity target;
    private final LivingEntity owner;
    private final BleedOwnerData ownerData;
    private final float damage;
    private final boolean detonation;

    public BleedDamageEvent(LivingEntity target, LivingEntity owner, BleedOwnerData ownerData, float damage, boolean detonation) {
        this.target = target;
        this.owner = owner;
        this.ownerData = ownerData;
        this.damage = damage;
        this.detonation = detonation;
    }

    public LivingEntity target() { return target; }
    public LivingEntity owner() { return owner; }
    public BleedOwnerData ownerData() { return ownerData; }
    public float damage() { return damage; }
    public boolean detonation() { return detonation; }
}
