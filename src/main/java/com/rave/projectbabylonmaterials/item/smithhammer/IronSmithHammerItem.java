package com.rave.projectbabylonmaterials.item.smithhammer;

import net.minecraft.world.item.Tiers;

public class IronSmithHammerItem extends BaseSmithHammerItem {
    private static final int ATTACK_DAMAGE_MODIFIER = 2;
    private static final float ATTACK_SPEED_MODIFIER = -2.9F;
    private static final int DURABILITY = 420;
    private static final float REDUCED_MINING_SPEED = Tiers.STONE.getSpeed();

    public IronSmithHammerItem() {
        super(Tiers.IRON, ATTACK_DAMAGE_MODIFIER, ATTACK_SPEED_MODIFIER, DURABILITY, REDUCED_MINING_SPEED);
    }
}
