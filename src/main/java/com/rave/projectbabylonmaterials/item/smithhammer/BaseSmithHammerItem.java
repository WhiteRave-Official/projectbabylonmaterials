package com.rave.projectbabylonmaterials.item.smithhammer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseSmithHammerItem extends PickaxeItem {
    private final float reducedMiningSpeed;

    protected BaseSmithHammerItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, int durability, float reducedMiningSpeed) {
        super(tier, attackDamageModifier, attackSpeedModifier, new Item.Properties().durability(durability));
        this.reducedMiningSpeed = reducedMiningSpeed;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float base = super.getDestroySpeed(stack, state);
        return base > 1.0F ? this.reducedMiningSpeed : base;
    }
}
