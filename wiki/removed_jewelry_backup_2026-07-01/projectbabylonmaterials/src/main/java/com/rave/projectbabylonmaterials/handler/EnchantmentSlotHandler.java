package com.rave.projectbabylonmaterials.handler;

import com.rave.projectbabylonmaterials.enchantment.EnchantmentSlotHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class EnchantmentSlotHandler {
    private EnchantmentSlotHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide || event.player.tickCount % 20 != 0) {
            return;
        }

        Inventory inventory = event.player.getInventory();
        sanitizeStacks(inventory.items);
        sanitizeStacks(inventory.armor);
        sanitizeStacks(inventory.offhand);

        ItemStack carried = event.player.containerMenu.getCarried();
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
