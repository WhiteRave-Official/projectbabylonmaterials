package com.rave.projectbabylonmaterials.gem;

import com.rave.projectbabylonmaterials.balance.PBMBalances;
import com.rave.projectbabylonmaterials.init.PBMDataComponents;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GemSlotHelper {
    public static final int MAX_GEM_SLOTS = 2;

    private GemSlotHelper() {
    }

    public static boolean supportsGemSlots(ItemStack stack) {
        return ItemRarityHelper.supportsSlottedItem(stack);
    }

    public static boolean hasGemTooltip(ItemStack stack) {
        int slotCount = getGemSlotCount(stack);
        int socketedCount = getSocketedGems(stack).size();
        return (supportsGemSlots(stack) || socketedCount > 0) && (slotCount > 0 || socketedCount > 0);
    }

    public static int getGemSlotCount(ItemStack stack) {
        Integer count = stack.get(PBMDataComponents.GEM_SLOT_COUNT.get());
        if (count == null) {
            return 0;
        }

        return Math.max(0, Math.min(maxGemSlots(), count));
    }

    public static boolean hasAnyGemSlots(ItemStack stack) {
        return getGemSlotCount(stack) > 0;
    }

    public static boolean hasAvailableGemSlot(ItemStack stack) {
        return getSocketedGems(stack).size() < getGemSlotCount(stack);
    }

    public static boolean canSocketGem(ItemStack targetStack, ItemStack gemStack) {
        if (!supportsGemSlots(targetStack) || targetStack.isEmpty() || gemStack.isEmpty() || !hasAvailableGemSlot(targetStack)) {
            return false;
        }

        Optional<GemType> gemTypeOptional = GemType.fromStack(gemStack);
        if (gemTypeOptional.isEmpty()) {
            return false;
        }

        for (GemApplication application : gemTypeOptional.get().getApplications()) {
            if (application.matches(targetStack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean socketGem(ItemStack targetStack, ItemStack gemStack) {
        if (!canSocketGem(targetStack, gemStack)) {
            return false;
        }

        ItemStack singleGem = gemStack.copy();
        singleGem.setCount(1);

        List<ItemStack> gems = new ArrayList<>(getSocketedGems(targetStack));
        gems.add(singleGem);
        targetStack.set(PBMDataComponents.SOCKETED_GEMS.get(), List.copyOf(gems));
        return true;
    }

    public static List<ItemStack> extractSocketedGems(ItemStack stack, int maxCount) {
        List<ItemStack> extractedGems = new ArrayList<>();
        if (stack.isEmpty() || maxCount <= 0) {
            return extractedGems;
        }

        List<ItemStack> gems = getSocketedGems(stack);
        if (gems.isEmpty()) {
            return extractedGems;
        }

        int extractCount = Math.min(maxCount, gems.size());
        for (int i = 0; i < extractCount; i++) {
            ItemStack gemStack = gems.get(i);
            if (!gemStack.isEmpty()) {
                extractedGems.add(gemStack);
            }
        }

        List<ItemStack> remaining = new ArrayList<>(gems.subList(extractCount, gems.size()));
        if (remaining.isEmpty()) {
            stack.remove(PBMDataComponents.SOCKETED_GEMS.get());
        } else {
            stack.set(PBMDataComponents.SOCKETED_GEMS.get(), List.copyOf(remaining));
        }

        return extractedGems;
    }

    public static List<ItemStack> getSocketedGems(ItemStack stack) {
        List<ItemStack> stored = stack.get(PBMDataComponents.SOCKETED_GEMS.get());
        if (stored == null) {
            return new ArrayList<>();
        }

        List<ItemStack> gems = new ArrayList<>();
        for (ItemStack gemStack : stored) {
            if (!gemStack.isEmpty()) {
                gems.add(gemStack.copy());
            }
        }

        return gems;
    }

    public static int getVisibleSlotCount(ItemStack stack) {
        return Math.max(getGemSlotCount(stack), Math.min(maxGemSlots(), getSocketedGems(stack).size()));
    }

    private static int maxGemSlots() {
        return PBMBalances.maxGemSlots(MAX_GEM_SLOTS);
    }
}
