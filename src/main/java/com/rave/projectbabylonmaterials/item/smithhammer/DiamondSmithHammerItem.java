package com.rave.projectbabylonmaterials.item.smithhammer;

import net.minecraft.world.item.Tiers;

public class DiamondSmithHammerItem extends BaseSmithHammerItem {
    private static final int ATTACK_DAMAGE_MODIFIER = 3;
    private static final float ATTACK_SPEED_MODIFIER = -2.9F;
    private static final int DURABILITY = 1561;
    private static final float REDUCED_MINING_SPEED = Tiers.IRON.getSpeed();

    public DiamondSmithHammerItem() {
        super(Tiers.DIAMOND, ATTACK_DAMAGE_MODIFIER, ATTACK_SPEED_MODIFIER, DURABILITY, REDUCED_MINING_SPEED);
    }
}
