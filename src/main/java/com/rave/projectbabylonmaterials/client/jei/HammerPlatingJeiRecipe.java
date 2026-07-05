package com.rave.projectbabylonmaterials.client.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record HammerPlatingJeiRecipe(List<ItemStack> hammers, ItemStack ingot, ItemStack result) {
}
