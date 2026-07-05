package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ItemRarityHandler {
    private ItemRarityHandler() {
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        ItemStack crafted = event.getCrafting();
        ItemRarityHelper.ensureRarity(crafted, event.getEntity().getRandom());
        EnchantmentSlotHelper.trimToSlotLimit(crafted);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 20 != 0) {
            return;
        }

        Inventory inventory = player.getInventory();
        sanitizeStacks(inventory.items, player.getRandom());
        sanitizeStacks(inventory.armor, player.getRandom());
        sanitizeStacks(inventory.offhand, player.getRandom());

        ItemStack carried = player.containerMenu.getCarried();
        if (ItemRarityHelper.ensureRarity(carried, player.getRandom())) {
            player.containerMenu.setCarried(carried);
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

    private static void sanitizeStacks(Iterable<ItemStack> stacks, RandomSource random) {
        for (ItemStack stack : stacks) {
            ItemRarityHelper.ensureRarity(stack, random);
            EnchantmentSlotHelper.trimToSlotLimit(stack);
        }
    }
}
