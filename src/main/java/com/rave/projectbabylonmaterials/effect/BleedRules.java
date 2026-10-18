package com.rave.projectbabylonmaterials.effect;

public final class BleedRules {
    private BleedRules() { }

    public static float acceptedDamage(float amount, boolean accepted, boolean canceled) {
        return accepted && !canceled && Float.isFinite(amount) && amount > 0 ? amount : 0;
    }

    public static int amplifier(int requested, int existing, int limit) {
        return limit > 0 ? Math.min(limit - 1, Math.max(requested, existing + 1)) : requested;
    }

    public static int remainingTicks(int duration, int interval) {
        return duration > 0 && interval > 0 ? duration / interval : 0;
    }

    public static boolean shouldTick(int duration, int interval) {
        return duration > 0 && interval > 0 && duration % interval == 0;
    }
}
