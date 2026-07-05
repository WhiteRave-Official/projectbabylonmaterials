package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ArmorSetBonusManager {

    private static final Map<UUID, String> ACTIVE_SETS = new HashMap<>();

    private ArmorSetBonusManager() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        updateActiveSet(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (entity.level().isClientSide || entity instanceof Player || entity.tickCount <= 0) {
            return;
        }

        updateActiveSet(entity);
    }

    public static void appendCollapsedTooltip(ItemStack stack, List<Component> tooltip) {
        if (findSet(stack) != null) {
            tooltip.add(Component.translatable("tooltip.project_babylon_materials.hold_shift").withStyle(ChatFormatting.GRAY));
        }
    }

    public static void appendTooltip(ItemStack stack, List<Component> tooltip, boolean expanded, LivingEntity entity) {
        ArmorSetDefinition set = findSet(stack);
        if (set == null) {
            return;
        }

        if (!expanded) {
            appendCollapsedTooltip(stack, tooltip);
            return;
        }

        tooltip.add(Component.translatable("tooltip.project_babylon_materials.set_label", Component.translatable(set.getDisplayNameKey()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.project_babylon_materials.pieces_label", getMatchedPieces(set, entity), 4)
                .withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.project_babylon_materials.armor_pieces").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.empty());
        appendPieceLine(tooltip, "tooltip.project_babylon_materials.piece.helmet", hasPiece(set, entity, EquipmentSlot.HEAD));
        appendPieceLine(tooltip, "tooltip.project_babylon_materials.piece.chestplate", hasPiece(set, entity, EquipmentSlot.CHEST));
        appendPieceLine(tooltip, "tooltip.project_babylon_materials.piece.leggings", hasPiece(set, entity, EquipmentSlot.LEGS));
        appendPieceLine(tooltip, "tooltip.project_babylon_materials.piece.boots", hasPiece(set, entity, EquipmentSlot.FEET));

        appendBonusSection(tooltip, set.getMaterialBonuses());
        appendBonusSection(tooltip, set.getClassBonuses());
    }

    public static ArmorSetTooltipData createTooltipData(ItemStack stack, LivingEntity entity) {
        ArmorSetDefinition set = findSet(stack);
        if (set == null) {
            return null;
        }

        List<ArmorSetTooltipData.ArmorPieceEntry> armorPieces = List.of(
                createArmorPieceEntry(set, entity, EquipmentSlot.HEAD, "tooltip.project_babylon_materials.piece.helmet"),
                createArmorPieceEntry(set, entity, EquipmentSlot.CHEST, "tooltip.project_babylon_materials.piece.chestplate"),
                createArmorPieceEntry(set, entity, EquipmentSlot.LEGS, "tooltip.project_babylon_materials.piece.leggings"),
                createArmorPieceEntry(set, entity, EquipmentSlot.FEET, "tooltip.project_babylon_materials.piece.boots")
        );

        List<ArmorSetTooltipData.BonusEntry> bonuses = set.getAllBonuses().stream()
                .map(bonus -> new ArmorSetTooltipData.BonusEntry(
                        bonus.getType(),
                        bonus.getDisplayName(),
                        bonus.getFrameTexture(),
                        bonus.getIconTexture(),
                        bonus.getTooltipLines()
                ))
                .toList();

        return new ArmorSetTooltipData(
                Component.translatable(set.getDisplayNameKey()).withStyle(ChatFormatting.GOLD),
                getMatchedPieces(set, entity),
                armorPieces,
                bonuses
        );
    }

    public static int getExpandedTooltipLineCount(ArmorSetTooltipData data) {
        int lineCount = 9;
        for (ArmorSetTooltipData.BonusEntry bonus : data.bonuses()) {
            lineCount += 1 + 1 + bonus.descriptionLines().size();
        }
        return lineCount;
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        clearActiveSet(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ACTIVE_SETS.remove(event.getOriginal().getUUID());
        ACTIVE_SETS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            clearActiveSet(living);
        }
    }

    private static void updateActiveSet(LivingEntity entity) {
        ArmorSetDefinition currentSet = ArmorSetRegistry.findMatching(entity);
        String previousSetId = ACTIVE_SETS.get(entity.getUUID());

        if (currentSet == null) {
            if (previousSetId != null) {
                removeSetBonus(entity, previousSetId);
                ACTIVE_SETS.remove(entity.getUUID());
            }
            return;
        }

        if (!currentSet.getId().equals(previousSetId)) {
            if (previousSetId != null) {
                removeSetBonus(entity, previousSetId);
            }

            applySetBonuses(entity, currentSet);
            ACTIVE_SETS.put(entity.getUUID(), currentSet.getId());
            return;
        }

        if (!areBonusesApplied(entity, currentSet)) {
            applySetBonuses(entity, currentSet);
        }
    }

    private static ArmorSetDefinition findSet(ItemStack stack) {
        return ArmorSetRegistry.findContaining(stack.getItem());
    }

    private static void removeSetBonus(LivingEntity entity, String setId) {
        ArmorSetDefinition set = ArmorSetRegistry.findById(setId);
        if (set != null) {
            for (ArmorSetBonus bonus : set.getAllBonuses()) {
                bonus.remove(entity);
            }
        }
    }

    private static void clearActiveSet(LivingEntity entity) {
        String setId = ACTIVE_SETS.remove(entity.getUUID());
        if (setId != null) {
            removeSetBonus(entity, setId);
        }
    }

    private static void applySetBonuses(LivingEntity entity, ArmorSetDefinition set) {
        double previousMaxHealth = entity.getMaxHealth();
        float previousHealth = entity.getHealth();

        for (ArmorSetBonus bonus : set.getAllBonuses()) {
            bonus.apply(entity);
        }

        adjustHealthAfterBonusApply(entity, previousMaxHealth, previousHealth);
    }

    private static void adjustHealthAfterBonusApply(LivingEntity entity, double previousMaxHealth, float previousHealth) {
        double newMaxHealth = entity.getMaxHealth();
        if (previousMaxHealth <= 0.0D || Math.abs(newMaxHealth - previousMaxHealth) <= 0.0001D) {
            return;
        }

        if (entity instanceof Player) {
            float adjustedHealth = (float) Mth.clamp((previousHealth / previousMaxHealth) * newMaxHealth, 0.0D, newMaxHealth);
            entity.setHealth(adjustedHealth);
            return;
        }

        if (newMaxHealth > previousMaxHealth) {
            float healedHealth = (float) Math.min(newMaxHealth, previousHealth + (newMaxHealth - previousMaxHealth));
            entity.setHealth(healedHealth);
            return;
        }

        if (entity.getHealth() > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }
    }

    private static boolean areBonusesApplied(LivingEntity entity, ArmorSetDefinition set) {
        for (ArmorSetBonus bonus : set.getAllBonuses()) {
            if (!bonus.isApplied(entity)) {
                return false;
            }
        }
        return true;
    }

    private static int getMatchedPieces(ArmorSetDefinition set, LivingEntity entity) {
        return entity == null ? 0 : set.countMatchedPieces(entity);
    }

    private static boolean hasPiece(ArmorSetDefinition set, LivingEntity entity, EquipmentSlot slot) {
        return entity != null && set.hasPiece(entity, slot);
    }

    private static ArmorSetTooltipData.ArmorPieceEntry createArmorPieceEntry(ArmorSetDefinition set, LivingEntity entity, EquipmentSlot slot,
                                                                             String pieceLabelKey) {
        return new ArmorSetTooltipData.ArmorPieceEntry(
                set.getPieceStack(slot),
                Component.translatable(pieceLabelKey),
                hasPiece(set, entity, slot)
        );
    }

    private static void appendPieceLine(List<Component> tooltip, String pieceLabelKey, boolean equipped) {
        ChatFormatting stateColor = equipped ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY;
        String stateKey = equipped ? "tooltip.project_babylon_materials.equipped" : "tooltip.project_babylon_materials.missing";
        tooltip.add(Component.literal("    ")
                .append(Component.translatable(pieceLabelKey).withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable(stateKey).withStyle(stateColor)));
    }

    private static void appendBonusSection(List<Component> tooltip, List<ArmorSetBonus> bonuses) {
        for (ArmorSetBonus bonus : bonuses) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("    ")
                    .append(bonus.getType().getTitle().copy().withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(": ").withStyle(ChatFormatting.GOLD))
                    .append(bonus.getDisplayName().copy().withStyle(ChatFormatting.AQUA)));
            tooltip.addAll(bonus.getTooltipLines());
        }
    }
}

