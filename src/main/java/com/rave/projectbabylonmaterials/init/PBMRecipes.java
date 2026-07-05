package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.recipe.GemDustRecipe;
import com.rave.projectbabylonmaterials.recipe.HammerPlatingRecipe;
import com.rave.projectbabylonmaterials.recipe.MagicalInfuserRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PBMRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProjectBabylonMaterials.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ProjectBabylonMaterials.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MagicalInfuserRecipe>> MAGICAL_INFUSING_SERIALIZER =
            SERIALIZERS.register("magical_infusing", () -> MagicalInfuserRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<HammerPlatingRecipe>> HAMMER_PLATING_SERIALIZER =
            SERIALIZERS.register("hammer_plating", () -> new SimpleCraftingRecipeSerializer<>(HammerPlatingRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<GemDustRecipe>> GEM_DUST_SERIALIZER =
            SERIALIZERS.register("gem_dust", () -> new SimpleCraftingRecipeSerializer<>(GemDustRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<MagicalInfuserRecipe>> MAGICAL_INFUSING_TYPE =
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
