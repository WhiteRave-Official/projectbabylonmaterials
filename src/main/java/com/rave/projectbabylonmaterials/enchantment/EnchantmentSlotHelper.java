package com.rave.projectbabylonmaterials.enchantment;

import com.rave.projectbabylonmaterials.init.PBMDataComponents;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class EnchantmentSlotHelper {
    public static final int MAX_ENCHANTMENT_SLOTS = 4;
    public static final int NO_AVAILABLE_ENCHANTMENT_SLOTS_COST = -100;
    public static final int NO_ENCHANTMENT_SLOTS_COST = -101;

    private static final Comparator<Object2IntMap.Entry<Holder<Enchantment>>> BY_ID =
            Comparator.comparing(entry -> idOf(entry.getKey()));

    private EnchantmentSlotHelper() {
    }

    public static boolean supportsEnchantmentSlots(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return stack.is(Items.BOOK)
                || stack.is(Items.ENCHANTED_BOOK)
                || ItemRarityHelper.supportsRarity(stack)
                || getEnchantmentSlotCount(stack) > 0
                || getEnchantmentCount(stack) > 0;
    }

    public static boolean hasEnchantmentTooltip(ItemStack stack) {
        return supportsEnchantmentSlots(stack) && (getVisibleSlotCount(stack) > 0);
    }

    public static boolean hasAnyEnchantmentSlots(ItemStack stack) {
        return !isLimitedBySlots(stack) || getEnchantmentSlotCount(stack) > 0;
    }

    public static boolean hasAvailableSlot(ItemStack stack) {
        if (!isLimitedBySlots(stack)) {
            return true;
        }

        return getEnchantmentCount(stack) < getEnchantmentSlotCount(stack);
    }

    public static boolean wouldExceedSlotLimit(ItemStack stack) {
        if (!isLimitedBySlots(stack)) {
            return false;
        }

        return getEnchantmentCount(stack) > getEnchantmentSlotCount(stack);
    }

    public static int getEnchantmentSlotCount(ItemStack stack) {
        if (stack.is(Items.BOOK) || stack.is(Items.ENCHANTED_BOOK)) {
            return MAX_ENCHANTMENT_SLOTS;
        }

        Integer count = stack.get(PBMDataComponents.ENCHANT_SLOT_COUNT.get());
        if (count == null) {
            return 0;
        }

        return Math.max(0, Math.min(MAX_ENCHANTMENT_SLOTS, count));
    }

    public static int getVisibleSlotCount(ItemStack stack) {
        return Math.max(getEnchantmentSlotCount(stack), Math.min(MAX_ENCHANTMENT_SLOTS, getEnchantmentCount(stack)));
    }

    public static int getEnchantmentCount(ItemStack stack) {
        return getOrderedEnchantments(stack).size();
    }

    public static List<SlottedEnchantment> getOrderedEnchantments(ItemStack stack) {
        List<SlottedEnchantment> enchantments = new ArrayList<>();
        if (stack.isEmpty()) {
            return enchantments;
        }

        ItemEnchantments stored = stack.getOrDefault(getEnchantmentComponentType(stack), ItemEnchantments.EMPTY);
        List<Object2IntMap.Entry<Holder<Enchantment>>> entries = new ArrayList<>(stored.entrySet());
        entries.sort(BY_ID);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : entries) {
            enchantments.add(new SlottedEnchantment(entry.getKey(), entry.getIntValue()));
        }

        return enchantments;
    }

    public static boolean trimToSlotLimit(ItemStack stack) {
        if (!isLimitedBySlots(stack)) {
            return false;
        }

        DataComponentType<ItemEnchantments> componentType = getEnchantmentComponentType(stack);
        ItemEnchantments stored = stack.getOrDefault(componentType, ItemEnchantments.EMPTY);
        int limit = getEnchantmentSlotCount(stack);
        if (stored.size() <= limit) {
            return false;
        }

        List<Object2IntMap.Entry<Holder<Enchantment>>> entries = new ArrayList<>(stored.entrySet());
        entries.sort(BY_ID);

        ItemEnchantments.Mutable trimmed = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (int i = 0; i < limit; i++) {
            Object2IntMap.Entry<Holder<Enchantment>> entry = entries.get(i);
            trimmed.set(entry.getKey(), entry.getIntValue());
        }

        stack.set(componentType, trimmed.toImmutable());
        return true;
    }

    public static boolean shouldBlockEnchantingTable(ItemStack stack) {
        return ItemRarityHelper.supportsSlottedItem(stack) && getEnchantmentSlotCount(stack) <= 0;
    }

    private static boolean isLimitedBySlots(ItemStack stack) {
        return ItemRarityHelper.supportsRarity(stack) || stack.has(PBMDataComponents.ENCHANT_SLOT_COUNT.get());
    }

    private static DataComponentType<ItemEnchantments> getEnchantmentComponentType(ItemStack stack) {
        return stack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
    }

    private static String idOf(Holder<Enchantment> holder) {
        return holder.unwrapKey().map(key -> key.location().toString()).orElse("");
    }

    public record SlottedEnchantment(Holder<Enchantment> enchantment, int level) {
    }
}
