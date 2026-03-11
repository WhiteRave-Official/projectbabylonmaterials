package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.recipe.HammerPlatingRecipe;
import com.rave.projectbabylonmaterials.recipe.MagicalInfuserRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class PBMRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProjectBabylonMaterials.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ProjectBabylonMaterials.MODID);

    public static final RegistryObject<RecipeSerializer<MagicalInfuserRecipe>> MAGICAL_INFUSING_SERIALIZER =
            SERIALIZERS.register("magical_infusing", () -> MagicalInfuserRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<HammerPlatingRecipe>> HAMMER_PLATING_SERIALIZER =
            SERIALIZERS.register("hammer_plating", () -> new SimpleCraftingRecipeSerializer<>(HammerPlatingRecipe::new));

    public static final RegistryObject<RecipeType<MagicalInfuserRecipe>> MAGICAL_INFUSING_TYPE =
            RECIPE_TYPES.register("magical_infusing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return ProjectBabylonMaterials.MODID + ":magical_infusing";
                }
            });

    private PBMRecipes() {
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
