package com.rave.projectbabylonmaterials.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.client.hud.ClientHudLayoutState;
import com.rave.projectbabylonmaterials.client.hud.HudPlacement;
import com.rave.projectbabylonmaterials.client.hud.HudScale;
import com.rave.projectbabylonmaterials.hud.HudElements;
import com.rave.projectbabylonmaterials.config.PBMClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CombatStatsOverlay {
    private static final ResourceLocation ARMOR_ICON = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/gui/hud/armor_icon.png");
    private static final ResourceLocation TOUGHNESS_ICON = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/gui/hud/armor_toughness_icon.png");
    private static final ResourceLocation MAGIC_ARMOR_ICON = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/gui/hud/magic_armor_icon.png");
    private static final ResourceLocation DRAGONSTEEL_PASSIVE_ICON = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "textures/gui/tooltip/icon/armorset/dragonsteel_passive_icon.png");
    private static final ResourceLocation SPELL_RESIST_ATTRIBUTE_ID = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "spell_resist");
    private static final String ARMORS_NAMESPACE = "project_babylon_armors";
    private static final String DRAGONSTEEL_PREFIX = "dragonsteel_";
    private static final int BACKGROUND_COLOR = 0x88000000;
    private static final int BORDER_COLOR = 0x90CFCFCF;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int COOLDOWN_TEXT_COLOR = 0xFFFFFFFF;
    private static final int COOLDOWN_SHADOW_COLOR = 0xAA000000;
    private static final int COOLDOWN_PROGRESS_COLOR = 0xCC2A0D0D;
    private static final int ICON_SIZE = 9;
    private static final int TEXT_OFFSET_X = 12;
    private static final int ROW_HEIGHT = 11;
    private static final int BOX_PADDING_X = 4;
    private static final int BOX_PADDING_Y = 3;
    private static final int HOTBAR_HALF_WIDTH = 91;
    private static final int HOTBAR_HEIGHT = 22;
    private static final int HOTBAR_GAP = 6;
    private static final int OFFHAND_SHIFT_X = 26;
    private static final int ARES_OFFHAND_EXTRA_SHIFT_X = 4;
    private static final int BOTTOM_MARGIN = -14;
    private static final int PASSIVE_ICON_SIZE = 16;
    private static final int PASSIVE_HOTBAR_GAP = 8;
    private static final int PASSIVE_VERTICAL_OFFSET = 3;
    private static final float COOLDOWN_TEXT_SCALE = 0.7F;
    private static final int COOLDOWN_TEXT_GAP = 3;
    private static final int REBIRTH_COOLDOWN_TICKS = 12000;
    private static final String ARES_HUD_MODID = "ares_hud";
    private static boolean aresHudLookupInitialized;
    private static Method aresGetHotbarTypeMethod;
    private static Method aresIsOffhandEnabledMethod;

    public static final LayeredDraw.Layer HUD = (guiGraphics, deltaTracker) -> {
        Minecraft minecraft = Minecraft.getInstance();
        if (!PBMClientConfig.showCustomCombatHud()) {
            return;
        }
        if (minecraft.options.hideGui || minecraft.player == null || minecraft.gameMode == null || minecraft.player.isSpectator()) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        Font font = minecraft.font;
        List<Row> rows = new ArrayList<>();
        rows.add(new Row(ARMOR_ICON, formatValue(minecraft.player.getAttributeValue(Attributes.ARMOR))));
        rows.add(new Row(TOUGHNESS_ICON, formatValue(minecraft.player.getAttributeValue(Attributes.ARMOR_TOUGHNESS))));

        Holder<Attribute> magicArmorAttribute = getMagicArmorAttribute();
        if (magicArmorAttribute != null) {
            rows.add(new Row(MAGIC_ARMOR_ICON, formatValue(minecraft.player.getAttributeValue(magicArmorAttribute))));
        }

        int contentWidth = rows.stream().mapToInt(row -> font.width(row.value())).max().orElse(0);
        int boxWidth = BOX_PADDING_X * 2 + ICON_SIZE + 3 + contentWidth;
        int boxHeight = BOX_PADDING_Y * 2 + ROW_HEIGHT * rows.size();
        HudPlacement statsPlacement = ClientHudLayoutState.resolve(HudElements.STATS_BOX, screenWidth, screenHeight, boxWidth, boxHeight);
        if (statsPlacement.visible()) {
            int x = statsPlacement.x();
            int y = statsPlacement.y();
            Boolean aresHudOffhandVisible = getAresHudOffhandSlotVisible();
            if (statsPlacement.shiftForOffhand() && shouldShiftForOffhandSlot(minecraft.player, aresHudOffhandVisible)) {
                x -= OFFHAND_SHIFT_X;
                if (Boolean.TRUE.equals(aresHudOffhandVisible)) {
                    x -= ARES_OFFHAND_EXTRA_SHIFT_X;
                }
            }

            guiGraphics.pose().pushPose();
            HudScale.apply(guiGraphics, x, y, statsPlacement.scale());

            guiGraphics.fill(x, y, x + boxWidth, y + boxHeight, BACKGROUND_COLOR);
            guiGraphics.fill(x, y, x + boxWidth, y + 1, BORDER_COLOR);
            guiGraphics.fill(x, y + boxHeight - 1, x + boxWidth, y + boxHeight, BORDER_COLOR);
            guiGraphics.fill(x, y, x + 1, y + boxHeight, BORDER_COLOR);
            guiGraphics.fill(x + boxWidth - 1, y, x + boxWidth, y + boxHeight, BORDER_COLOR);

            for (int i = 0; i < rows.size(); i++) {
                Row row = rows.get(i);
                renderRow(guiGraphics, font, x + BOX_PADDING_X, y + BOX_PADDING_Y + ROW_HEIGHT * i, row.icon(), row.value());
            }

            guiGraphics.pose().popPose();
        }

        renderDragonsteelPassive(guiGraphics, font, minecraft.player, screenWidth, screenHeight);
    };

    private CombatStatsOverlay() {
    }

    private static boolean shouldShiftForOffhandSlot(Player player, Boolean aresHudOffhandVisible) {
        if (player.getMainArm() != HumanoidArm.RIGHT) {
            return false;
        }
        if (aresHudOffhandVisible != null) {
            return aresHudOffhandVisible;
        }

        return !player.getOffhandItem().isEmpty();
    }

    private static Boolean getAresHudOffhandSlotVisible() {
        if (!ModList.get().isLoaded(ARES_HUD_MODID)) {
            return null;
        }

        initializeAresHudHooks();
        if (aresGetHotbarTypeMethod == null || aresIsOffhandEnabledMethod == null) {
            return null;
        }

        try {
            Object hotbarType = aresGetHotbarTypeMethod.invoke(null);
            if (hotbarType == null) {
                return null;
            }
            return (Boolean) aresIsOffhandEnabledMethod.invoke(null, hotbarType);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static void initializeAresHudHooks() {
        if (aresHudLookupInitialized) {
            return;
        }

        synchronized (CombatStatsOverlay.class) {
            if (aresHudLookupInitialized) {
                return;
            }

            try {
                Class<?> hudConfigClass = Class.forName("com.super_awesome_baby.ares_hud.client.config.HudConfig");
                Class<?> hotbarTypeClass = Class.forName("com.super_awesome_baby.ares_hud.client.config.HudConfig$HotbarType");
                aresGetHotbarTypeMethod = hudConfigClass.getMethod("getHotbarType");
                aresIsOffhandEnabledMethod = hudConfigClass.getMethod("isOffhandEnabled", hotbarTypeClass);
            } catch (ReflectiveOperationException ignored) {
                aresGetHotbarTypeMethod = null;
                aresIsOffhandEnabledMethod = null;
            }

            aresHudLookupInitialized = true;
        }
    }
    private static void renderRow(GuiGraphics guiGraphics, Font font, int x, int y, ResourceLocation iconTexture, String value) {
        guiGraphics.blit(iconTexture, x, y + 1, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        guiGraphics.drawString(font, value, x + TEXT_OFFSET_X, y + 2, TEXT_COLOR, false);
    }

    private static void renderDragonsteelPassive(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight) {
        if (!isWearingDragonsteelSet(player)) {
            return;
        }

        HudPlacement placement = ClientHudLayoutState.resolve(HudElements.DRAGONSTEEL_PASSIVE, screenWidth, screenHeight, PASSIVE_ICON_SIZE, PASSIVE_ICON_SIZE);
        if (!placement.visible()) {
            return;
        }
        int iconX = placement.x();
        int iconY = placement.y();
        int remainingTicks = DragonsteelCooldownClientState.getRemainingTicks();
        boolean onCooldown = remainingTicks > 0;

        guiGraphics.pose().pushPose();
        HudScale.apply(guiGraphics, iconX, iconY, placement.scale());

        if (onCooldown) {
            RenderSystem.setShaderColor(0.45F, 0.45F, 0.45F, 1.0F);
        }
        guiGraphics.blit(
                DRAGONSTEEL_PASSIVE_ICON,
                iconX,
                iconY,
                0,
                0,
                PASSIVE_ICON_SIZE,
                PASSIVE_ICON_SIZE,
                PASSIVE_ICON_SIZE,
                PASSIVE_ICON_SIZE
        );
        if (onCooldown) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            renderCooldownProgress(guiGraphics, iconX, iconY, remainingTicks);

            String cooldownText = formatCooldownSeconds(remainingTicks);
            int scaledTextWidth = Math.round(font.width(cooldownText) * COOLDOWN_TEXT_SCALE);
            int scaledTextHeight = Math.round(font.lineHeight * COOLDOWN_TEXT_SCALE);
            int textX = iconX + (PASSIVE_ICON_SIZE - scaledTextWidth) / 2;
            int textY = iconY - COOLDOWN_TEXT_GAP - scaledTextHeight;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(textX, textY, 0.0F);
            guiGraphics.pose().scale(COOLDOWN_TEXT_SCALE, COOLDOWN_TEXT_SCALE, 1.0F);
            guiGraphics.drawString(font, cooldownText, 1, 1, COOLDOWN_SHADOW_COLOR, false);
            guiGraphics.drawString(font, cooldownText, 0, 0, COOLDOWN_TEXT_COLOR, false);
            guiGraphics.pose().popPose();
        }

        guiGraphics.pose().popPose();
    }

    private static void renderCooldownProgress(GuiGraphics guiGraphics, int x, int y, int remainingTicks) {
        double cooldownProgress = Math.min(1.0D, Math.max(0.0D, remainingTicks / (double) REBIRTH_COOLDOWN_TICKS));
        int overlayHeight = (int) Math.ceil(PASSIVE_ICON_SIZE * cooldownProgress);
        int overlayTop = y + PASSIVE_ICON_SIZE - overlayHeight;
        guiGraphics.fill(x, overlayTop, x + PASSIVE_ICON_SIZE, y + PASSIVE_ICON_SIZE, COOLDOWN_PROGRESS_COLOR);
    }

    private static boolean isWearingDragonsteelSet(Player player) {
        int armorPieces = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) {
                return false;
            }

            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (itemId == null || !ARMORS_NAMESPACE.equals(itemId.getNamespace()) || !itemId.getPath().startsWith(DRAGONSTEEL_PREFIX)) {
                return false;
            }
            armorPieces++;
        }
        return armorPieces == 4;
    }

    private static Holder<Attribute> getMagicArmorAttribute() {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return null;
        }
        return BuiltInRegistries.ATTRIBUTE.getHolder(SPELL_RESIST_ATTRIBUTE_ID).orElse(null);
    }

    private static String formatValue(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String formatCooldownSeconds(int remainingTicks) {
        double remainingSeconds = remainingTicks / 20.0D;
        if (remainingSeconds >= 10.0D) {
            return Integer.toString((int) Math.ceil(remainingSeconds));
        }
        return String.format(Locale.ROOT, "%.1f", remainingSeconds);
    }

    private record Row(ResourceLocation icon, String value) {
    }
}

