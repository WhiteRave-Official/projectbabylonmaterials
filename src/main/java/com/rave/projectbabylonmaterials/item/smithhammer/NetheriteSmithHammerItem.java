package com.rave.projectbabylonmaterials.item.smithhammer;

import net.minecraft.world.item.Tiers;

public class NetheriteSmithHammerItem extends BaseSmithHammerItem {
    private static final int ATTACK_DAMAGE_MODIFIER = 4;
    private static final float ATTACK_SPEED_MODIFIER = -3.0F;
    private static final int DURABILITY = 2031;
    private static final float REDUCED_MINING_SPEED = Tiers.DIAMOND.getSpeed();

    public NetheriteSmithHammerItem() {
        super(Tiers.NETHERITE, ATTACK_DAMAGE_MODIFIER, ATTACK_SPEED_MODIFIER, DURABILITY, REDUCED_MINING_SPEED);
    }
}
