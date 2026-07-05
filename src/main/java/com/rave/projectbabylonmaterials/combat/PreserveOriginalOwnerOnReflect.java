package com.rave.projectbabylonmaterials.combat;

/**
 * Marker interface for projectiles that must keep their original owner when
 * reflected by external mechanics. This avoids breaking return, pickup, or
 * other owner-bound behavior.
 */
public interface PreserveOriginalOwnerOnReflect {
}
