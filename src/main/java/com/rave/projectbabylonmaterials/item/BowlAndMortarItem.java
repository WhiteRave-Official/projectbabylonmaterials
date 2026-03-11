package com.rave.projectbabylonmaterials.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BowlAndMortarItem extends Item {
    public BowlAndMortarItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(this);
    }
}
