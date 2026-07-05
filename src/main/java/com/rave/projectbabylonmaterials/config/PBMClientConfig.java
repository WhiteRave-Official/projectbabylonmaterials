package com.rave.projectbabylonmaterials.config;

import com.rave.projectbabylonmaterials.hud.HudAlign;
import com.rave.projectbabylonmaterials.hud.HudAnchor;
import com.rave.projectbabylonmaterials.hud.HudElementLayout;
import com.rave.projectbabylonmaterials.hud.HudElements;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Map;

public final class PBMClientConfig {
    public static final ModConfigSpec SPEC;
    private static final Holder HOLDER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
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

    /**
     * Applies the player's local HUD override for an element on top of the (datapack/default) base layout.
     * Returns {@code base} unchanged when the override for that element is disabled.
     */
    public static HudElementLayout applyHudOverride(String elementId, HudElementLayout base) {
        HudOverride override = HOLDER.overrides.get(elementId);
        if (override == null || !override.override.get()) {
            return base;
        }
        return new HudElementLayout(
                override.anchor.get(),
                override.alignX.get(),
                override.alignY.get(),
                override.offsetX.get(),
                override.offsetY.get(),
                (float) (double) override.scale.get(),
                override.visible.get(),
                base.shiftForOffhand()
        );
    }

    private static final class Holder {
        private final ModConfigSpec.BooleanValue showCustomCombatHud;
        private final ModConfigSpec.BooleanValue hideVanillaArmorHud;
        private final Map<String, HudOverride> overrides;

        private Holder(ModConfigSpec.Builder builder) {
            builder.push("hud");
            showCustomCombatHud = builder
                    .comment("Show the custom armor and toughness HUD next to the hotbar.")
                    .define("showCustomCombatHud", true);
            hideVanillaArmorHud = builder
                    .comment("Hide the vanilla armor bar when the custom combat HUD is enabled.")
                    .define("hideVanillaArmorHud", true);

            builder.comment("Local per-element HUD placement overrides. Enable 'override' to use these instead of the datapack layout.")
                    .push("layout");
            overrides = Map.of(
                    HudElements.STATS_BOX, buildOverride(builder, HudElements.STATS_BOX, HudElements.STATS_BOX_DEFAULT),
                    HudElements.DRAGONSTEEL_PASSIVE, buildOverride(builder, HudElements.DRAGONSTEEL_PASSIVE, HudElements.DRAGONSTEEL_PASSIVE_DEFAULT)
            );
            builder.pop();

            builder.pop();
        }

        private static HudOverride buildOverride(ModConfigSpec.Builder builder, String id, HudElementLayout def) {
            builder.push(id);
            HudOverride override = new HudOverride(
                    builder.comment("Use the values below instead of the datapack-provided placement for this element.")
                            .define("override", false),
                    builder.comment("Screen anchor point.").defineEnum("anchor", def.anchor()),
                    builder.comment("Horizontal alignment of the element to the anchor (start/center/end).").defineEnum("align_x", def.alignX()),
                    builder.comment("Vertical alignment of the element to the anchor (start/center/end).").defineEnum("align_y", def.alignY()),
                    builder.comment("Horizontal pixel offset from the anchor.").defineInRange("offset_x", def.offsetX(), -10000, 10000),
                    builder.comment("Vertical pixel offset from the anchor.").defineInRange("offset_y", def.offsetY(), -10000, 10000),
                    builder.comment("Scale multiplier.").defineInRange("scale", (double) def.scale(), 0.1D, 10.0D),
                    builder.comment("Whether the element is drawn at all.").define("visible", def.visible())
            );
            builder.pop();
            return override;
        }
    }

    private record HudOverride(
            ModConfigSpec.BooleanValue override,
            ModConfigSpec.EnumValue<HudAnchor> anchor,
            ModConfigSpec.EnumValue<HudAlign> alignX,
            ModConfigSpec.EnumValue<HudAlign> alignY,
            ModConfigSpec.IntValue offsetX,
            ModConfigSpec.IntValue offsetY,
            ModConfigSpec.DoubleValue scale,
            ModConfigSpec.BooleanValue visible
    ) {
    }
}
