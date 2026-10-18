package com.rave.projectbabylonmaterials.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public final class BleedApplyingEvent extends Event {
    private final LivingEntity target;
    private final LivingEntity owner;
    private float chance;
    private int duration;
    private int amplifier;
    private int stackLimit;
    private int interval = 100;

    public BleedApplyingEvent(LivingEntity target, LivingEntity owner, float chance, int duration, int amplifier) {
        this.target = target;
        this.owner = owner;
        setChance(chance);
        setDuration(duration);
        setAmplifier(amplifier);
    }

    public LivingEntity target() { return target; }
    public LivingEntity owner() { return owner; }
    public float chance() { return chance; }
    public int duration() { return duration; }
    public int amplifier() { return amplifier; }
    public int stackLimit() { return stackLimit; }
    public int interval() { return interval; }
    public void setChance(float value) { chance = Float.isFinite(value) ? Math.max(0, Math.min(1, value)) : 0; }
    public void setDuration(int value) { duration = Math.max(0, value); }
    public void setAmplifier(int value) { amplifier = Math.max(0, value); }
    public void setStackLimit(int value) { stackLimit = Math.max(0, value); }
    public void setInterval(int value) { interval = Math.max(1, value); }
}
