package com.rave.projectbabylonmaterials.item.smithhammer;

import net.minecraft.world.item.Tiers;

public class GoldenSmithHammerItem extends BaseSmithHammerItem {
    private static final int ATTACK_DAMAGE_MODIFIER = 1;
    private static final float ATTACK_SPEED_MODIFIER = -2.8F;
    private static final int DURABILITY = 96;
    private static final float REDUCED_MINING_SPEED = Tiers.WOOD.getSpeed();

    public GoldenSmithHammerItem() {
        super(Tiers.GOLD, ATTACK_DAMAGE_MODIFIER, ATTACK_SPEED_MODIFIER, DURABILITY, REDUCED_MINING_SPEED);
    }
}
