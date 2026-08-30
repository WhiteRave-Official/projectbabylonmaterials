package com.rave.projectbabylonmaterials.client.photon;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PBMPhotonEffectHelper {
    private PBMPhotonEffectHelper() {
    }

    public static void startArclightAwakening(Entity entity) { PhotonPersistentEffects.startArclightAwakening(entity); }
    public static void burstArclightAwakening(Entity entity) { PhotonPersistentEffects.burstArclightAwakening(entity); }
    public static void stopArclightAwakening(Entity entity) { PhotonPersistentEffects.stopArclightAwakening(entity); }
    public static void startDragonDescendCast(Entity entity) { PhotonPersistentEffects.startDragonDescendCast(entity); }
    public static void burstDragonDescendCast(Entity entity) { PhotonPersistentEffects.burstDragonDescendCast(entity); }
    public static void stopDragonDescendCast(Entity entity) { PhotonPersistentEffects.stopDragonDescendCast(entity); }
    public static void startGlacierCast(Entity entity) { PhotonPersistentEffects.startGlacierCast(entity); }
    public static void stopGlacierCast(Entity entity) { PhotonPersistentEffects.stopGlacierCast(entity); }
    public static void startFireStormCast(Entity entity) { PhotonPersistentEffects.startFireStormCast(entity); }
    public static void burstFireStormCast(Entity entity) { PhotonPersistentEffects.burstFireStormCast(entity); }
    public static void stopFireStormCast(Entity entity) { PhotonPersistentEffects.stopFireStormCast(entity); }
    public static void startMagicalVeil(Entity entity) { PhotonPersistentEffects.startMagicalVeil(entity); }
    public static void stopMagicalVeil(Entity entity) { PhotonPersistentEffects.stopMagicalVeil(entity); }
    public static void startBastionFrostAura(Entity entity, float radiusBlocks) { PhotonPersistentEffects.startBastionFrostAura(entity, radiusBlocks); }
    public static void stopBastionFrostAura(Entity entity) { PhotonPersistentEffects.stopBastionFrostAura(entity); }
    public static void startBastionRuleAura(Entity entity, float radiusBlocks) { PhotonPersistentEffects.startBastionRuleAura(entity, radiusBlocks); }
    public static void stopBastionRuleAura(Entity entity) { PhotonPersistentEffects.stopBastionRuleAura(entity); }
    public static void startBastionHeavensGiftAura(Entity entity, float radiusBlocks) { PhotonPersistentEffects.startBastionHeavensGiftAura(entity, radiusBlocks); }
    public static void stopBastionHeavensGiftAura(Entity entity) { PhotonPersistentEffects.stopBastionHeavensGiftAura(entity); }
    public static void startArmorIceAura(Entity entity, float radiusBlocks) { PhotonPersistentEffects.startArmorIceAura(entity, radiusBlocks); }
    public static void stopArmorIceAura(Entity entity) { PhotonPersistentEffects.stopArmorIceAura(entity); }
    public static void startArmorNetheriteFireRing(Entity entity, float radiusBlocks) { PhotonPersistentEffects.startArmorNetheriteFireRing(entity, radiusBlocks); }
    public static void stopArmorNetheriteFireRing(Entity entity) { PhotonPersistentEffects.stopArmorNetheriteFireRing(entity); }

    public static void spawnArmorDragonsteelRebirth(Entity entity) { PhotonAmbientEffects.spawnArmorDragonsteelRebirth(entity); }
    public static void spawnShadowFormTransition(Entity entity, boolean entering) { PhotonAmbientEffects.spawnShadowFormTransition(entity, entering); }
    public static void spawnSpectralBurst(Entity entity) { PhotonAmbientEffects.spawnSpectralBurst(entity); }
    public static void spawnSpectralFlightTrail(Entity entity, Vec3 movement) { PhotonAmbientEffects.spawnSpectralFlightTrail(entity, movement); }
    public static void spawnStormArrowFlight(Entity entity, Vec3 movement) { PhotonAmbientEffects.spawnStormArrowFlight(entity, movement); }
    public static void spawnStormArrowShot(Entity entity) { PhotonAmbientEffects.spawnStormArrowShot(entity); }
    public static void spawnFireStorm(Entity entity, float progress, float height, float radius, int tickCount) { PhotonAmbientEffects.spawnFireStorm(entity, progress, height, radius, tickCount); }
    public static void startBlessingCast(Entity entity) { PhotonAmbientEffects.startBlessingCast(entity); }
    public static void burstBlessingCast(Entity entity) { PhotonAmbientEffects.burstBlessingCast(entity); }
    public static void stopBlessingCast(Entity entity) { PhotonAmbientEffects.stopBlessingCast(entity); }
    public static void spawnGlacierContactWave(Entity entity) { PhotonAmbientEffects.spawnGlacierContactWave(entity); }
    public static void spawnBlessingHealPulse(Entity entity) { PhotonAmbientEffects.spawnBlessingHealPulse(entity); }
    public static void spawnBlessingAbsorptionPulse(Entity entity) { PhotonAmbientEffects.spawnBlessingAbsorptionPulse(entity); }
    public static void spawnAbsorptionShield(LivingEntity entity, float progress, int tick, float absorptionAmount) { PhotonAmbientEffects.spawnAbsorptionShield(entity, progress, tick, absorptionAmount); }

    public static void spawnDragonDescendFlight(Entity projectile, Vec3 movement) { PhotonProjectileEffects.spawnDragonDescendFlight(projectile, movement); }
    public static void spawnDragonDescendFlight(Entity projectile, Vec3 movement, int trailVisualLifetimeTicks) { PhotonProjectileEffects.spawnDragonDescendFlight(projectile, movement, trailVisualLifetimeTicks); }
    public static void spawnDragonDescendLingeringTrail(ClientLevel level, Vec3 center, Vec3 forward, int pulseLifetimeTicks) { PhotonProjectileEffects.spawnDragonDescendLingeringTrail(level, center, forward, pulseLifetimeTicks); }
    public static void spawnEnderProjectileFlight(Entity projectile, Vec3 movement) { PhotonProjectileEffects.spawnEnderProjectileFlight(projectile, movement); }
    public static void spawnEnderProjectileImpact(Entity projectile, Vec3 hitPos) { PhotonProjectileEffects.spawnEnderProjectileImpact(projectile, hitPos); }
    public static void spawnHolyProjectileFlight(Entity projectile, Vec3 movement) { PhotonProjectileEffects.spawnHolyProjectileFlight(projectile, movement); }
    public static void spawnHolyProjectileImpact(Entity projectile, Vec3 hitPos) { PhotonProjectileEffects.spawnHolyProjectileImpact(projectile, hitPos); }
    public static void spawnIceProjectileFlight(Entity projectile, Vec3 movement) { PhotonProjectileEffects.spawnIceProjectileFlight(projectile, movement); }
    public static void spawnIceProjectileImpact(Entity projectile, Vec3 hitPos) { PhotonProjectileEffects.spawnIceProjectileImpact(projectile, hitPos); }
    public static void spawnFireProjectileFlight(Entity projectile, Vec3 movement) { PhotonProjectileEffects.spawnFireProjectileFlight(projectile, movement); }
    public static void spawnFireProjectileImpact(Entity projectile, Vec3 hitPos) { PhotonProjectileEffects.spawnFireProjectileImpact(projectile, hitPos); }
    public static void spawnGoldenProjectileFlight(Entity projectile, Vec3 movement) { PhotonProjectileEffects.spawnGoldenProjectileFlight(projectile, movement); }
    public static void spawnGoldenProjectileImpact(Entity projectile, Vec3 hitPos) { PhotonProjectileEffects.spawnGoldenProjectileImpact(projectile, hitPos); }
    public static void spawnDiamondProjectileFlight(Entity projectile, Vec3 movement) { PhotonProjectileEffects.spawnDiamondProjectileFlight(projectile, movement); }
    public static void spawnDiamondProjectileImpact(Entity projectile, Vec3 hitPos) { PhotonProjectileEffects.spawnDiamondProjectileImpact(projectile, hitPos); }

    public static void spawnArclightMiniPortal(Entity projectile, Vec3 direction) { ArclightMiniPhotonEffects.spawnPortal(projectile, direction); }
    public static void spawnArclightMiniLaunch(Entity projectile, Vec3 direction) { ArclightMiniPhotonEffects.spawnLaunch(projectile, direction); }
    public static void spawnArclightMiniFlight(Entity projectile, Vec3 movement) { ArclightMiniPhotonEffects.spawnFlight(projectile, movement); }
    public static void spawnArclightMiniImpact(Entity projectile, Vec3 hitPos, Vec3 direction) { ArclightMiniPhotonEffects.spawnImpact(projectile, hitPos, direction); }
    public static void spawnArclightMiniDissolve(Entity projectile, Vec3 position) { ArclightMiniPhotonEffects.spawnDissolve(projectile, position); }
    public static void spawnArclightSpearPortal(Entity projectile, Vec3 direction) { ArclightMiniPhotonEffects.spawnSpearPortal(projectile, direction); }
    public static void spawnArclightSpearLaunch(Entity projectile, Vec3 direction) { ArclightMiniPhotonEffects.spawnSpearLaunch(projectile, direction); }
    public static void spawnArclightSpearFlight(Entity projectile, Vec3 movement) { ArclightMiniPhotonEffects.spawnSpearFlight(projectile, movement); }
    public static void spawnArclightSpearImpact(Entity projectile, Vec3 hitPos, Vec3 direction) { ArclightMiniPhotonEffects.spawnSpearImpact(projectile, hitPos, direction); }
    public static void spawnArclightSpearDissolve(Entity projectile, Vec3 position) { ArclightMiniPhotonEffects.spawnSpearDissolve(projectile, position); }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        PhotonPersistentEffects.onClientTick(event);
    }
}