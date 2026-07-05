package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.config.PBMServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class PlayerHealthHandler {
    private static final ResourceLocation PLAYER_BASE_HEALTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "player_base_health");
    private static final double VANILLA_PLAYER_BASE_HEALTH = 20.0D;

    private PlayerHealthHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            applyConfiguredMaxHealth(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            applyConfiguredMaxHealth(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            applyConfiguredMaxHealth(player);
        }
    }

    private static boolean isServerConfig(ModConfig config) {
        return config.getModId().equals(ProjectBabylonMaterials.MODID) && config.getType() == ModConfig.Type.SERVER;
    }

    private static void refreshOnlinePlayers() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            applyConfiguredMaxHealth(player);
        }
    }

    private static void applyConfiguredMaxHealth(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        double previousMaxHealth = player.getMaxHealth();
        float previousHealth = player.getHealth();
        if (maxHealth.getModifier(PLAYER_BASE_HEALTH_MODIFIER_ID) != null) {
            maxHealth.removeModifier(PLAYER_BASE_HEALTH_MODIFIER_ID);
        }

        double bonusHealth = PBMServerConfig.getPlayerBaseHealth() - VANILLA_PLAYER_BASE_HEALTH;
        if (Math.abs(bonusHealth) > 0.0001D) {
            maxHealth.addTransientModifier(new AttributeModifier(
                    PLAYER_BASE_HEALTH_MODIFIER_ID,
                    bonusHealth,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }

        double newMaxHealth = player.getMaxHealth();
        if (previousMaxHealth > 0.0D && Math.abs(newMaxHealth - previousMaxHealth) > 0.0001D) {
            float adjustedHealth = (float) Mth.clamp((previousHealth / previousMaxHealth) * newMaxHealth, 0.0D, newMaxHealth);
            player.setHealth(adjustedHealth);
        }
    }

    // TODO(port): The 1.20.1 Forge version subscribed these config handlers on the FORGE (game) bus, where
    // ModConfigEvent — a mod-bus event — was never delivered. NeoForge strictly separates the buses and would
    // throw if a mod-bus event were registered on the game bus, so the config-reload handlers live on the mod
    // bus here. They re-apply the same idempotent base-health modifier already applied on join/login/respawn.
    @EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static final class ConfigEvents {
        private ConfigEvents() {
        }

        @SubscribeEvent
        public static void onConfigLoading(ModConfigEvent.Loading event) {
            if (isServerConfig(event.getConfig())) {
                refreshOnlinePlayers();
            }
        }

        @SubscribeEvent
        public static void onConfigReloading(ModConfigEvent.Reloading event) {
            if (isServerConfig(event.getConfig())) {
                refreshOnlinePlayers();
            }
        }
    }
}
