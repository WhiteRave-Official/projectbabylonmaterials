package com.rave.projectbabylonmaterials.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class PBMClientConfig {
    public static final ForgeConfigSpec SPEC;
    private static final Holder HOLDER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        HOLDER = new Holder(builder);
        SPEC = builder.build();
    }

    private PBMClientConfig() {
    }

    public static boolean showCustomCombatHud() {
        return HOLDER.showCustomCombatHud.get();
    }

    public static boolean hideVanillaArmorHud() {
        return HOLDER.hideVanillaArmorHud.get();
    }

    public static CombatHudPosition combatHudPosition() {
        return HOLDER.combatHudPosition.get();
    }

    public static int customCombatHudX() {
        return HOLDER.customCombatHudX.get();
    }

    public static int customCombatHudY() {
        return HOLDER.customCombatHudY.get();
    }

    public static PhotonMode photonMode() {
        return HOLDER.photonMode.get();
    }

    public static boolean useLitePhotonEffects() {
        return photonMode() == PhotonMode.LITE;
    }

    public enum PhotonMode {
        NORMAL,
        LITE
    }

    public enum CombatHudPosition {
        LEFT_OF_HOTBAR,
        RIGHT_OF_HOTBAR,
        TOP_LEFT,
        TOP_RIGHT,
        CUSTOM
    }

    private static final class Holder {
        private final ForgeConfigSpec.BooleanValue showCustomCombatHud;
        private final ForgeConfigSpec.BooleanValue hideVanillaArmorHud;
        private final ForgeConfigSpec.EnumValue<CombatHudPosition> combatHudPosition;
        private final ForgeConfigSpec.IntValue customCombatHudX;
        private final ForgeConfigSpec.IntValue customCombatHudY;
        private final ForgeConfigSpec.EnumValue<PhotonMode> photonMode;

        private Holder(ForgeConfigSpec.Builder builder) {
            builder.push("hud");
            showCustomCombatHud = builder
                    .comment("Show the custom armor and toughness HUD next to the hotbar.")
                    .define("showCustomCombatHud", true);
            hideVanillaArmorHud = builder
                    .comment("Hide the vanilla armor bar when the custom combat HUD is enabled.")
                    .define("hideVanillaArmorHud", true);
            combatHudPosition = builder
                    .comment("Preset position for the custom combat HUD.", "Available: LEFT_OF_HOTBAR, RIGHT_OF_HOTBAR, TOP_LEFT, TOP_RIGHT, CUSTOM")
                    .defineEnum("combatHudPosition", CombatHudPosition.LEFT_OF_HOTBAR);
            customCombatHudX = builder
                    .comment("Absolute X position for the custom combat HUD when combatHudPosition=CUSTOM.")
                    .defineInRange("customCombatHudX", 4, -4096, 4096);
            customCombatHudY = builder
                    .comment("Absolute Y position for the custom combat HUD when combatHudPosition=CUSTOM.")
                    .defineInRange("customCombatHudY", 4, -4096, 4096);
            builder.pop();

            builder.push("photon");
            photonMode = builder
                    .comment("Photon visual quality mode.", "NORMAL keeps full effects.", "LITE reduces particle counts, effect lifetime and spawn frequency for better performance.")
                    .defineEnum("photonMode", PhotonMode.NORMAL);
            builder.pop();
        }
    }
}