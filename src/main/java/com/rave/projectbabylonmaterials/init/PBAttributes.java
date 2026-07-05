package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PBAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ProjectBabylonMaterials.MODID);

    public static final RegistryObject<Attribute> CRIT_CHANCE = ATTRIBUTES.register("crit_chance",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.crit_chance", 0.05D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> CRIT_DAMAGE = ATTRIBUTES.register("crit_damage",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.crit_damage", 0.50D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> HEALING_STRENGTH = ATTRIBUTES.register("healing_strength",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.healing_strength", 1.0D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> LIFESTEAL = ATTRIBUTES.register("lifesteal",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.lifesteal", 0.0D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> RANGED_DRAW_SPEED = ATTRIBUTES.register("ranged_draw_speed",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.ranged_draw_speed", 0.0D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> TOOL_EFFICIENCY = ATTRIBUTES.register("tool_efficiency",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.tool_efficiency", 0.0D, 0.0D, 1024.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> TOOL_DURABILITY = ATTRIBUTES.register("tool_durability",
            () -> new RangedAttribute("attribute.name.project_babylon_materials.tool_durability", 0.0D, 0.0D, 1024.0D)
                    .setSyncable(true));

    private PBAttributes() {
    }

    public static void register(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
        modBus.addListener(PBAttributes::onEntityAttributeModification);
    }

    private static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        for (EntityType<? extends net.minecraft.world.entity.LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, CRIT_CHANCE.get());
            event.add(entityType, CRIT_DAMAGE.get());
            event.add(entityType, HEALING_STRENGTH.get());
            event.add(entityType, LIFESTEAL.get());
            event.add(entityType, RANGED_DRAW_SPEED.get());
            event.add(entityType, TOOL_EFFICIENCY.get());
            event.add(entityType, TOOL_DURABILITY.get());
        }
    }
}
