package com.rave.projectbabylonmaterials.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PBMServerConfig {
    public static final ModConfigSpec SPEC;
    private static final Holder HOLDER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        HOLDER = new Holder(builder);
        SPEC = builder.build();
    }

    private PBMServerConfig() {
    }

    public static double getPlayerBaseHealth() {
        return HOLDER.playerBaseHealth.get();
    }

    private static final class Holder {
        private final ModConfigSpec.DoubleValue playerBaseHealth;

        private Holder(ModConfigSpec.Builder builder) {
            builder.push("combat");
            playerBaseHealth = builder
                    .comment("Base max health for players. Vanilla default is 20.0.")
                    .defineInRange("playerBaseHealth", 30.0D, 1.0D, 1024.0D);
            builder.pop();
        }
    }
}
