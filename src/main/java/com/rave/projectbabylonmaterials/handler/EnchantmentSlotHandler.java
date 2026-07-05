package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class EnchantmentSlotHandler {
    private EnchantmentSlotHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 20 != 0) {
            return;
        }

        Inventory inventory = player.getInventory();
        sanitizeStacks(inventory.items);
        sanitizeStacks(inventory.armor);
        sanitizeStacks(inventory.offhand);

        ItemStack carried = player.containerMenu.getCarried();
        EnchantmentSlotHelper.trimToSlotLimit(carried);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        ItemStack stack = itemEntity.getItem();
        if (EnchantmentSlotHelper.trimToSlotLimit(stack)) {
            itemEntity.setItem(stack);
        }
    }

    private static void sanitizeStacks(Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            EnchantmentSlotHelper.trimToSlotLimit(stack);
        }
    }
}
