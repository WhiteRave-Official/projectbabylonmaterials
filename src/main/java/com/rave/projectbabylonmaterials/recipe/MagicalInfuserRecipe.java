package com.rave.projectbabylonmaterials.recipe;

import com.google.gson.JsonObject;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class MagicalInfuserRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient topIngredient;
    private final int topCount;
    private final Ingredient bottomIngredient;
    private final int bottomCount;
    private final ItemStack result;
    private final int craftTime;

    public MagicalInfuserRecipe(ResourceLocation id, Ingredient topIngredient, int topCount, Ingredient bottomIngredient, int bottomCount, ItemStack result, int craftTime) {
        this.id = id;
        this.topIngredient = topIngredient;
        this.topCount = topCount;
        this.bottomIngredient = bottomIngredient;
        this.bottomCount = bottomCount;
        this.result = result;
        this.craftTime = craftTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack topStack = container.getItem(1);
        ItemStack bottomStack = container.getItem(2);

        return this.topIngredient.test(topStack)
                && topStack.getCount() >= this.topCount
                && this.bottomIngredient.test(bottomStack)
                && bottomStack.getCount() >= this.bottomCount;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBMRecipes.MAGICAL_INFUSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PBMRecipes.MAGICAL_INFUSING_TYPE.get();
    }

    public int getTopCount() {
        return topCount;
    }

    public int getBottomCount() {
        return bottomCount;
    }

    public int getCraftTime() {
        return craftTime;
    }

    public static class Serializer implements RecipeSerializer<MagicalInfuserRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MagicalInfuserRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonObject topObject = GsonHelper.getAsJsonObject(json, "top");
            JsonObject bottomObject = GsonHelper.getAsJsonObject(json, "bottom");

            Ingredient topIngredient = Ingredient.fromJson(topObject);
            Ingredient bottomIngredient = Ingredient.fromJson(bottomObject);

            int topCount = GsonHelper.getAsInt(topObject, "count", 1);
            int bottomCount = GsonHelper.getAsInt(bottomObject, "count", 1);

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int craftTime = GsonHelper.getAsInt(json, "craft_time", 200);

            return new MagicalInfuserRecipe(recipeId, topIngredient, topCount, bottomIngredient, bottomCount, result, craftTime);
        }

        @Override
        public MagicalInfuserRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient topIngredient = Ingredient.fromNetwork(buffer);
            int topCount = buffer.readVarInt();

            Ingredient bottomIngredient = Ingredient.fromNetwork(buffer);
            int bottomCount = buffer.readVarInt();

            ItemStack result = buffer.readItem();
            int craftTime = buffer.readVarInt();

            return new MagicalInfuserRecipe(recipeId, topIngredient, topCount, bottomIngredient, bottomCount, result, craftTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MagicalInfuserRecipe recipe) {
            recipe.topIngredient.toNetwork(buffer);
            buffer.writeVarInt(recipe.topCount);

            recipe.bottomIngredient.toNetwork(buffer);
            buffer.writeVarInt(recipe.bottomCount);

            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.craftTime);
        }
    }
}