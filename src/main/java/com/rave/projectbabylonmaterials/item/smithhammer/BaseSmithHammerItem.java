package com.rave.projectbabylonmaterials.item.smithhammer;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseSmithHammerItem extends PickaxeItem {
    private final float reducedMiningSpeed;

    protected BaseSmithHammerItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, int durability, float reducedMiningSpeed) {
        // 1.21.1: PickaxeItem(Tier, int, float, Properties) is gone. Attack damage/speed
        // modifiers now live on the ItemAttributeModifiers data component via Properties.attributes(...).
        super(tier, new Item.Properties()
                .durability(durability)
                .attributes(DiggerItem.createAttributes(tier, attackDamageModifier, attackSpeedModifier)));
        this.reducedMiningSpeed = reducedMiningSpeed;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float base = super.getDestroySpeed(stack, state);
        return base > 1.0F ? this.reducedMiningSpeed : base;
    }
}
