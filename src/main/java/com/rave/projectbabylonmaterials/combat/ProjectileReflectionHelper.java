package com.rave.projectbabylonmaterials.combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;

public final class ProjectileReflectionHelper {
    private ProjectileReflectionHelper() {
    }

    public static boolean shouldPreserveOriginalOwner(Projectile projectile) {
        return projectile instanceof ThrownTrident
                || projectile instanceof PreserveOriginalOwnerOnReflect;
    }

    public static void assignReflectedOwner(Projectile projectile, Entity originalOwner, LivingEntity reflector) {
        projectile.setOwner(shouldPreserveOriginalOwner(projectile) ? originalOwner : reflector);
    }
}
