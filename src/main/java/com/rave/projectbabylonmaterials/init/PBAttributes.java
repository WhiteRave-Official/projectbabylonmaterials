package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PBAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, ProjectBabylonMaterials.MODID);

    public static final DeferredHolder<Attribute, Attribute> CRIT_CHANCE = ATTRIBUTES.register("crit_chance",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.crit_chance", 0.05D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> CRIT_DAMAGE = ATTRIBUTES.register("crit_damage",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.crit_damage", 0.50D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> RANGED_DRAW_SPEED = ATTRIBUTES.register("ranged_draw_speed",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.ranged_draw_speed", 0.0D, 0.0D, 1024.0D)
                    .setSyncable(true));

    private PBAttributes() {
    }

    public static void register(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
        modBus.addListener(PBAttributes::onEntityAttributeModification);
    }

    private static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, CRIT_CHANCE);
            event.add(entityType, CRIT_DAMAGE);
            event.add(entityType, RANGED_DRAW_SPEED);
        }
    }
}
