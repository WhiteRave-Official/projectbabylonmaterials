package com.rave.projectbabylonmaterials.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class MagicalInfuserRecipe implements Recipe<RecipeInput> {
    private final Ingredient topIngredient;
    private final int topCount;
    private final Ingredient bottomIngredient;
    private final int bottomCount;
    private final ItemStack result;
    private final int craftTime;

    public MagicalInfuserRecipe(Ingredient topIngredient, int topCount, Ingredient bottomIngredient, int bottomCount, ItemStack result, int craftTime) {
        this.topIngredient = topIngredient;
        this.topCount = topCount;
        this.bottomIngredient = bottomIngredient;
        this.bottomCount = bottomCount;
        this.result = result;
        this.craftTime = craftTime;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        ItemStack topStack = input.getItem(1);
        ItemStack bottomStack = input.getItem(2);

        return this.topIngredient.test(topStack)
                && topStack.getCount() >= this.topCount
                && this.bottomIngredient.test(bottomStack)
                && bottomStack.getCount() >= this.bottomCount;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.copy();
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

    public Ingredient getTopIngredient() {
        return topIngredient;
    }

    public int getBottomCount() {
        return bottomCount;
    }

    public Ingredient getBottomIngredient() {
        return bottomIngredient;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public int getCraftTime() {
        return craftTime;
    }

    public static class Serializer implements RecipeSerializer<MagicalInfuserRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        // TODO(port): recipe JSON schema changed from inline ingredient counts to flat sibling
        // fields ("top"/"bottom" are now pure ingredients, with "top_count"/"bottom_count" alongside);
        // datapack files are re-authored separately (Layer 7).
        private static final MapCodec<MagicalInfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("top").forGetter(recipe -> recipe.topIngredient),
                Codec.INT.optionalFieldOf("top_count", 1).forGetter(recipe -> recipe.topCount),
                Ingredient.CODEC.fieldOf("bottom").forGetter(recipe -> recipe.bottomIngredient),
                Codec.INT.optionalFieldOf("bottom_count", 1).forGetter(recipe -> recipe.bottomCount),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf("craft_time", 200).forGetter(recipe -> recipe.craftTime)
        ).apply(instance, MagicalInfuserRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, MagicalInfuserRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.topIngredient,
                ByteBufCodecs.VAR_INT, recipe -> recipe.topCount,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.bottomIngredient,
                ByteBufCodecs.VAR_INT, recipe -> recipe.bottomCount,
                ItemStack.STREAM_CODEC, recipe -> recipe.result,
                ByteBufCodecs.VAR_INT, recipe -> recipe.craftTime,
                MagicalInfuserRecipe::new
        );

        @Override
        public MapCodec<MagicalInfuserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MagicalInfuserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
