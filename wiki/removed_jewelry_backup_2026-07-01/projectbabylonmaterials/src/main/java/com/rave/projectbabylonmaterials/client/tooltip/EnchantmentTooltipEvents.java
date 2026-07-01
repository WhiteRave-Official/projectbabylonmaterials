package com.rave.projectbabylonmaterials.client.tooltip;

import com.mojang.datafixers.util.Either;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import com.rave.projectbabylonmaterials.tooltip.EnchantmentDetailsTooltipData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentTooltipEvents {
    private static final Set<String> MODIFIER_HEADERS = Set.of(
            "item.modifiers.mainhand",
            "item.modifiers.offhand",
            "item.modifiers.head",
            "item.modifiers.chest",
            "item.modifiers.legs",
            "item.modifiers.feet"
    );
    private static final Set<String> EPIC_FIGHT_ATTRIBUTE_KEYS = Set.of(
            "attribute.name.epicfight.impact",
            "attribute.name.epicfight.armor_negation",
            "attribute.name.epicfight.max_strikes",
            "attribute.name.epicfight.stun_armor",
            "attribute.name.epicfight.weight",
            "attribute.name.epicfight.staminar",
            "attribute.name.epicfight.stamina_regen",
            "attribute.name.epicfight.execution_resistance",
            "attribute.name.epicfight.offhand_attack_speed",
            "attribute.name.epicfight.offhand_impact",
            "attribute.name.epicfight.offhand_armor_negation",
            "attribute.name.epicfight.offhand_max_strikes"
    );
    private static final Set<String> EPIC_FIGHT_FALLBACK_SNIPPETS = Set.of(
            "epic fight",
            "hit % enemies per swing",
            "hit % enemies per sming",
            "enemies per swing",
            "enemies per sming",
            "impact",
            "armor negation",
            "max strikes",
            "stun armor",
            "weight",
            "max stamina",
            "stamina regen",
            "execution resistance",
            "offhand attack speed",
            "offhand impact",
            "offhand armor negation",
            "offhand max strikes"
    );

    private EnchantmentTooltipEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (Screen.hasAltDown() || !EnchantmentSlotHelper.hasEnchantmentTooltip(stack)) {
            return;
        }

        if (!Screen.hasControlDown()) {
            event.getToolTip().add(Component.translatable("tooltip.project_babylon_materials.hold_ctrl_enchants")
                    .withStyle(ChatFormatting.WHITE));
            return;
        }

        filterTooltipComponents(event.getToolTip(), stack);
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (!Screen.hasControlDown() || Screen.hasAltDown() || !EnchantmentSlotHelper.hasEnchantmentTooltip(stack)) {
            return;
        }

        filterTooltipElements(event.getTooltipElements(), stack);
        event.getTooltipElements().add(Either.right(createTooltipData(stack)));
    }

    public static void filterTooltipComponents(List<Component> tooltip, ItemStack stack) {
        Set<String> enchantmentLines = buildEnchantmentLineSet(stack);
        List<Component> filtered = new ArrayList<>(tooltip.size());
        boolean inModifierSection = false;

        for (Component component : tooltip) {
            String line = component.getString();
            if (isVanillaEnchantmentLine(line, enchantmentLines) || isEpicFightLine(line)) {
                continue;
            }

            if (isModifierHeader(line)) {
                inModifierSection = true;
                continue;
            }

            if (inModifierSection) {
                if (looksLikeModifierLine(line) || isEpicFightLine(line)) {
                    continue;
                }
                inModifierSection = false;
            }

            filtered.add(component);
        }

        tooltip.clear();
        tooltip.addAll(filtered);
    }

    public static void filterTooltipElements(List<Either<FormattedText, TooltipComponent>> elements, ItemStack stack) {
        Set<String> enchantmentLines = buildEnchantmentLineSet(stack);
        List<Either<FormattedText, TooltipComponent>> filtered = new ArrayList<>(elements.size());
        boolean inModifierSection = false;

        for (Either<FormattedText, TooltipComponent> element : elements) {
            if (element.left().isEmpty()) {
                filtered.add(element);
                continue;
            }

            String line = element.left().get().getString();
            if (isVanillaEnchantmentLine(line, enchantmentLines) || isEpicFightLine(line)) {
                continue;
            }

            if (isModifierHeader(line)) {
                inModifierSection = true;
                continue;
            }

            if (inModifierSection) {
                if (looksLikeModifierLine(line) || isEpicFightLine(line)) {
                    continue;
                }
                inModifierSection = false;
            }

            filtered.add(element);
        }

        elements.clear();
        elements.addAll(filtered);
    }

    private static EnchantmentDetailsTooltipData createTooltipData(ItemStack stack) {
        int visibleSlotCount = EnchantmentSlotHelper.getVisibleSlotCount(stack);
        List<EnchantmentDetailsTooltipData.SlotEntry> slots = new ArrayList<>(visibleSlotCount);
        List<EnchantmentSlotHelper.SlottedEnchantment> enchantments = EnchantmentSlotHelper.getOrderedEnchantments(stack);

        for (int i = 0; i < visibleSlotCount; i++) {
            if (i < enchantments.size()) {
                EnchantmentSlotHelper.SlottedEnchantment slottedEnchantment = enchantments.get(i);
                Enchantment enchantment = slottedEnchantment.enchantment();
                slots.add(new EnchantmentDetailsTooltipData.SlotEntry(
                        false,
                        new ItemStack(Items.ENCHANTED_BOOK),
                        enchantment.getFullname(slottedEnchantment.level()),
                        createDescription(enchantment),
                        createApplicableItems(enchantment)
                ));
            } else {
                slots.add(new EnchantmentDetailsTooltipData.SlotEntry(
                        true,
                        ItemStack.EMPTY,
                        Component.translatable("tooltip.project_babylon_materials.empty_slot"),
                        Component.empty(),
                        List.of()
                ));
            }
        }

        return new EnchantmentDetailsTooltipData(
                Component.translatable("tooltip.project_babylon_materials.enchantments"),
                Component.translatable("tooltip.project_babylon_materials.applies_to"),
                slots
        );
    }

    private static Component createDescription(Enchantment enchantment) {
        String descriptionKey = enchantment.getDescriptionId() + ".desc";
        return I18n.exists(descriptionKey)
                ? Component.translatable(descriptionKey)
                : Component.translatable("tooltip.project_babylon_materials.no_enchantment_description");
    }

    private static Set<String> buildEnchantmentLineSet(ItemStack stack) {
        Set<String> enchantmentLines = new HashSet<>();
        for (EnchantmentSlotHelper.SlottedEnchantment enchantment : EnchantmentSlotHelper.getOrderedEnchantments(stack)) {
            enchantmentLines.add(enchantment.enchantment().getFullname(enchantment.level()).getString());
        }
        return enchantmentLines;
    }

    private static boolean isVanillaEnchantmentLine(String line, Set<String> enchantmentLines) {
        return enchantmentLines.contains(line);
    }

    private static boolean isModifierHeader(String line) {
        String trimmed = line.trim();
        for (String key : MODIFIER_HEADERS) {
            if (trimmed.equals(I18n.get(key))) {
                return true;
            }
        }
        return false;
    }

    private static boolean looksLikeModifierLine(String line) {
        if (line.isBlank()) {
            return true;
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return true;
        }

        char first = trimmed.charAt(0);
        return first == '+' || first == '-' || Character.isDigit(first);
    }

    private static boolean isEpicFightLine(String line) {
        String trimmed = line.trim();
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if (normalized.contains("epic fight")) {
            return true;
        }

        String epicFightHeader = I18n.get("gui.epicfight.attribute");
        if (!epicFightHeader.equals("gui.epicfight.attribute") && trimmed.equals(epicFightHeader)) {
            return true;
        }

        for (String key : EPIC_FIGHT_ATTRIBUTE_KEYS) {
            String localized = I18n.get(key);
            if (!localized.equals(key) && normalized.contains(localized.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }

        for (String snippet : EPIC_FIGHT_FALLBACK_SNIPPETS) {
            if (normalized.contains(snippet)) {
                return true;
            }
        }

        return false;
    }

    private static List<ItemStack> createApplicableItems(Enchantment enchantment) {
        return switch (enchantment.getDescriptionId()) {
            case "enchantment.minecraft.aqua_affinity", "enchantment.minecraft.respiration" ->
                    List.of(stackOf(Items.IRON_HELMET));
            case "enchantment.minecraft.depth_strider", "enchantment.minecraft.feather_falling",
                    "enchantment.minecraft.frost_walker", "enchantment.minecraft.soul_speed" ->
                    List.of(stackOf(Items.IRON_BOOTS));
            case "enchantment.minecraft.swift_sneak" ->
                    List.of(stackOf(Items.IRON_LEGGINGS));
            case "enchantment.minecraft.protection", "enchantment.minecraft.fire_protection",
                    "enchantment.minecraft.blast_protection", "enchantment.minecraft.projectile_protection",
                    "enchantment.project_babylon_materials.magic_resistance" ->
                    List.of(stackOf(Items.IRON_HELMET), stackOf(Items.IRON_CHESTPLATE), stackOf(Items.IRON_LEGGINGS), stackOf(Items.IRON_BOOTS));
            case "enchantment.minecraft.binding_curse" ->
                    List.of(stackOf(Items.IRON_HELMET), stackOf(Items.IRON_CHESTPLATE), stackOf(Items.IRON_LEGGINGS), stackOf(Items.IRON_BOOTS));
            case "enchantment.minecraft.thorns" ->
                    List.of(stackOf(Items.IRON_CHESTPLATE));
            case "enchantment.minecraft.sharpness", "enchantment.minecraft.smite", "enchantment.minecraft.bane_of_arthropods",
                    "enchantment.minecraft.fire_aspect", "enchantment.minecraft.knockback",
                    "enchantment.minecraft.looting", "enchantment.minecraft.sweeping" ->
                    List.of(stackOf(Items.IRON_SWORD));
            case "enchantment.minecraft.efficiency", "enchantment.minecraft.fortune", "enchantment.minecraft.silk_touch" ->
                    List.of(stackOf(Items.IRON_PICKAXE), stackOf(Items.IRON_AXE), stackOf(Items.IRON_SHOVEL), stackOf(Items.IRON_HOE));
            case "enchantment.minecraft.power", "enchantment.minecraft.flame",
                    "enchantment.minecraft.infinity", "enchantment.minecraft.punch" ->
                    List.of(stackOf(Items.BOW));
            case "enchantment.minecraft.multishot", "enchantment.minecraft.piercing",
                    "enchantment.minecraft.quick_charge" ->
                    List.of(stackOf(Items.CROSSBOW));
            case "enchantment.minecraft.impaling", "enchantment.minecraft.loyalty",
                    "enchantment.minecraft.channeling", "enchantment.minecraft.riptide" ->
                    List.of(stackOf(Items.TRIDENT));
            case "enchantment.minecraft.luck_of_the_sea", "enchantment.minecraft.lure" ->
                    List.of(stackOf(Items.FISHING_ROD));
            case "enchantment.minecraft.mending", "enchantment.minecraft.unbreaking",
                    "enchantment.minecraft.vanishing_curse" ->
                    List.of(stackOf(Items.IRON_SWORD), stackOf(Items.IRON_CHESTPLATE), stackOf(Items.IRON_PICKAXE), stackOf(Items.BOW), stackOf(Items.CROSSBOW), stackOf(Items.TRIDENT));
            default -> List.of(stackOf(Items.ENCHANTED_BOOK));
        };
    }

    private static ItemStack stackOf(net.minecraft.world.item.Item item) {
        return new ItemStack(item);
    }
}