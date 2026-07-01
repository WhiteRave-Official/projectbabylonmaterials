package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ItemRarityHandler {
    private ItemRarityHandler() {
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        ItemStack crafted = event.getCrafting();
        if (ItemRarityHelper.ensureRarity(crafted, event.getEntity().getRandom())) {
            crafted.setTag(crafted.getTag());
        }
        EnchantmentSlotHelper.trimToSlotLimit(crafted);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide || event.player.tickCount % 20 != 0) {
            return;
        }

        Inventory inventory = event.player.getInventory();
        sanitizeStacks(inventory.items, event.player.getRandom());
        sanitizeStacks(inventory.armor, event.player.getRandom());
        sanitizeStacks(inventory.offhand, event.player.getRandom());

        ItemStack carried = event.player.containerMenu.getCarried();
        if (ItemRarityHelper.ensureRarity(carried, event.player.getRandom())) {
            event.player.containerMenu.setCarried(carried);
        }
        EnchantmentSlotHelper.trimToSlotLimit(carried);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        ItemStack stack = itemEntity.getItem();
        boolean changed = ItemRarityHelper.ensureRarity(stack, event.getLevel().random);
        changed |= EnchantmentSlotHelper.trimToSlotLimit(stack);
        if (changed) {
            itemEntity.setItem(stack);
        }
    }

    private static void sanitizeStacks(Iterable<ItemStack> stacks, net.minecraft.util.RandomSource random) {
        for (ItemStack stack : stacks) {
            ItemRarityHelper.ensureRarity(stack, random);
            EnchantmentSlotHelper.trimToSlotLimit(stack);
        }
    }
}